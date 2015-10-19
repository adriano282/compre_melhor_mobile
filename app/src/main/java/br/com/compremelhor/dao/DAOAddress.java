package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.DomainEntity;

/**
 * Created by adriano on 13/09/15.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOAddress extends DAO {
    public DAOAddress(Context context) {
        super(context);
    }

    public Address getAddressById(Long id) {
        try(Cursor cursor = getDB().query(DatabaseHelper.Address.TABLE,
                DatabaseHelper.Address.COLUMNS,
                DatabaseHelper.Address._ID + " = ?",
                new String[] {id.toString()}, null, null, null)) {
            if (cursor.moveToNext()) {
                Address address = (Address) getBind().bind(new Address(), cursor);
                cursor.close();
                return address;
            }
            return null;
        }
    }

    public List<Address> getAddressesByUserId(Long userId) {
        try(Cursor cursor = getDB().query(DatabaseHelper.Address.TABLE,
                DatabaseHelper.Address.COLUMNS,
                DatabaseHelper.Address._USER_ID + " = ?",
                new String[] {userId.toString()}, null, null, null)) {

            List<Address> addresses = new ArrayList<Address>();
            while (cursor.moveToNext()) {
                Address address = (Address) getBind().bind(new Address(), cursor);
                addresses.add(address);
            }
            return addresses;
        }
    }

    @Override
    public long insertOrUpdate(DomainEntity ad) {
        ContentValues values = new ContentValues();
        Address address = (Address) ad;

        values.put(DatabaseHelper.Address.CITY, address.getCity());
        values.put(DatabaseHelper.Address.NUMBER, address.getNumber());
        values.put(DatabaseHelper.Address.QUARTER, address.getQuarter());
        values.put(DatabaseHelper.Address.STATE, address.getState());
        values.put(DatabaseHelper.Address.STREET, address.getStreet());
        values.put(DatabaseHelper.Address.ZIPCODE, address.getZipcode());
        values.put(DatabaseHelper.Address._USER_ID, address.getUserId());

        if (address.getId() == null || address.getId() == 0)
            return getDB().insert(DatabaseHelper.Address.TABLE, null, values);

        return getDB().update(DatabaseHelper.Address.TABLE, values,
                DatabaseHelper.Address._ID + " = ?", new String[] {address.getId().toString()});
    }
}
