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
import br.com.compremelhor.api.integration.resource.impl.PurchaseLineResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.dao.DAOPurchase;
import br.com.compremelhor.dao.DAOPurchaseLine;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.User;

public class CartService {
    private static CartService instance;

    private DAOPurchase daoPurchase;
    private DAOPurchaseLine daoItem;

    private PurchaseResource purchaseResource;
    private PurchaseLineResource itemResource;

    private Purchase purchase;

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

            instance.purchaseResource = new PurchaseResource("purchases", context);
            instance.handler = new Handler();
            instance.userId = userId;
            instance.partnerId = partnerId;
            instance.loadCurrentPurchase();

            if (instance.purchase != null) {
                instance.itemResource = new PurchaseLineResource(
                        "purchases/" + instance.purchase.getId() +"/lines", context);
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
                if (!getItems().add(item)) return false;

                ResponseServer<PurchaseLine> response = itemResource.createResource(item);
                if (response.hasErrors()) {
                    log(response);
                    return false;
                }
                item.setId(response.getEntity().getId());
                if (daoItem.insert(item) == -1)
                    throw new RuntimeException("An error occurred during the try of save on database");

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
                    purchase.setEstablishment(establishment);

                    if (purchase == null) {
                        purchase = new Purchase();
                        User user = new User();
                        user.setId(userId);
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
                        daoPurchase.insertOrUpdate(purchase);
                        progressDialog.dismiss();
                        return null;
                    }

                    daoPurchase.insert(purchase);
                    progressDialog.dismiss();
                    return null;
                }
            };
            request.execute();
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

    private void log(ResponseServer<PurchaseLine> response) {
        Log.d("REST API", "Response Status: " + response.getStatusCode());
        for (String error : response.getErrors()) {
            Log.d("REST API", "Error: " + error);
        }

    }
    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(context, context.getString(R.string.wait_header_dialog), message, true, false);
    }
}
