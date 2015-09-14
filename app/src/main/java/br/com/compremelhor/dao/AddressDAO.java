package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.com.compremelhor.model.Address;

/**
 * Created by adriano on 13/09/15.
 */
public class AddressDAO extends DAO {

    public AddressDAO(Context context) {
        super(context);
    }

    public List<Address> listAddresses() {
        Cursor cursor = getDB().query(
                DatabaseHelper.Address.TABLE,
                DatabaseHelper.Address.COLUMNS,
                null, null, null, null, null);

        List<Address> addresses = new ArrayList<Address>();

        while(cursor.moveToNext()) {
            Address address = (Address) getBind().bind(new Address(), cursor);
            addresses.add(address);
        }
        cursor.close();
        return addresses;
    }

    public Address getAddressById(Long id) {
        Cursor cursor = getDB().query(DatabaseHelper.Address.TABLE,
                DatabaseHelper.Address.COLUMNS,
                DatabaseHelper.Address._ID + " = ?",
                new String[] {id.toString()}, null, null, null);

        if (cursor.moveToNext()) {
            Address address = (Address) getBind().bind(new Address(), cursor);
            cursor.close();
            return address;
        }
        return null;
    }

    public void delete(Long id) {
        String where [] = new String[] {id.toString()};
        getDB().delete(
                DatabaseHelper.Address.TABLE,
                DatabaseHelper.Address._ID + " = ?",
                where);
    }

    public int insertOrUpdate(Address address) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Address.CITY, address.getCity());
        values.put(DatabaseHelper.Address.NUMBER, address.getNumber());
        values.put(DatabaseHelper.Address.QUARTER, address.getQuarter());
        values.put(DatabaseHelper.Address.STATE, address.getState());
        values.put(DatabaseHelper.Address.STREET, address.getStreet());
        values.put(DatabaseHelper.Address.ZIPCODE, address.getZipcode());

        if (address.getId() == null || address.getId() == 0)
            return (int) getDB().insert(DatabaseHelper.Address.TABLE, null, values);

        return getDB().update(DatabaseHelper.Address.TABLE, values,
                DatabaseHelper.Address._ID + " = ?", new String[] {address.getId().toString()});
    }
}
