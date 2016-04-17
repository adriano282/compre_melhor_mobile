package br.com.compremelhor.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.FreightResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseLineResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.dao.impl.DAOFreight;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.User;
import br.com.compremelhor.util.DatabaseHelper;

public class CartService {
    private static CartService instance;

    private DAOFreight daoFreight;
    private DAOPurchase daoPurchase;
    private DAOPurchaseLine daoItem;

    private PurchaseResource purchaseResource;
    private PurchaseLineResource itemResource;
    private FreightResource freightResource;

    private Purchase purchase;
    private Freight freight;

    private FreightSetup freightSetup;

    private Handler handler;

    private ProgressDialog progressDialog;

    private Context context;

    private int userId;
    private int partnerId;

    public static CartService getInstance(Context context, int userId, int partnerId) {
        if (instance == null) {
            instance = new CartService();
            instance.context = context;
            instance.daoItem = DAOPurchaseLine.getInstance(context);
            instance.daoPurchase = DAOPurchase.getInstance(context);
            instance.daoFreight = DAOFreight.getInstance(context);

            instance.purchaseResource = new PurchaseResource("purchases", context);
            instance.handler = new Handler();
            instance.userId = userId;
            instance.partnerId = partnerId;
            instance.loadCurrentPurchase();

            if (instance.purchase != null) {
                instance.itemResource = new PurchaseLineResource(
                        "purchases/" + instance.purchase.getId() +"/lines", context);

                instance.freightResource = new FreightResource(
                        "purchases/" + instance.purchase.getId() + "/freight", context);

            }
        }
        return instance;
    }

    public boolean addItem(final PurchaseLine item) {
        if (item.getPurchase() == null) { item.setPurchase(purchase);}

        showProgressDialog(context.getString(R.string.dialog_content_text_putting_item_on_cart));

        AsyncTask<Void, Void, Boolean> request = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (getItems().contains(item))
                    return false;

                ResponseServer<PurchaseLine> response = itemResource.createResource(item);
                if (response.hasErrors()) {
                    log(response);
                    return false;
                }
                item.setId(response.getEntity().getId());
                if (daoItem.insert(item) == -1)
                    throw new RuntimeException("An error occurred during the try of save on database");

                TreeSet<PurchaseLine> lines = getItems();
                lines.add(item);
                purchase.setItems(lines);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                refreshSubTotal();
                progressDialog.dismiss();
            }
        };

        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error occurred during editing an item on cart: " + e);
        }
    }

    public boolean editItem(final PurchaseLine item) {
        showProgressDialog(context.getString(R.string.dialog_content_text_changing_item_on_cart));

        AsyncTask<Void, Void, Boolean> request = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (item.getPurchase() == null) item.setPurchase(purchase);
                ResponseServer<PurchaseLine> response = itemResource.updateResource(item);
                if (response.hasErrors()) {
                    log(response);
                    return false;
                }

                if (daoItem.insertOrUpdate(item) == -1) {
                    return false;
                }

                getItems().remove(item);
                getItems().add(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                refreshSubTotal();
                progressDialog.dismiss();
            }
        };

        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error occurred during editing an item on cart: " + e);
        }
    }

    public boolean removeFreight() {

        Freight freight = daoFreight
                .findByAttribute(DatabaseHelper.Freight._PURCHASE_ID, String.valueOf(purchase.getId()));

        if (freight == null) return true;

        ResponseServer<Freight> responseServer = freightResource.deleteResource(freight);
        if (responseServer.hasErrors()) {
            log(responseServer);
        }

        daoFreight.delete(freight.getId());

        boolean result = daoFreight.find(freight.getId()) == null;
        purchase.setFreight(null);

        return  result;
    }

    public boolean persistFreight() {
        freight.setPurchase(purchase);

        HashMap<String, String> params = new HashMap<>();
        params.put("purchase.id", String.valueOf(purchase.getId()));

        Freight freightOnServer = freightResource.getResource(params);

        if (freightOnServer != null) {
            freight.setId(freightOnServer.getId());
            ResponseServer<Freight> responseServer = freightResource.updateResource(freight);
            if (responseServer.hasErrors()) {
                log(responseServer);
                return false;
            }
            if (daoFreight.insertOrUpdate(freight) != -1) {
                purchase.setFreight(freight);
                return true;
            }

        } else {
            ResponseServer<Freight> responseServer = freightResource.createResource(freight);
            if (responseServer.hasErrors()) {
                log(responseServer);
                return false;
            }
            freight.setId(responseServer.getEntity().getId());
            if (daoFreight.insert(freight) != -1) {
                purchase.setFreight(freight);
                return true;
            }
        }
        return false;
    }

    public boolean removeItem(final PurchaseLine item) {
//        showProgressDialog(context.getString(R.string.dialog_content_text_removing_item_on_cart));

        AsyncTask<Void, Void, Boolean> request = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (!purchase.getItems().remove(item)) return false;

                ResponseServer<PurchaseLine> responseServer = itemResource.deleteResource(item);
                if (responseServer.hasErrors()) {
                    log(responseServer);
                    return false;
                }
                daoItem.delete(item.getId());
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                refreshSubTotal();
  //              progressDialog.dismiss();
            }
        };

        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error occurred during editing an item on cart: " + e);
        }
    }

    public TreeSet<PurchaseLine> getItems() {
        TreeSet<PurchaseLine> items = purchase.getItems();

        if (items == null)
            items = new TreeSet<>();

        return items;
    }

    private void loadCurrentPurchase() {
        showProgressDialog(context.getString(R.string.dialog_content_text_starting_item_on_cart));
        purchase = daoPurchase.getOpenedPurchase();

        if (purchase == null) {
            final Establishment establishment = new Establishment();
            establishment.setId(partnerId);

            AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("user.id", String.valueOf(userId));
                    param.put("status", Purchase.Status.OPENED.toString());

                    purchase = purchaseResource.getResource(param);

                    if (purchase == null) {
                        purchase = new Purchase();
                        User user = new User();
                        user.setId(userId);
                        purchase.setEstablishment(establishment);
                        purchase.setUser(user);
                        purchase.setStatus(Purchase.Status.OPENED);
                        purchase.setDateCreated(Calendar.getInstance());
                        purchase.setLastUpdated(Calendar.getInstance());

                        ResponseServer<Purchase> responseServer = purchaseResource.createResource(purchase);
                        if (responseServer.hasErrors()) {
                            Log.d("REST API", "Response STATUS CODE: " + responseServer.getStatusCode());
                            for (String error : responseServer.getErrors()) {
                                Log.d("REST API", "Error: " + error);
                            }
                            throw new RuntimeException("Error on Server");
                        }

                        purchase.setId(responseServer.getEntity().getId());
                        daoPurchase.insert(purchase);
                        progressDialog.dismiss();
                        return null;
                    }

                    purchase.setEstablishment(establishment);
                    daoPurchase.insert(purchase);
                    progressDialog.dismiss();
                    return null;
                }
            };
            try {
                request.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            progressDialog.dismiss();
        }
    }

    private void refreshSubTotal() {
        BigDecimal total = BigDecimal.valueOf(0.0);
        for (PurchaseLine line : getItems()) {
            total.add(line.getSubTotal());
        }
        instance.purchase.setTotalValue(total);
    }

    public Purchase getPurchase() {
        return purchase == null ?
                new Purchase() : purchase;
    }


    private void log(ResponseServer<? extends EntityModel> response) {
        Log.d("REST API", "Response Status: " + response.getStatusCode());
        for (String error : response.getErrors()) {
            Log.d("REST API", "Error: " + error);
        }

    }


    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(context, context.getString(R.string.dialog_header_wait), message, true, false);
    }

    public Freight getFreight() {
        freight = purchase.getFreight();
        return freight;
    }

    public void setFreight(Freight freight) {
        purchase.setFreight(freight);
        getFreight();
    }

    public FreightSetup getFreightSetup() {
        if (getFreight() != null && getFreight().getFreightSetup() != null)
            freightSetup = getFreight().getFreightSetup();

        return freightSetup;
    }

    public void loadCurrentFreight() {
        if (getFreight() == null)
            setFreight(new Freight());
    }

    public void setFreightSetup(FreightSetup freightSetup) {
        if (getFreight() != null) {
            freight.setFreightSetup(freightSetup);
            getFreightSetup();
        }
    }

}
