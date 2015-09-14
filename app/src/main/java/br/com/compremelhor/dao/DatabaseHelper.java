package br.com.compremelhor.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adriano on 25/08/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "CompreMelhor.db";
    private static int DATABASE_VERSION = 1;

    public static class Address {
        public static final String TABLE = "address";
        public static final String _ID = "_id";
        public static final String STREET = "street";
        public static final String NUMBER = "number";
        public static final String QUARTER = "quarter";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String ZIPCODE = "zipcode";
        public static final String[] COLUMNS = new String[] {_ID, STREET, NUMBER,
            QUARTER, CITY, STATE, ZIPCODE };
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Address.TABLE + " (" +
                Address._ID + " INTEGER PRIMARY KEY, " +
                Address.STREET + " TEXT, " +
                Address.NUMBER + " TEXT, " +
                Address.QUARTER + " TEXT, " +
                Address.CITY + " TEXT, " +
                Address.STATE + " TEXT, " +
                Address.ZIPCODE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE address");
    }
}
