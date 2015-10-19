package br.com.compremelhor.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adriano on 25/08/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "CompreMelhor.db";
    private static int DATABASE_VERSION = 8;

    public interface Domain {
        String  _ID = "_id",
                DATE_CREATED = "date_created",
                DATE_UPDATED = "date_updated";
    }
    public interface Manufacturer extends Domain{
        String  TABLE = "manufacturer",
                COMPANY_NAME = "company_name";
        String[] COLUMNS = new String[] {_ID, COMPANY_NAME, DATE_CREATED, DATE_UPDATED};
    }

    public interface Code extends Domain {
        String  TABLE = "code",
                CODE = "code",
                CODE_TYPE = "code_type";
        String[] COLUMNS = new String[] {_ID, CODE, CODE_TYPE, DATE_CREATED, DATE_UPDATED};
    }

    public interface Category extends Domain {
        String  TABLE = "category",
                NAME = "name";
        String[] COLUMNS = new String[] {_ID, NAME, DATE_CREATED, DATE_UPDATED};
    }

    public interface Product extends Domain {
        String  TABLE = "product",
                NAME = "name",
                DESCRIPTION = "description",
                UNIT = "unit",
                _MANUFACTURER_ID = "_manufacturer_id",
                _CODE_ID = "_code_id",
                _CATEGORY_ID = "_category_id";
        String[] COLUMNS = new String[] {_ID, NAME, DESCRIPTION, UNIT,
                _MANUFACTURER_ID, _CODE_ID, _CATEGORY_ID, DATE_CREATED, DATE_UPDATED};
    }

    public interface Establishment extends Domain {
        String  TABLE = "establishment",
                NAME = "name";
        String[] COLUMNS = new String[] {_ID, NAME, DATE_CREATED, DATE_UPDATED};
    }

    public interface Address extends Domain {
        String  TABLE = "address",
                STREET = "street",
                NUMBER = "number",
                QUARTER = "quarter",
                CITY = "city",
                STATE = "state",
                ZIPCODE = "zipcode",
                _USER_ID = "_user_id";
        String[] COLUMNS = new String[] {_ID, STREET, NUMBER,
            QUARTER, CITY, STATE, ZIPCODE, _USER_ID, DATE_CREATED, DATE_UPDATED};
    }

    public interface User extends Domain {
        String  TABLE = "user",
                EMAIL = "email",
                NAME = "name",
                DOCUMENT = "document",
                TYPE_DOCUMENT = "type_document",
                PASSWORD = "password";
        String[] COLUMNS = new String[] {_ID, EMAIL, NAME,
                DOCUMENT, TYPE_DOCUMENT, PASSWORD, DATE_CREATED, DATE_UPDATED};
    }

    public interface Freight extends Domain {
        String  TABLE = "freight",
                TOTAL_VALUE_DRIVE = "total_value_drive",
                _ADDRESS_ID = "_address_id";
        String[] COLUMNS = new String[] {_ID, TOTAL_VALUE_DRIVE,
                _ADDRESS_ID, DATE_CREATED, DATE_UPDATED};
    }

    public interface Purchase extends Domain {
        String  TABLE = "purchase",
                STATUS = "status",
                TOTAL_VALUE = "value_total",
                _FREIGHT_ID = "_freight_id",
                _ESTABLISHMENT_ID = "_establishment_id";
        String[] COLUMNS = {_ID, STATUS, TOTAL_VALUE, _FREIGHT_ID, _ESTABLISHMENT_ID, DATE_CREATED, DATE_UPDATED};
    }

    public interface PurchaseLine extends Domain {
        String  TABLE = "purchase_line",
                QUANTITY = "quantity",
                UNITARY_PRICE = "unitary_price",
                _PRODUCT_ID = "_product_id",
                _PURCHASE_ID = "_purchase_id";
        String[] COLUMNS = {_ID, QUANTITY, UNITARY_PRICE, _PRODUCT_ID, _PURCHASE_ID, DATE_CREATED, DATE_UPDATED};
    }


    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +  User.TABLE + " (" +
                User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                User.EMAIL + " TEXT," +
                User.NAME + " TEXT, " +
                User.DOCUMENT + " NUMERIC, " +
                User.PASSWORD + " TEXT, " +
                User.TYPE_DOCUMENT + " TEXT);");

        db.execSQL("CREATE TABLE " + Address.TABLE + " (" +
                Address._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Address.STREET + " TEXT, " +
                Address.NUMBER + " TEXT, " +
                Address.QUARTER + " TEXT, " +
                Address.CITY + " TEXT, " +
                Address.STATE + " TEXT, " +
                Address.ZIPCODE + " TEXT, " +
                Address._USER_ID + " INTEGER, " +
                " FOREIGN KEY(" + Address._USER_ID + ") " +
                " REFERENCES user(" + User._ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE address");
        db.execSQL("DROP TABLE user");

        db.execSQL("CREATE TABLE " +  User.TABLE + " (" +
                User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                User.EMAIL + " VARCHAR(30)," +
                User.NAME + " VARCHAR(20), " +
                User.DOCUMENT + " NUMERIC, " +
                User.PASSWORD + " VARCHAR(10), " +
                User.TYPE_DOCUMENT + " VARCHAR(5) NOT NULL," +
                User.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                User.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Address.TABLE + " (" +
                Address._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Address.STREET + " VARCHAR(20), " +
                Address.NUMBER + " VARCHAR(10), " +
                Address.QUARTER + " VARCHAR(20), " +
                Address.CITY + " VARCHAR(20), " +
                Address.STATE + " VARCHAR(10), " +
                Address.ZIPCODE + " VARCHAR(10) NOT NULL, " +
                Address._USER_ID + " INTEGER NOT NULL, " +
                Address.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Address.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY(" + Address._USER_ID + ") " +
                " REFERENCES " + User.TABLE + "(" + User._ID + "));");

        db.execSQL("CREATE TABLE " + Manufacturer.TABLE + " (" +
                Manufacturer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Manufacturer.COMPANY_NAME + " VARCHAR(20) UNIQUE NOT NULL, " +
                Manufacturer.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Manufacturer.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Code.TABLE + " (" +
                Code._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Code.CODE + " VARCHAR(20) UNIQUE NOT NULL, " +
                Code.CODE_TYPE + " VARCHAR(10) NOT NULL" +
                Code.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Code.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");


        db.execSQL("CREATE TABLE " + Category.TABLE + " (" +
                Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Category.NAME + " VARCHAR(20), " +
                Category.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Category.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Product.TABLE + " (" +
                Product._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Product.NAME + " VARCHAR(20), " +
                Product.DESCRIPTION + " TEXT, " +
                Product.UNIT + " VARCHAR(10), " +
                Product._MANUFACTURER_ID + " INTEGER NOT NULL, " +
                Product._CODE_ID + " INTEGER NOT NULL, " +
                Product._CATEGORY_ID + " INTEGER NOT NULL, " +
                Product.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Product.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + Product._MANUFACTURER_ID + ") " +
                " REFERENCES " + Manufacturer.TABLE + "(" + Manufacturer._ID + "), " +
                " FOREIGN KEY( " + Product._CODE_ID + ") " +
                " REFERENCES " + Code.TABLE + "(" + Code._ID + "), " +
                " FOREIGN KEY( " + Product._CATEGORY_ID + ") " +
                " REFERENCES " + Category.TABLE + "(" + Category._ID + "));");

        db.execSQL("CREATE TABLE " + Freight.TABLE + " (" +
                Freight._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Freight.TOTAL_VALUE_DRIVE + " DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                Freight._ADDRESS_ID + " INTEGER NOT NULL, " +
                Freight.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Freight.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + Freight._ADDRESS_ID + ") " +
                " REFERENCES " + Address.TABLE + "(" + Address._ID + "));");

        db.execSQL("CREATE TABLE " + Establishment.TABLE + " (" +
                Establishment._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Establishment.NAME + " VARCHAR(20) NOT NULL, " +
                Establishment.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Establishment.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + PurchaseLine.TABLE + " (" +
                PurchaseLine._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                PurchaseLine.QUANTITY + " DECIMAL(10,2) NOT NULL DEFAULT 0.0 " +
                PurchaseLine.UNITARY_PRICE + " DECIMAL(10,2) NOT NULL DEFAULT 0.00 " +
                PurchaseLine._PRODUCT_ID + " INTEGER NOT NULL " +
                PurchaseLine._PURCHASE_ID + " INTEGER NOT NULL " +
                PurchaseLine.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                PurchaseLine.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + PurchaseLine._PRODUCT_ID + ") " +
                " REFERENCES " + Product.TABLE + "(" + Product._ID + "), " +
                " FOREIGN KEY( " + PurchaseLine._PURCHASE_ID + ") " +
                " REFERENCES " + Purchase.TABLE + "(" + Purchase._ID + "));");

        db.execSQL("CREATE TABLE " + Purchase.TABLE + " (" +
                Purchase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Purchase.STATUS + " VARCHAR(10) NOT NULL, " +
                Purchase.TOTAL_VALUE + " DECIMAL(10,2) DEFAULT NOT NULL DEFAULT 0.00, " +
                Purchase._ESTABLISHMENT_ID + " INTEGER NOT NULL, " +
                Purchase._FREIGHT_ID + " INTEGER NOT NULL, " +
                Purchase.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Purchase.DATE_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + Purchase._ESTABLISHMENT_ID + ") " +
                " REFERENCES " + Establishment.TABLE + "(" + Establishment._ID + "), " +
                " FOREIGN KEY( " + Purchase._FREIGHT_ID + ") " +
                " REFERENCES " + Freight.TABLE + "(" + Freight._ID + "));");
    }
}
