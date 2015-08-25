package br.com.compremelhor.dao;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

/**
 * Created by adriano on 25/08/15.
 */
public class DAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DAO(Context context) {
        helper = new DatabaseHelper(context);
    }

    protected SQLiteDatabase getDB() {
        if (db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }
    public void close() {
        helper.close();
    }
}
