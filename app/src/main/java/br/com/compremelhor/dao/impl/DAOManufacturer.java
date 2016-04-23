package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Manufacturer;

public class DAOManufacturer extends AbstractDAO<Manufacturer> {
    private static DAOManufacturer instance;

    public static DAOManufacturer getInstance(Context context) {
        if (instance == null)
            instance = new DAOManufacturer(context);

        return instance;
    }

    private DAOManufacturer(Context context) {
        super(context, Manufacturer.class, DatabaseHelper.Manufacturer.TABLE, DatabaseHelper.Manufacturer.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        ContentValues values = new ContentValues();
        Manufacturer m = (Manufacturer) o;

        values.put(DatabaseHelper.Manufacturer._ID, m.getId());
        values.put(DatabaseHelper.Manufacturer.COMPANY_NAME, m.getCompanyName());
        values.put(DatabaseHelper.Manufacturer.DATE_CREATED, "now");

        return values;
    }
}
