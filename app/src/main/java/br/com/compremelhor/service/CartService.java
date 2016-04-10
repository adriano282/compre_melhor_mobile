package br.com.compremelhor.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.PurchaseLineResource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.dao.DAOEstablishment;
import br.com.compremelhor.dao.DAOPurchase;
import br.com.compremelhor.dao.DAOPurchaseLine;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 05/04/16.
 */
public class CartService {

    private static CartService instance;

    private DAOPurchase daoPurchase;
    private DAOPurchaseLine daoItem;
    private DAOEstablishment daoEstablishment;

    private PurchaseResource purchaseResource;
    private PurchaseLineResource itemResource;

    private Handler handler;

    private ProgressDialog progressDialog;
    private Context context;
    private int userId;
    private Purchase purchase;

    public static CartService getInstance(Context context, int userId) {
        if (instance == null)
            instance = new CartService();

        instance.context = context;
        instance.daoItem = DAOPurchaseLine.getInstance(context);
        instance.daoPurchase = DAOPurchase.getInstance(context);
        instance.daoEstablishment = DAOEstablishment.getInstance(context);

        instance.purchaseResource = new PurchaseResource("purchases", context);
        instance.handler = new Handler();
        instance.userId = userId;
        instance.loadCurrentPurchase();

        if (instance.purchase != null) {
            instance.itemResource = new PurchaseLineResource(
                    "purchases/" + instance.purchase.getId() +"/lines", context);
        }
        return instance;
    }

    public boolean addItem(final PurchaseLine item) {
        if (item.getPurchase() == null) { item.setPurchase(purchase);}

        AsyncTask<Void, Void, Boolean> request = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (!getItems().add(item)) {
                    progressDialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "Seu item não foi inserido, pois o mesmo já está em seu carrinho.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog("Inserindo item no carrinho...");
                    }
                });

                ResponseServer<PurchaseLine> response = itemResource.createResource(item);

                if (response.hasErrors()) {
                    Log.d("REST API", "Response Status: " + response.getStatusCode());
                    for (String error : response.getErrors()) {
                        Log.d("REST API", "Error: " + error);
                    }
                    progressDialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "Seu item não foi inserido, pois o mesmo já está em seu carrinho.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }

                if (daoItem.insertOrUpdate(item) == -1)
                    throw new RuntimeException("An error occurred during the try of save on database");

                refreshSubTotal();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Item inserido com sucesso!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        };
        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }

    public boolean removeItem(PurchaseLine item) {
        daoItem.delete(item.getId());
        boolean r = purchase.getItems().remove(item);
        refreshSubTotal();
        return r;
    }

    public TreeSet<PurchaseLine> getItems() {
        TreeSet<PurchaseLine> items = purchase.getItems();

        if (items == null)
            items = new TreeSet<>();

        return items;
    }

    private void loadCurrentPurchase() {
        showProgressDialog("Iniciando carrinho atual...");
        purchase = daoPurchase.getOpenedPurchase();
        if (purchase == null) {

            final Establishment establishment = new Establishment();
            establishment.setName("SuperMercado da Gente");


            if (daoEstablishment.findByAttribute("name", establishment.getName()) == null) {
                daoEstablishment.insertOrUpdate(establishment);
            }

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
                        }
                    }
                    purchase.setEstablishment(establishment);
                    daoPurchase.insert(purchase);
                    progressDialog.dismiss();
                    return null;
                }
            };
            request.execute();
        }
    }

    private void refreshSubTotal() {
        BigDecimal total = BigDecimal.valueOf(0.0);
        for (PurchaseLine line : purchase.getItems()) {
                total.add(line.getSubTotal());
        }
        instance.purchase.setTotalValue(total);
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(context, context.getString(R.string.wait_header_dialog), message, true, false);
    }
}
