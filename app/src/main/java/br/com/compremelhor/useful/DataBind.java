package br.com.compremelhor.useful;

import android.database.Cursor;

import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.User;

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
        } else if (objectModel instanceof User) {
            User user = (User) objectModel;

            user.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.User._ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.NAME)));
            user.setDocument(cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.DOCUMENT)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.PASSWORD)));
            user.setTypeDocument(cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.TYPE_DOCUMENT)));

            return user;
        }

        return null;
    }
}
