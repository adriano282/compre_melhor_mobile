package br.com.compremelhor.util.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adriano on 25/08/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "CompreMelhor.db";
    private static int DATABASE_VERSION = 57;

    public final String[] TABLES;

    {
        TABLES = new String[] {
                Purchase.TABLE,
                PurchaseLine.TABLE,
                Establishment.TABLE,
                Freight.TABLE,
                Product.TABLE,
                Category.TABLE,
                Code.TABLE,
                Manufacturer.TABLE,
                Address.TABLE,
                User.TABLE
        };
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    public interface Domain {
        String  _ID = "_id",
                DATE_CREATED = "date_created",
                LAST_UPDATED = "last_updated";
    }
    public interface Manufacturer extends Domain{
        String  TABLE = "manufacturer",
                COMPANY_NAME = "company_name";
        String[] COLUMNS = new String[] {_ID, COMPANY_NAME, DATE_CREATED, LAST_UPDATED};
    }

    public interface Code extends Domain {
        String  TABLE = "code",
                CODE = "code",
                CODE_TYPE = "code_type";
        String[] COLUMNS = new String[] {_ID, CODE, CODE_TYPE, DATE_CREATED, LAST_UPDATED};
    }

    public interface Category extends Domain {
        String  TABLE = "category",
                NAME = "name";
        String[] COLUMNS = new String[] {_ID, NAME, DATE_CREATED, LAST_UPDATED};
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
                _MANUFACTURER_ID, _CODE_ID, _CATEGORY_ID, DATE_CREATED, LAST_UPDATED};
    }

    public interface Establishment extends Domain {
        String  TABLE = "establishment",
                NAME = "name";
        String[] COLUMNS = new String[] {_ID, NAME, DATE_CREATED, LAST_UPDATED};
    }

    public interface Address extends Domain {
        String  TABLE = "address",
                STREET = "street",
                NUMBER = "number",
                QUARTER = "quarter",
                CITY = "city",
                STATE = "state",
                ZIPCODE = "zipcode",
                ADDRESS_NAME = "address_name",
                _USER_ID = "_user_id";
        String[] COLUMNS = new String[] {_ID, STREET, NUMBER,
            QUARTER, CITY, STATE, ZIPCODE, ADDRESS_NAME, _USER_ID, DATE_CREATED, LAST_UPDATED};
    }

    public interface User extends Domain {
        String  TABLE = "user",
                EMAIL = "email",
                NAME = "name",
                DOCUMENT = "document",
                TYPE_DOCUMENT = "type_document",
                PASSWORD = "password",
                BYTES_PICTURE = "bytes_picture",
                LOGGED_BY_FACEBOOK = "logged_by_facebook";
        String[] COLUMNS = new String[] {_ID, EMAIL, NAME,
                DOCUMENT, TYPE_DOCUMENT, PASSWORD, BYTES_PICTURE, LOGGED_BY_FACEBOOK, DATE_CREATED, LAST_UPDATED};
    }

    public interface Freight extends Domain {
        String  TABLE = "freight",
                TOTAL_VALUE_DRIVE = "total_value_drive",
                TYPE = "type",
                STARTING_DATE_TIME = "starting_date_time",
                _PURCHASE_ID = "_purchase_id",
                _ADDRESS_ID = "_address_id";
        String[] COLUMNS = new String[] {_ID, TOTAL_VALUE_DRIVE, _PURCHASE_ID,
                _ADDRESS_ID, DATE_CREATED, LAST_UPDATED};
    }

    public interface Purchase extends Domain {
        String  TABLE = "purchase",
                STATUS = "status",
                TOTAL_VALUE = "value_total",
                _USER_ID = "_user_id",
                _ESTABLISHMENT_ID = "_establishment_id";
        String[] COLUMNS = {_ID, STATUS, TOTAL_VALUE, _USER_ID, _ESTABLISHMENT_ID, DATE_CREATED, LAST_UPDATED};
    }

    public interface PurchaseLine extends Domain {
        String  TABLE = "purchase_line",
                QUANTITY = "quantity",
                UNITARY_PRICE = "unitary_price",
                SUB_TOTAL = "sub_total",
                CATEGORY = "category",
                PRODUCT_NAME = "product_name",
                _PRODUCT_ID = "_product_id",
                _PURCHASE_ID = "_purchase_id";
        String[] COLUMNS = {_ID, QUANTITY, UNITARY_PRICE, SUB_TOTAL, CATEGORY, PRODUCT_NAME, _PRODUCT_ID, _PURCHASE_ID, DATE_CREATED, LAST_UPDATED};
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +  User.TABLE + " (" +
                User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                User.EMAIL + " VARCHAR(30)," +
                User.NAME + " VARCHAR(20), " +
                User.DOCUMENT + " NUMERIC, " +
                User.PASSWORD + " VARCHAR(10), " +
                User.TYPE_DOCUMENT + " VARCHAR(5) NOT NULL, " +
                User.BYTES_PICTURE + " BLOB, " +
                User.LOGGED_BY_FACEBOOK + " INT(1), " +
                User.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                User.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Address.TABLE + " (" +
                Address._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Address.STREET + " VARCHAR(20), " +
                Address.NUMBER + " VARCHAR(10), " +
                Address.QUARTER + " VARCHAR(20), " +
                Address.CITY + " VARCHAR(20), " +
                Address.STATE + " VARCHAR(10), " +
                Address.ZIPCODE + " VARCHAR(10) NOT NULL, " +
                Address.ADDRESS_NAME + " VARCHAR(20), " +
                Address._USER_ID + " INTEGER NOT NULL, " +
                Address.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Address.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY(" + Address._USER_ID + ") " +
                " REFERENCES " + User.TABLE + "(" + User._ID + "));");

        db.execSQL("CREATE TABLE " + Manufacturer.TABLE + " (" +
                Manufacturer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Manufacturer.COMPANY_NAME + " VARCHAR(20) UNIQUE NOT NULL, " +
                Manufacturer.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Manufacturer.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Code.TABLE + " (" +
                Code._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Code.CODE + " VARCHAR(20) UNIQUE NOT NULL, " +
                Code.CODE_TYPE + " VARCHAR(10) NOT NULL, " +
                Code.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Code.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");


        db.execSQL("CREATE TABLE " + Category.TABLE + " (" +
                Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Category.NAME + " VARCHAR(20), " +
                Category.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Category.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Product.TABLE + " (" +
                Product._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Product.NAME + " VARCHAR(20), " +
                Product.DESCRIPTION + " TEXT, " +
                Product.UNIT + " VARCHAR(10), " +
                Product._MANUFACTURER_ID + " INTEGER NOT NULL, " +
                Product._CODE_ID + " INTEGER NOT NULL, " +
                Product._CATEGORY_ID + " INTEGER NOT NULL, " +
                Product.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Product.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + Product._MANUFACTURER_ID + ") " +
                " REFERENCES " + Manufacturer.TABLE + "(" + Manufacturer._ID + "), " +
                " FOREIGN KEY( " + Product._CODE_ID + ") " +
                " REFERENCES " + Code.TABLE + "(" + Code._ID + "), " +
                " FOREIGN KEY( " + Product._CATEGORY_ID + ") " +
                " REFERENCES " + Category.TABLE + "(" + Category._ID + "));");

        db.execSQL("CREATE TABLE " + Freight.TABLE + " (" +
                Freight._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Freight.TOTAL_VALUE_DRIVE + " DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                Freight.TYPE + " VARCHAR(20) NOT NULL , " +
                Freight.STARTING_DATE_TIME + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Freight._PURCHASE_ID + " INTEGER NOT NULL, " +
                Freight._ADDRESS_ID + " INTEGER NOT NULL, " +
                Freight.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Freight.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + Freight._ADDRESS_ID + ") " +
                " REFERENCES " + Address.TABLE + "(" + Address._ID + "), " +
                " FOREIGN KEY( " + Freight._PURCHASE_ID + ") " +
                " REFERENCES " + Purchase.TABLE + "(" + Purchase._ID + "));");

        db.execSQL("CREATE TABLE " + Establishment.TABLE + " (" +
                Establishment._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Establishment.NAME + " VARCHAR(20) NOT NULL, " +
                Establishment.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                Establishment.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + Purchase.TABLE + " (" +
                Purchase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                Purchase.STATUS + " VARCHAR(10) NOT NULL, " +
                Purchase.TOTAL_VALUE + " DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                Purchase._USER_ID + " INTEGER NOT NULL, " +
                Purchase._ESTABLISHMENT_ID + " INTEGER NOT NULL, " +
                Purchase.DATE_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                Purchase.LAST_UPDATED + " TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                " FOREIGN KEY( " + Purchase._USER_ID + ") " +
                " REFERENCES " + User.TABLE + "(" + User._ID + "), " +
                " FOREIGN KEY( " + Purchase._ESTABLISHMENT_ID + ") " +
                " REFERENCES " + Establishment.TABLE + "(" + Establishment._ID + "));");

        db.execSQL("CREATE TABLE " + PurchaseLine.TABLE + " (" +
                PurchaseLine._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                PurchaseLine.QUANTITY + " DECIMAL(10,2) NOT NULL DEFAULT 0.0, " +
                PurchaseLine.UNITARY_PRICE + " DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                PurchaseLine.SUB_TOTAL + " DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                PurchaseLine.CATEGORY + " VARCHAR(20), " +
                PurchaseLine.PRODUCT_NAME + " VARCHAR(20), " +
                PurchaseLine._PRODUCT_ID + " INTEGER, " +
                PurchaseLine._PURCHASE_ID + " INTEGER, " +
                PurchaseLine.DATE_CREATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                PurchaseLine.LAST_UPDATED + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " FOREIGN KEY( " + PurchaseLine._PRODUCT_ID + ") " +
                " REFERENCES " + Product.TABLE + "(" + Product._ID + "), " +
                " FOREIGN KEY( " + PurchaseLine._PURCHASE_ID + ") " +
                " REFERENCES " + Purchase.TABLE + "(" + Purchase._ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
     }
}
