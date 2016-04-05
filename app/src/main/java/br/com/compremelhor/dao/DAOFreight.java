package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Freight;

public class DAOFreight extends AbstractDAO<Freight> {
    private static DAOFreight instance;

    public static DAOFreight getInstance(Context context) {
        if (instance == null)
            instance = new DAOFreight(context);

        return instance;
    }

    private DAOFreight(Context context) {
        super(context, Freight.class, DatabaseHelper.Freight.TABLE, DatabaseHelper.Freight.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        Freight f = (Freight) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Freight._ID, f.getId());
        values.put(DatabaseHelper.Freight.TOTAL_VALUE_DRIVE, f.getTotalValueDrive().doubleValue());
        values.put(DatabaseHelper.Freight._ADDRESS_ID, f.getAddress().getId());
        values.put(DatabaseHelper.Freight.DATE_CREATED, f.getDateCreated().getTimeInMillis());
        values.put(DatabaseHelper.Freight.LAST_UPDATED, f.getLastUpdated().getTimeInMillis());

        return values;
    }
}
