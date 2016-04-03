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

    public void delete(Long id, String table) {
        String where [] = new String[] {id.toString()};
        getDB().delete(
                table,
                "_id = ?",
                where);
    }

    public long insert(EntityModel o) {
        return getDB().insert(DatabaseHelper.User.TABLE, null, bindContentValues(o));
    }

    public long insertOrUpdate(EntityModel o) {
        ContentValues values = bindContentValues(o);
        if ((o.getId() == 0 || o.getId() == 0))
            return getDB().insert(DatabaseHelper.User.TABLE, null, values);

        return new Long(getDB().update(DatabaseHelper.User.TABLE, values,
                DatabaseHelper.User._ID + " = ?", new String[] {String.valueOf(o.getId())}));
    }

}
