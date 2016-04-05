package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Establishment;

public class DAOEstablishment extends AbstractDAO<Establishment> {
    private static DAOEstablishment instance;

    public static DAOEstablishment getInstance(Context context) {
        if (instance == null)
            instance = new DAOEstablishment(context);

        return instance;
    }
    private DAOEstablishment(Context context) {
        super(context, Establishment.class, DatabaseHelper.Establishment.TABLE, DatabaseHelper.Establishment.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        ContentValues values = new ContentValues();
        Establishment est = (Establishment) o;

        values.put(DatabaseHelper.Establishment._ID, est.getId());
        values.put(DatabaseHelper.Establishment.NAME, est.getName());
        return values;
    }
}
