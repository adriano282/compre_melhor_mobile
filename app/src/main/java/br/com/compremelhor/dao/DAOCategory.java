package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOCategory extends DAO {
    public DAOCategory(Context context) {
        super(context);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        Category c = (Category) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Category._ID, c.getId());
        values.put(DatabaseHelper.Category.NAME, c.getName());

        return values;
    }

    public Category getCategoryByName(String name) {
        if (name == null)
            return null;

        try (Cursor cursor = getDB().query(DatabaseHelper.Category.TABLE,
                DatabaseHelper.Category.COLUMNS,
                DatabaseHelper.Category.NAME + " = ?",
                new String[] {name.toString()}, null, null, null)) {

            if (cursor.moveToNext()) {
                return (Category) getBind().bind(new Category(), cursor);
            }
            return null;
        }
    }

    public Category getCategoryById(int id) {
        try (Cursor cursor = getDB().query(DatabaseHelper.Category.TABLE,
                DatabaseHelper.Category.COLUMNS,
                DatabaseHelper.Category._ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null)) {

            if (cursor.moveToNext()) {
                return (Category) getBind().bind(new Category(), cursor);
            }
            return null;
        }
    }
}
