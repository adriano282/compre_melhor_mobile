package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 13/09/15.
 */
public class DAOAddress extends AbstractDAO<Address> {
    private static DAOAddress instance;

    public static  DAOAddress getInstance(Context context) {
        if (instance == null) {
            instance = new DAOAddress(context);
        }
        return instance;
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        ContentValues values = new ContentValues();
        Address address = (Address) o;

        if (address.getId() != 0) {values.put(DatabaseHelper.User._ID, address.getId());}
        values.put(DatabaseHelper.Address.CITY, address.getCity());
        values.put(DatabaseHelper.Address.NUMBER, address.getNumber());
        values.put(DatabaseHelper.Address.QUARTER, address.getQuarter());
        values.put(DatabaseHelper.Address.STATE, address.getState());
        values.put(DatabaseHelper.Address.STREET, address.getStreet());
        values.put(DatabaseHelper.Address.ZIPCODE, address.getZipcode());
        values.put(DatabaseHelper.Address.ADDRESS_NAME, address.getAddressName());
        values.put(DatabaseHelper.Address._USER_ID, address.getUserId());

        if (address.getUserId() == 0) { throw new RuntimeException("User ID is null on address entity, while trying create address"); }
        return  values;
    }

    private DAOAddress(Context context) {
        super(context, Address.class, DatabaseHelper.Address.TABLE, DatabaseHelper.Address.COLUMNS);
    }
}
