package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.DomainEntity;
import br.com.compremelhor.model.Establishment;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOEstablishment extends DAO {
    @Override
    public long insertOrUpdate(DomainEntity o) {
        ContentValues values = new ContentValues();
        Establishment est = (Establishment) o;

        values.put(DatabaseHelper.Establishment._ID, est.getId());
        values.put(DatabaseHelper.Establishment.NAME, est.getName());

        if (est.getId() == null)
            return getDB().insert(DatabaseHelper.Establishment.TABLE, null, values);

        return getDB().update(DatabaseHelper.Establishment.TABLE, values,
                DatabaseHelper.Establishment._ID + " = ?", new String[] {est.getId().toString()});
    }


    public Establishment getEstablishmentById(Long id) {
        if (id == null)
            return null;

        try (Cursor cursor = getDB().query(DatabaseHelper.Establishment.TABLE,
                    DatabaseHelper.Establishment.COLUMNS,
                    DatabaseHelper.Establishment._ID + " = ?",
                    new String[] {id.toString()}, null, null, null)) {
            if (cursor.moveToNext()) {
                return (Establishment) getBind().bind(new Establishment(), cursor);
            }
            return null;
        }
    }

    public DAOEstablishment(Context context) {
        super(context);
    }
}