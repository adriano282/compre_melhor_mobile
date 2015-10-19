package br.com.compremelhor.useful;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;

import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.dao.DAOCategory;
import br.com.compremelhor.dao.DAOCode;
import br.com.compremelhor.dao.DAOManufacturer;
import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 13/09/15.
 */
public class DataBind {

    private Context context;

    public DataBind(Context context) {
        this.context = context;
    }

    public Object bind(Object objectModel, Cursor cursor) {
        if (objectModel instanceof Address) {
            Address ad = (Address) objectModel;

            ad.setId(getLong(cursor, DatabaseHelper.Address._ID));
            ad.setState(getString(cursor, DatabaseHelper.Address.STATE));
            ad.setStreet(getString(cursor, DatabaseHelper.Address.STREET));
            ad.setZipcode(getString(cursor, DatabaseHelper.Address.ZIPCODE));
            ad.setQuarter(getString(cursor, DatabaseHelper.Address.QUARTER));
            ad.setCity(getString(cursor, DatabaseHelper.Address.CITY));
            ad.setNumber(getString(cursor, DatabaseHelper.Address.NUMBER));
            ad.setUserId(getLong(cursor, DatabaseHelper.Address._USER_ID));

            return ad;
        } else if (objectModel instanceof User) {
            User user = (User) objectModel;

            user.setId(getLong(cursor, DatabaseHelper.User._ID));
            user.setName(getString(cursor, DatabaseHelper.User.NAME));
            user.setDocument(getString(cursor, DatabaseHelper.User.DOCUMENT));
            user.setEmail(getString(cursor, DatabaseHelper.User.EMAIL));
            user.setPassword(getString(cursor, DatabaseHelper.User.PASSWORD));
            user.setTypeDocument(getString(cursor, DatabaseHelper.User.TYPE_DOCUMENT));

            return user;
        } else if (objectModel instanceof Product) {
            Product p = (Product) objectModel;

            p.setId(getLong(cursor, DatabaseHelper.Product._ID));
            p.setDescription(getString(cursor, DatabaseHelper.Product.DESCRIPTION));
            p.setName(getString(cursor, DatabaseHelper.Product.NAME));

            Manufacturer manufacturer = new DAOManufacturer(context).getManufacturerById(getLong(cursor, DatabaseHelper.Product._MANUFACTURER_ID));
            p.setManufacturer(manufacturer);

            Category category = new DAOCategory(context).getCategoryById(getLong(cursor, DatabaseHelper.Product._CATEGORY_ID));
            p.setCategory(category);

            Code code = new DAOCode(context).getCodeById(getLong(cursor, DatabaseHelper.Product._CODE_ID));
            p.setCode(code);

            p.setUnit(getString(cursor, DatabaseHelper.Product.UNIT));

            return p;
        } else if (objectModel instanceof Manufacturer) {
            Manufacturer m = (Manufacturer) objectModel;

            m.setId(getLong(cursor, DatabaseHelper.Manufacturer._ID));
            m.setCompanyName(getString(cursor, DatabaseHelper.Manufacturer.COMPANY_NAME));
            /* Miss the implementation of date field */

            return m;
        } else if (objectModel instanceof Code) {
            Code c = (Code) objectModel;

            c.setId(getLong(cursor, DatabaseHelper.Code._ID));
            c.setCode(getString(cursor, DatabaseHelper.Code.CODE));
            c.setType(getString(cursor, DatabaseHelper.Code.CODE_TYPE));

            return c;
        } else if (objectModel instanceof Establishment) {
            Establishment est = (Establishment) objectModel;

            est.setId(getLong(cursor, DatabaseHelper.Establishment._ID));
            est.setName(getString(cursor, DatabaseHelper.Establishment.NAME));

            return est;
        } else if (objectModel instanceof Freight) {
            Freight freight = (Freight) objectModel;

            freight.setId(getLong(cursor, DatabaseHelper.Freight._ID));

            Address address = new DAOAddress(context).getAddressById(getLong(cursor, DatabaseHelper.Freight._ADDRESS_ID));
            freight.setAddress(address);

            freight.setTotalValueDrive(getBigDecimal(cursor, DatabaseHelper.Freight.TOTAL_VALUE_DRIVE));

        }

        return null;
    }

    private String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private Long getLong(Cursor cursor, String column) {
        return cursor.getLong(cursor.getColumnIndex(column));
    }

    private Double getDouble(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    private BigDecimal getBigDecimal(Cursor cursor, String column) {
        try {
            return new BigDecimal(cursor.getDouble(cursor.getColumnIndex(column)));
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0.0);
        }
    }
}
