package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import java.util.Date;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.util.DatabaseHelper;

public class DAOPurchase extends AbstractDAO<Purchase> {
    private static DAOPurchase instance;

    public static DAOPurchase getInstance(Context context) {
        if (instance == null)
            instance = new DAOPurchase(context);

        return instance;
    }


    private DAOPurchase(Context context) { super(context, Purchase.class, DatabaseHelper.Purchase.TABLE, DatabaseHelper.Purchase.COLUMNS); }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        Purchase purchase = (Purchase) o;
        ContentValues values = new ContentValues();

        if (o.getId() != 0)
            values.put(DatabaseHelper.Purchase._ID, o.getId());


        values.put(DatabaseHelper.Purchase.STATUS, purchase.getStatus().toString());

        if (purchase.getTotalValue() != null)
            values.put(DatabaseHelper.Purchase.TOTAL_VALUE, purchase.getTotalValue().toString());
        else
            values.put(DatabaseHelper.Purchase.TOTAL_VALUE, "0.0");

        if (purchase.getUser() != null)
            values.put(DatabaseHelper.Purchase._USER_ID, purchase.getUser().getId());

        if (purchase.getEstablishment() != null)
            values.put(DatabaseHelper.Purchase._ESTABLISHMENT_ID, purchase.getEstablishment().getId());

        if (purchase.getDateCreated() != null)
            values.put(DatabaseHelper.Purchase.DATE_CREATED, purchase.getDateCreated().getTime().getTime());
        else {
            values.put(DatabaseHelper.Purchase.DATE_CREATED, (new Date()).getTime());
        }

        if (purchase.getLastUpdated() != null)
            values.put(DatabaseHelper.Purchase.LAST_UPDATED, purchase.getLastUpdated().getTime().getTime());
        else {
            values.put(DatabaseHelper.Purchase.LAST_UPDATED, (new Date()).getTime());
        }
        return values;
    }

    public Purchase getOpenedPurchase() {
        return findByAttribute(DatabaseHelper.Purchase.STATUS, Purchase.Status.OPENED.toString());
    }
}
