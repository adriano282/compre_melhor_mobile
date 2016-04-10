package br.com.compremelhor.service;

import android.content.Context;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TreeSet;

import br.com.compremelhor.dao.DAOPurchase;
import br.com.compremelhor.dao.DAOPurchaseLine;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;

/**
 * Created by adriano on 05/04/16.
 */
public class CartService {

    private static CartService instance;

    private DAOPurchase daoPurchase;
    private DAOPurchaseLine daoItem;
    private Purchase purchase;

    public static CartService getInstance(Context context) {
        if (instance == null)
            instance = new CartService();

        instance.daoItem = DAOPurchaseLine.getInstance(context);
        instance.daoPurchase = DAOPurchase.getInstance(context);
        instance.loadCurrentPurchase();

        return instance;
    }

    public boolean addItem(PurchaseLine item) {
        if (item.getPurchase() == null) { item.setPurchase(purchase);}

        if (daoItem.insertOrUpdate(item) == -1)
            throw new RuntimeException("An error occurred during the try of save on database");

        boolean r = purchase.getItems().add(item);
        refreshSubTotal();
        return r;
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
        purchase = daoPurchase.getOpenedPurchase();
        if (purchase == null) {
            
            Establishment establishment = new Establishment();
            establishment.setName("SuperMercado da Gente");

            purchase = new Purchase();
            purchase.setEstablishment(establishment);
            purchase.setStatus(Purchase.Status.OPENED);
            purchase.setDateCreated(Calendar.getInstance());
            purchase.setLastUpdated(Calendar.getInstance());

            daoPurchase.insertOrUpdate(purchase);
        }
    }

    private void refreshSubTotal() {
        BigDecimal total = BigDecimal.valueOf(0.0);
        for (PurchaseLine line : purchase.getItems()) {
                total.add(line.getSubTotal());
        }
        instance.purchase.setTotalValue(total);
    }

}
