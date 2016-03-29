package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Manufacturer;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOManufacturer extends DAO {
    public DAOManufacturer(Context context) {
        super(context);
    }

    @Override
    public long insertOrUpdate(EntityModel manufacturer) {
        ContentValues values = new ContentValues();
        Manufacturer m = (Manufacturer) manufacturer;

        values.put(DatabaseHelper.Manufacturer._ID, m.getId());
        values.put(DatabaseHelper.Manufacturer.COMPANY_NAME, m.getCompanyName());
        values.put(DatabaseHelper.Manufacturer.DATE_CREATED, "now");

        if (m.getId() == 0)
            return (int) getDB().insert(DatabaseHelper.Manufacturer.TABLE, null, values);

        return getDB().update(DatabaseHelper.Manufacturer.TABLE, values,
                DatabaseHelper.Manufacturer._ID + " = ?", new String[] {String.valueOf(m.getId())});
    }

    public Manufacturer getManufacturerByCompanyName(String name) {
        if (name == null || name.equals(""))
            return null;

        try (Cursor cursor = getDB().query(DatabaseHelper.Manufacturer.TABLE,
                DatabaseHelper.Manufacturer.COLUMNS,
                DatabaseHelper.Manufacturer.COMPANY_NAME + " = ?",
                new String[] {name}, null, null, null)) {

            if (cursor.moveToNext()) {
                return (Manufacturer) getBind().bind(new Manufacturer(), cursor);
            }
            return null;
        }
    }

    public Manufacturer getManufacturerById(int id) {
        try (Cursor cursor = getDB().query(DatabaseHelper.Manufacturer.TABLE,
                DatabaseHelper.Manufacturer.COLUMNS,
                DatabaseHelper.Manufacturer._ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null)) {

            if (cursor.moveToNext()) {
                return (Manufacturer) getBind().bind(new Manufacturer(), cursor);
            }
            return null;
        }
    }
}
