package br.com.compremelhor.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.com.compremelhor.useful.DataBind;

/**
 * Created by adriano on 25/08/15.
 */
public abstract class DAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private DataBind dataBind;

    public DAO(Context context) {
        helper = new DatabaseHelper(context);
        dataBind = new DataBind();
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

    public DataBind getBind() {
        return dataBind;
    }
}
