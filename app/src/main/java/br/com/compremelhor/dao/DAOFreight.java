package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.DomainEntity;
import br.com.compremelhor.model.Freight;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOFreight extends DAO {
    public DAOFreight(Context context) {
        super(context);
    }

    public long insertOrUpdate(DomainEntity o) {
        Freight f = (Freight) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Freight._ID, f.getId());
        values.put(DatabaseHelper.Freight.TOTAL_VALUE_DRIVE, f.getTotalValueDrive().doubleValue());
        values.put(DatabaseHelper.Freight._ADDRESS_ID, f.getAddress().getId());
        values.put(DatabaseHelper.Freight.DATE_CREATED, f.getDateCreated().getTimeInMillis());
        values.put(DatabaseHelper.Freight.LAST_UPDATED, f.getLastUpdated().getTimeInMillis());

        if (f.getId() == null)
            return getDB().insert(DatabaseHelper.Freight.TABLE, null, values);

        return getDB().update(DatabaseHelper.Freight.TABLE, values,
                DatabaseHelper.Freight._ID + " = ?",
                new String[] {f.getId().toString()});
    }

    public Freight getFreightById(Long id) {
        if (id == null)
            return null;
        try(Cursor cursor = getDB().query(DatabaseHelper.Freight.TABLE, DatabaseHelper.Freight.COLUMNS,
                DatabaseHelper.Freight._ID + " = ?", new String[] {id.toString()},
                null, null, null)) {
            if (cursor.moveToNext()) {
                return (Freight) getBind().bind(new Freight(), cursor);
            }
            return null;
        }
    }
}
