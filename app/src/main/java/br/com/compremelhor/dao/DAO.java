package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.useful.DataBind;

/**
 * Created by adriano on 25/08/15.
 */
public abstract class DAO {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private DataBind dataBind;

    public abstract ContentValues bindContentValues(EntityModel o);

    public DAO(Context context) {
        helper = new DatabaseHelper(context);
        dataBind = new DataBind(context);
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

    public void delete(int id, String table) {
        String where [] = new String[] {String.valueOf(id)};
        getDB().delete(
                table,
                "_id = ?",
                where);
    }

    public long insert(EntityModel o, String table) {
        return getDB().insert(table, null, bindContentValues(o));
    }

    public long insertOrUpdate(EntityModel o, String table) {
        ContentValues values = bindContentValues(o);
        if ((o.getId() == 0 || o.getId() == 0))
            return getDB().insert(table, null, values);

        return getDB().update(table, values,
                DatabaseHelper.User._ID + " = ?", new String[] {String.valueOf(o.getId())});
    }

}
