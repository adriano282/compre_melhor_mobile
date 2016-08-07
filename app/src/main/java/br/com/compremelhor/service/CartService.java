package br.com.compremelhor.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.FreightResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseLineResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.api.integration.resource.impl.SKUResource;
import br.com.compremelhor.api.integration.resource.impl.SyncResource;
import br.com.compremelhor.dao.impl.DAOFreight;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.model.FreightType;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.Sync;
import br.com.compremelhor.model.User;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.util.helper.dialog.ProgressDialogHelper;

public class CartService implements Serializable {
    private static CartService instance;
    private static final String TAG = "cartService";

    private DAOFreight daoFreight;
    private DAOPurchase daoPurchase;
    private DAOPurchaseLine daoItem;

    private PurchaseResource purchaseResource;
    private PurchaseLineResource itemResource;
    private FreightResource freightResource;
    private SKUResource skuResource;

    private Purchase purchase;
    private Freight freight;
    private FreightType freightType;

    private Handler handler;
    transient private Context context;

    private int userId;
    private int partnerId;

    public static void invalidateInstance() {
        instance = null;
    }

    public static CartService getInstance(Context context, int userId, int partnerId) {
        Log.d(TAG, "getInstance");
        boolean wasItNull = false;
        if (instance == null) {
            Log.d(TAG, "instance is null");
            instance = new CartService();
            wasItNull = true;
            instance.userId = userId;
            instance.partnerId = partnerId;
            instance.handler = new Handler();
        }

        instance.context = context;
        instance.daoItem = DAOPurchaseLine.getInstance(context);
        instance.daoPurchase = DAOPurchase.getInstance(context);
        instance.daoFreight = DAOFreight.getInstance(context);
        instance.purchaseResource = new PurchaseResource("purchases", context);
        instance.skuResource = new SKUResource(context);

        if (wasItNull) {
            instance.loadCurrentPurchase();
        }

        if (instance.purchase != null) {
            instance.itemResource = new PurchaseLineResource(
                    "purchases/" + instance.purchase.getId() +"/lines", context);

            instance.freightResource = new FreightResource(
                    "purchases/" + instance.purchase.getId() + "/freight", context);
        }

        return instance;
    }

    public List<PurchaseLine> getExpiredItems(final boolean delete) {
        Log.d(TAG, "getExpiredItems");
        final SyncResource syncResource = new SyncResource(context);

        AsyncTask<Void, Void, List<PurchaseLine>> request = new AsyncTask<Void, Void, List<PurchaseLine>>() {
            @Override
            protected List<PurchaseLine> doInBackground(Void... p) {

                Map<String, String> params = new HashMap<>();
                params.put("mobileUserIdRef", String.valueOf(userId));
                params.put("entityName", "purchaseLine");

                List<PurchaseLine> lines = new ArrayList<>();

                List<Sync> syncs = syncResource.getAllResources(params);
                for (Sync c : syncs) {
                    PurchaseLine line = daoItem.find(c.getEntityId());
                    lines.add(line);

                    if (delete) {
                        daoItem.delete(line.getId());
                        syncResource.deleteResource(c);
                    }
                }
                return lines;
            }
        };

        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addItem(final PurchaseLine item) {
        Log.d(TAG, "addItem");
        if (item.getPurchase() == null) { item.setPurchase(purchase);}

        ProgressDialogHelper
                .getInstance(context)
                .setMessage(context.getString(R.string.dialog_content_text_putting_item_on_cart))
                .showWaitProgressDialog();

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
                ProgressDialogHelper.dismissProgressDialog();
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

    public void setFreightType(FreightType freightType) {
        Log.d(TAG, "setFreightType");
        this.freightType = freightType;
    }

    public FreightType getFreightType() {
        Log.d(TAG, "getFreightType");
        return freightType; }

    public PurchaseLine getItem(int itemId) {
        return DAOPurchaseLine.getInstance(context).find(itemId);
    }

    public Product getProduct(String code) {
        return skuResource.getResource("code", code);
    }

    public boolean containsProduct(String code) {
        for (PurchaseLine line : getItems()) {
            line = daoItem.find(line.getId());

            if (line != null
                    && line.getProductCode() != null
                    && line.getProductCode().equals(code))
               return true;
        }
        return false;
    }

    public Product getProduct(int itemId) {
        PurchaseLine line;
        if ((line = getItem(itemId)) != null)
            return skuResource.getResource(line.getProduct().getId());
        return null;
    }

    public boolean editItem(final PurchaseLine item) {
        Log.d(TAG, "editItem");
        ProgressDialogHelper
                .getInstance(context)
                .setMessage(context.getString(R.string.dialog_content_text_changing_item_on_cart))
                .showWaitProgressDialog();

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
                ProgressDialogHelper.dismissProgressDialog();
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

    public boolean persistFreight() {
        Log.d(TAG, "persistFreight");
        freight.setPurchase(purchase);
        freight.setFreightTypeId(freightType.getId());

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

    public boolean removeItem(int itemId) {
        Log.d(TAG, "removeItem");

        handler.post(new Runnable() {
            @Override
            public void run() {
                ProgressDialogHelper
                        .getInstance(context)
                        .setMessage(context.getString(R.string.dialog_content_text_removing_item_on_cart))
                        .showWaitProgressDialog();
            }
        });

        final PurchaseLine item = daoItem.find(itemId);
        if (item == null) return false;

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
                ProgressDialogHelper.dismissProgressDialog();
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
        Log.d(TAG, "getItems");
        TreeSet<PurchaseLine> items = purchase.getItems();

        if (items == null)
            items = new TreeSet<>();

        return items;
    }

    public void loadCurrentPurchase() {
        Log.d(TAG, "loadCurrentPurchase");
        ProgressDialogHelper
                .getInstance(context)
                .setMessage(context.getString(R.string.dialog_content_text_starting_item_on_cart))
                .showWaitProgressDialog();

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


                        ProgressDialogHelper.dismissProgressDialog();
                        return null;
                    }


                    purchase.setEstablishment(establishment);
                    purchase.setFreight(daoFreight.findByAttribute(DatabaseHelper.Freight._PURCHASE_ID, String.valueOf(purchase.getId())));
                    daoPurchase.insert(purchase);
                    refreshSubTotal();
                    ProgressDialogHelper.dismissProgressDialog();
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
            ProgressDialogHelper.dismissProgressDialog();
        }

        if (purchase.getTotalValue().doubleValue() == 0.0 ) refreshSubTotal();
    }

    public boolean startTransaction(boolean showWaitingDialog) {
        Log.d(TAG, "startTransaction");
        if (showWaitingDialog) {
        }


        getFreight();
        purchase.setStatus(Purchase.Status.STARTED_TRANSACTION);

        AsyncTask<Void, Void, Boolean> request = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                ResponseServer<Purchase> response = purchaseResource.updateResource(purchase);

                if (response.hasErrors()) {
                    log(response);
                    return false;
                }

                if (daoPurchase.insertOrUpdate(purchase) == -1) {
                    Log.i(TAG, "Error while updating the purchase on database");
                    return false;
                }
                return true;
            }
        };

        try {
            boolean result = request.execute().get();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ProgressDialogHelper.dismissProgressDialog();
                }
            });

            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closePurchase(boolean showStatusMessage) {
        Log.d(TAG, "closePurchase");
        getFreight();
        purchase.setStatus(Purchase.Status.PAID);

        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ResponseServer<Purchase> response = purchaseResource.updateResource(purchase);

                if (response.hasErrors()) {
                    log(response);
                    return null;
                }

                if (daoPurchase.insertOrUpdate(purchase) == -1) {
                    Log.i(TAG, "Error while updating the purchase on database");
                    return null;
                }
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
    }

    private void refreshSubTotal() {Log.d(TAG, "refreshSubTotal");

        BigDecimal total = BigDecimal.valueOf(0.0);
        for (PurchaseLine line : getItems()) {
            total = total.add(line.getSubTotal());
        }
        purchase.setTotalValue(total);
    }

    public Purchase getPurchase() { Log.d(TAG, "getPurchase");
        return purchase == null ?
                new Purchase() : purchase;
    }


    private void log(ResponseServer<? extends EntityModel> response) {
        Log.d("REST API", "Response Status: " + response.getStatusCode());
        for (String error : response.getErrors()) {
            Log.d("REST API", "Error: " + error);
        }

    }

    public void persistFreightInBackground() { Log.d(TAG, "persistFreightInBackground");
        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... params) {

                if (getFreight() == null
                        || getFreight().getVersion() == 0) return null;

                persistFreight();
                getFreight().setVersion(0);
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
    }

    public Freight getFreight() {
        Log.d(TAG, "getFreight");
        freight = purchase.getFreight();
        return freight;
    }

    public void setFreight(Freight freight) {
        Log.d(TAG, "setFreight");
        purchase.setFreight(freight);
        this.freight = freight;
    }

    public FreightSetup getFreightSetupFromDB() {
        Log.d(TAG, "getFreightSetupFromDB");

        if (purchase == null) return null;

        if (purchase.getFreight() == null) {
            purchase.setFreight(daoFreight.findByAttribute(DatabaseHelper.Freight._PURCHASE_ID, String.valueOf(purchase.getId())));

            return purchase.getFreight() != null ?
                    purchase.getFreight().getFreightSetup() :
                    null;
        }

        return purchase.getFreight().getFreightSetup();
    }

    public FreightSetup getFreightSetup() {
        Log.d(TAG, "getFreightSetup");
        Freight f = getFreight();

        if (f != null)
            return f.getFreightSetup();

        return null;
    }

    public FreightSetup getFreightSetup(boolean tryFromDatabase) {
        Log.d(TAG, "getFreightSetup(boolean)");
        if (tryFromDatabase)
            return getFreightSetupFromDB();
        return null;
    }

    public void setFreightSetup(FreightSetup freightSetup) {
        Log.d(TAG, "setFreightSetup");
        if (getFreight() != null) {
            freight.setFreightSetup(freightSetup);
            getFreightSetup();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
