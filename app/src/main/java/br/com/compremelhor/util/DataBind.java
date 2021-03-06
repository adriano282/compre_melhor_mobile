package br.com.compremelhor.util;

import android.content.Context;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import br.com.compremelhor.dao.impl.DAOAddress;
import br.com.compremelhor.dao.impl.DAOCategory;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOFreight;
import br.com.compremelhor.dao.impl.DAOManufacturer;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.dao.impl.DAOUser;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.model.FreightType;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.User;
import br.com.compremelhor.util.helper.DatabaseHelper;

/**
 * Created by adriano on 13/09/15.
 */
public class DataBind {

    private Context context;
    public DataBind(Context context) {
        this.context = context;
    }

    public Object bind(Class clazz, Cursor cursor) {
        try {
            return bind((EntityModel)Class.forName(clazz.getName()).newInstance(), cursor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object bind(EntityModel objectModel, Cursor cursor) {
        if (objectModel != null) {
            objectModel.setId(getInt(cursor, DatabaseHelper.Domain._ID));
            objectModel.setDateCreated(getCalendar(cursor, DatabaseHelper.Domain.DATE_CREATED));
            objectModel.setLastUpdated(getCalendar(cursor, DatabaseHelper.Domain.LAST_UPDATED));
        }

        if (objectModel instanceof Address) {
            Address ad = (Address) objectModel;

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

            user.setName(getString(cursor, DatabaseHelper.User.NAME));
            user.setDocument(getString(cursor, DatabaseHelper.User.DOCUMENT));
            user.setEmail(getString(cursor, DatabaseHelper.User.EMAIL));
            user.setPassword(getString(cursor, DatabaseHelper.User.PASSWORD));
            user.setTypeDocument(getString(cursor, DatabaseHelper.User.TYPE_DOCUMENT));
            user.setLoggedByFacebook(getBoolean(cursor, DatabaseHelper.User.LOGGED_BY_FACEBOOK));

            return user;
        } else if (objectModel instanceof Product) {
            Product p = (Product) objectModel;

            p.setDescription(getString(cursor, DatabaseHelper.Product.DESCRIPTION));
            p.setName(getString(cursor, DatabaseHelper.Product.NAME));

            Manufacturer manufacturer = DAOManufacturer
                    .getInstance(context)
                    .find(getInt(cursor, DatabaseHelper.Product._MANUFACTURER_ID));
            p.setManufacturer(manufacturer);

            Category category = DAOCategory.getInstance(context).find(getInt(cursor, DatabaseHelper.Product._CATEGORY_ID));
            p.setCategory(category);

            p.setCode(getString(cursor, DatabaseHelper.Product.CODE));

            p.setUnit(Product.Unit.valueOf(getString(cursor, DatabaseHelper.Product.UNIT)));

            return p;
        } else if (objectModel instanceof Manufacturer) {
            Manufacturer m = (Manufacturer) objectModel;

            m.setCompanyName(getString(cursor, DatabaseHelper.Manufacturer.COMPANY_NAME));
            /* Miss the implementation of date field */

            return m;
        } else if (objectModel instanceof Establishment) {
            Establishment est = (Establishment) objectModel;

            est.setName(getString(cursor, DatabaseHelper.Establishment.NAME));

            return est;
        } else if (objectModel instanceof Freight) {
            Freight freight = (Freight) objectModel;

            Address address = DAOAddress.getInstance(context).find(getInt(cursor, DatabaseHelper.Freight._ADDRESS_ID));
            freight.setShipAddress(address);


            Calendar calendar = getCalendar(cursor, DatabaseHelper.Freight.STARTING_DATE_TIME);
            if (calendar != null) {
                FreightSetup freightSetup = new FreightSetup();
                freightSetup.setYear(calendar.get(Calendar.YEAR));
                freightSetup.setMonth(calendar.get(Calendar.MONTH));
                freightSetup.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                freightSetup.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                freightSetup.setMinute(calendar.get(Calendar.MINUTE));
                freight.setFreightSetup(freightSetup);
            }

            if (getString(cursor, DatabaseHelper.Freight.TYPE) != null) {
                String type = getString(cursor, DatabaseHelper.Freight.TYPE);
                freight.setType(type);
            }

            freight.setVersion(getInt(cursor, DatabaseHelper.Freight.VERSION));
            freight.setComplete(getBoolean(cursor, DatabaseHelper.Freight.COMPLETED));
            freight.setRideValue(getBigDecimal(cursor, DatabaseHelper.Freight.TOTAL_VALUE_DRIVE));
            return freight;
        }
        else if (objectModel instanceof FreightType) {
            FreightType ft = (FreightType) objectModel;

            ft.setTypeName(getString(cursor, DatabaseHelper.FreightType.TYPE_NAME));
            ft.setDelayInWorkdays(getInt(cursor, DatabaseHelper.FreightType.DELAY_WORK_DAYS));
            ft.setAvailabilityScheduleWorkDays(getInt(cursor, DatabaseHelper.FreightType.AVAILABILITY_SCHEDULE_WORK_DAYS));
            ft.setScheduled(getBoolean(cursor, DatabaseHelper.FreightType.SCHEDULED));
            ft.setRideValue(getBigDecimal(cursor, DatabaseHelper.FreightType.RIDE_VALUE));
            ft.setEstablishmentId(getInt(cursor, DatabaseHelper.FreightType._ESTABLISHMENT_ID));
            ft.setDescription(getString(cursor, DatabaseHelper.FreightType.DESCRIPTION));
            return ft;
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
            pl.setProductCode(getString(cursor, DatabaseHelper.PurchaseLine.PRODUCT_CODE));


            Product p = new Product();
            p.setId(getInt(cursor, DatabaseHelper.PurchaseLine._PRODUCT_ID));
            pl.setProduct(p);

            return pl;
        } else if (objectModel instanceof Purchase) {
            Purchase purchase = (Purchase) objectModel;

            purchase.setStatus(Purchase.Status.valueOf(getString(cursor, DatabaseHelper.Purchase.STATUS)));

            Establishment establishment = DAOEstablishment
                    .getInstance(context)
                    .find(getInt(cursor, DatabaseHelper.Purchase._ESTABLISHMENT_ID));

            purchase.setEstablishment(establishment);

            purchase.setTotalValue(getBigDecimal(cursor, DatabaseHelper.Purchase.TOTAL_VALUE));

            User user = DAOUser.getInstance(context).find(getInt(cursor, DatabaseHelper.Purchase._USER_ID));
            purchase.setUser(user);

            List<PurchaseLine> list = DAOPurchaseLine
                    .getInstance(context)
                    .findAllByForeignId(DatabaseHelper.PurchaseLine._PURCHASE_ID, purchase.getId());
            purchase.setItems(new TreeSet<>(list));

            if (purchase.getFreight() != null) {
                Freight freight;
                freight = DAOFreight.getInstance(context).find(purchase.getFreight().getId());
                purchase.setFreight(freight);
            }

            return purchase;
        }

        return null;
    }

    private String getString(Cursor cursor, String column) {
        if (cursor.getColumnIndex(column) == -1) return null;
        return cursor.getString(cursor.getColumnIndex(column));
    }


    private Long getLong(Cursor cursor, String column) {
        if (cursor.getColumnIndex(column) == -1) return null;

        return cursor.getLong(cursor.getColumnIndex(column));
    }

    private Double getDouble(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    private Integer getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }
    private Calendar getCalendar(Cursor cursor, String column) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(getLong(cursor, column)));
            return  calendar;
        } catch (Exception e) { return null; }
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
