package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.util.DatabaseHelper;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.PurchaseLine;

/**
 * Created by adriano on 05/04/16.
 */
public class DAOPurchaseLine extends AbstractDAO<PurchaseLine> {
    private static DAOPurchaseLine instance;

    public static DAOPurchaseLine getInstance(Context context) {
        if (instance == null)
            instance = new DAOPurchaseLine(context);
        return instance;
    }

    private DAOPurchaseLine(Context context) {
        super(context, PurchaseLine.class, DatabaseHelper.PurchaseLine.TABLE, DatabaseHelper.PurchaseLine.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        PurchaseLine pl = (PurchaseLine) o;

        ContentValues values = new ContentValues();

        if (o.getId() != 0)
            values.put(DatabaseHelper.PurchaseLine._ID, pl.getId());

        values.put(DatabaseHelper.PurchaseLine.CATEGORY, pl.getCategory());
        values.put(DatabaseHelper.PurchaseLine.PRODUCT_NAME, pl.getProductName());
        values.put(DatabaseHelper.PurchaseLine.UNITARY_PRICE, pl.getQuantity().toString());
        values.put(DatabaseHelper.PurchaseLine.QUANTITY, pl.getQuantity().toString());
        values.put(DatabaseHelper.PurchaseLine.SUB_TOTAL, pl.getSubTotal().toString());

        if (pl.getProduct() != null && pl.getProduct().getId() != 0)
            values.put(DatabaseHelper.PurchaseLine._PRODUCT_ID, pl.getProduct().getId());

        if (pl.getPurchase() != null && pl.getPurchase().getId() != 0)
            values.put(DatabaseHelper.PurchaseLine._PURCHASE_ID, pl.getPurchase().getId());

        if (pl.getDateCreated() != null)
            values.put(DatabaseHelper.PurchaseLine.DATE_CREATED, pl.getDateCreated().toString());

        if (pl.getLastUpdated() != null)
            values.put(DatabaseHelper.PurchaseLine.LAST_UPDATED, pl.getLastUpdated().toString());

        return values;
    }
}
