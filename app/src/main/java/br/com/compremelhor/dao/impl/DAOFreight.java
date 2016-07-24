package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import java.util.Calendar;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.util.helper.DatabaseHelper;

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
        values.put(DatabaseHelper.Freight.TOTAL_VALUE_DRIVE, f.getRideValue().doubleValue());
        values.put(DatabaseHelper.Freight._ADDRESS_ID, f.getShipAddress().getId());
        values.put(DatabaseHelper.Freight.TYPE, f.getType() != null ? f.getType().toString() : "");
        values.put(DatabaseHelper.Freight._PURCHASE_ID, f.getPurchase().getId());

        if (f.getFreightSetup() != null) {
            Calendar startingDate = Calendar.getInstance();
            startingDate.set(f.getFreightSetup().getYear(), f.getFreightSetup().getMonth(), f.getFreightSetup().getDayOfMonth(),
                    f.getFreightSetup().getHour(), f.getFreightSetup().getMinute(), 0);
            values.put(DatabaseHelper.Freight.STARTING_DATE_TIME, startingDate.getTimeInMillis());
        }

        values.put(DatabaseHelper.Freight.DATE_CREATED, f.getDateCreated() == null ? Calendar.getInstance().getTimeInMillis() : f.getDateCreated().getTimeInMillis());
        values.put(DatabaseHelper.Freight.LAST_UPDATED, f.getLastUpdated() == null ? Calendar.getInstance().getTimeInMillis() : f.getLastUpdated().getTimeInMillis());

        return values;
    }
}
