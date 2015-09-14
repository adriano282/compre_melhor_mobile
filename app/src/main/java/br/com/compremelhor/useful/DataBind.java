package br.com.compremelhor.useful;

import android.database.Cursor;

import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;

/**
 * Created by adriano on 13/09/15.
 */
public class DataBind {

    public Object bind(Object objectModel, Cursor cursor) {
        if (objectModel instanceof Address) {
            Address ad = (Address) objectModel;

            ad.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Address._ID)));
            ad.setState(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.STATE)));
            ad.setStreet(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.STREET)));
            ad.setZipcode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.ZIPCODE)));
            ad.setQuarter(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.QUARTER)));
            ad.setCity(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.CITY)));
            ad.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Address.NUMBER)));

            return ad;
        }

        return null;
    }
}
