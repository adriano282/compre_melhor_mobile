package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.util.DataBind;
import br.com.compremelhor.util.helper.DatabaseHelper;

/**
 * Created by adriano on 25/08/15.
 */
public abstract class AbstractDAO<T> implements IDAO<T>, Serializable {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private DataBind dataBind;
    private Class<T> clazz;

    private final String[] COLUMNS;
    private final String TABLE;

    public AbstractDAO(Context context, Class clazz, String table, String[] columns) {
        this.TABLE = table;
        this.COLUMNS = columns;
        this.clazz = clazz;
        this.helper = new DatabaseHelper(context);
        this.dataBind = new DataBind(context);
    }

    public abstract ContentValues bindContentValues(EntityModel o);

    public T find(int id) {
        Cursor cursor = null;

        try {
            cursor = getDB().query(TABLE, COLUMNS,
                    DatabaseHelper.Domain._ID + " = ?",
                    new String[] {String.valueOf(id)},
                    null, null, null);
            if (cursor.moveToNext()) {
                return (T) dataBind.bind(clazz, cursor);
            }
            return null;

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public void delete(int id) {
        String where [] = new String[] {String.valueOf(id)};
        getDB().delete(TABLE, "_id = ?", where);
    }

    public long insert(EntityModel o) {
        return getDB().insert(TABLE, null, bindContentValues(o));
    }

    public long insertOrUpdate(EntityModel o) {
        ContentValues values = bindContentValues(o);

         if (o.getId() == 0) {
            return getDB().insert(TABLE, null, values);
        } else {
            return getDB().update(TABLE, values,
                    DatabaseHelper.User._ID + " = ?", new String[]{String.valueOf(o.getId())});
        }
    }

    public List<T> findAllByForeignId(String idName, int idValue) {
        return findAllByAttribute(idName, String.valueOf(idValue));
    }

    public List<T> findAllByAttribute(String attributeName, String attributeValue) {
        if (!Arrays.asList(COLUMNS).contains(attributeName))
            throw new IllegalArgumentException("Unknown attribute name: " + attributeName);

        Cursor cursor = null;
        try {
            cursor = getDB().query(TABLE, COLUMNS,
                    attributeName + " = ?",
                    new String[] {attributeValue}, null, null, null);

            List<T> entities = new ArrayList<>();
            while (cursor.moveToNext()) {
                T entity = (T) dataBind.bind(clazz, cursor);
                entities.add(entity);
            }
            return entities;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public T findByAttribute(String attributeName, String attributeValue) {
        if (!Arrays.asList(COLUMNS).contains(attributeName))
            throw new IllegalArgumentException("Unknown attribute name: " + attributeName);

        Cursor cursor = null;
        try {
            cursor = getDB().query(TABLE, COLUMNS,
                    attributeName + " = ?",
                    new String[] {attributeValue}, null, null, null);

            if (cursor.moveToNext()) {
                return (T) dataBind.bind(clazz, cursor);
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    protected SQLiteDatabase getDB() {
        if (db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public void cleanDatabase() {
        for (String table : helper.TABLES) {
            getDB().execSQL("DROP TABLE IF EXISTS " + table);
        }

        helper.onCreate(helper.getWritableDatabase());
    }

}
