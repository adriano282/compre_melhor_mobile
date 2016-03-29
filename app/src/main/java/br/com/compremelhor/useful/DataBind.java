package br.com.compremelhor.useful;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.dao.DAOCategory;
import br.com.compremelhor.dao.DAOCode;
import br.com.compremelhor.dao.DAOManufacturer;
import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;
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

            ad.setId(getInt(cursor, DatabaseHelper.Address._ID));
            ad.setState(getString(cursor, DatabaseHelper.Address.STATE));
            ad.setStreet(getString(cursor, DatabaseHelper.Address.STREET));
            ad.setZipcode(getString(cursor, DatabaseHelper.Address.ZIPCODE));
            ad.setQuarter(getString(cursor, DatabaseHelper.Address.QUARTER));
            ad.setCity(getString(cursor, DatabaseHelper.Address.CITY));
            ad.setNumber(getString(cursor, DatabaseHelper.Address.NUMBER));
            ad.setUserId(getInt(cursor, DatabaseHelper.Address._USER_ID));
            ad.setAddressName(getString(cursor, DatabaseHelper.Address.ADDRESS_NAME));

            return ad;
        } else if (objectModel instanceof User) {
            User user = (User) objectModel;

            user.setId(getInt(cursor, DatabaseHelper.User._ID));
            user.setName(getString(cursor, DatabaseHelper.User.NAME));
            user.setDocument(getString(cursor, DatabaseHelper.User.DOCUMENT));
            user.setEmail(getString(cursor, DatabaseHelper.User.EMAIL));
            user.setPassword(getString(cursor, DatabaseHelper.User.PASSWORD));
            user.setTypeDocument(getString(cursor, DatabaseHelper.User.TYPE_DOCUMENT));
            user.setLoggedByFacebook(getBoolean(cursor, DatabaseHelper.User.LOGGED_BY_FACEBOOK));

            return user;
        } else if (objectModel instanceof Product) {
            Product p = (Product) objectModel;

            p.setId(getInt(cursor, DatabaseHelper.Product._ID));
            p.setDescription(getString(cursor, DatabaseHelper.Product.DESCRIPTION));
            p.setName(getString(cursor, DatabaseHelper.Product.NAME));

            Manufacturer manufacturer = new DAOManufacturer(context).getManufacturerById(getInt(cursor, DatabaseHelper.Product._MANUFACTURER_ID));
            p.setManufacturer(manufacturer);

            Category category = new DAOCategory(context).getCategoryById(getInt(cursor, DatabaseHelper.Product._CATEGORY_ID));
            p.setCategory(category);

            Code code = new DAOCode(context).getCodeById(getInt(cursor, DatabaseHelper.Product._CODE_ID));
            p.setCode(code);

            p.setUnit(Product.Unit.valueOf(getString(cursor, DatabaseHelper.Product.UNIT)));

            return p;
        } else if (objectModel instanceof Manufacturer) {
            Manufacturer m = (Manufacturer) objectModel;

            m.setId(getInt(cursor, DatabaseHelper.Manufacturer._ID));
            m.setCompanyName(getString(cursor, DatabaseHelper.Manufacturer.COMPANY_NAME));
            /* Miss the implementation of date field */

            return m;
        } else if (objectModel instanceof Code) {
            Code c = (Code) objectModel;

            c.setId(getInt(cursor, DatabaseHelper.Code._ID));
            c.setCode(getString(cursor, DatabaseHelper.Code.CODE));
            c.setType(Code.CodeType.valueOf(getString(cursor, DatabaseHelper.Code.CODE_TYPE)));

            return c;
        } else if (objectModel instanceof Establishment) {
            Establishment est = (Establishment) objectModel;

            est.setId(getInt(cursor, DatabaseHelper.Establishment._ID));
            est.setName(getString(cursor, DatabaseHelper.Establishment.NAME));

            return est;
        } else if (objectModel instanceof Freight) {
            Freight freight = (Freight) objectModel;

            freight.setId(getInt(cursor, DatabaseHelper.Freight._ID));

            Address address = DAOAddress.getInstance(context).getAddressById(getInt(cursor, DatabaseHelper.Freight._ADDRESS_ID));
            freight.setAddress(address);

            freight.setTotalValueDrive(getBigDecimal(cursor, DatabaseHelper.Freight.TOTAL_VALUE_DRIVE));

            return freight;
        }
        else if (objectModel instanceof PurchaseLine) {
            PurchaseLine pl = (PurchaseLine) objectModel;

            pl.setId(getInt(cursor, DatabaseHelper.PurchaseLine._ID));
            pl.setSubTotal(getBigDecimal(cursor, DatabaseHelper.PurchaseLine.SUB_TOTAL));
            pl.setQuantity(getBigDecimal(cursor, DatabaseHelper.PurchaseLine.QUANTITY));
            pl.setDateCreated(getCalendar(cursor, DatabaseHelper.PurchaseLine.DATE_CREATED));
            pl.setDateCreated(getCalendar(cursor, DatabaseHelper.PurchaseLine.LAST_UPDATED));
            pl.setCategory(getString(cursor, DatabaseHelper.PurchaseLine.CATEGORY));
            pl.setProductName(getString(cursor, DatabaseHelper.PurchaseLine.PRODUCT_NAME));

            Product p = new Product();
            p.setId(getInt(cursor, DatabaseHelper.PurchaseLine._PRODUCT_ID));
            pl.setProduct(p);

            return pl;
        }
        else if (objectModel instanceof Cart) {
            Cart c = (Cart) objectModel;

            c.setId(getInt(cursor, DatabaseHelper.Cart._ID));
            c.setDateCreated(getCalendar(cursor, DatabaseHelper.Cart.DATE_CREATED));
            c.setLastUpdated(getCalendar(cursor, DatabaseHelper.Cart.LAST_UPDATED));

            return c;
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

    private Integer getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }
    private Calendar getCalendar(Cursor cursor, String column) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(getInt(cursor, column)));
        return  calendar;
    }
    private boolean getBoolean(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column)) != 0;
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
