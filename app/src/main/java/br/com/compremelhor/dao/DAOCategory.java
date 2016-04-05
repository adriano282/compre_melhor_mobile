package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 19/10/15.
 */
public class DAOCategory extends AbstractDAO<Category> {
    private static DAOCategory instance;

    public static  DAOCategory getInstance(Context context) {
        if (instance == null) {
            instance = new DAOCategory(context);
        }
        return instance;
    }

    private DAOCategory(Context context) {
        super(context, Category.class, DatabaseHelper.Category.TABLE, DatabaseHelper.Category.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        Category c = (Category) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Category._ID, c.getId());
        values.put(DatabaseHelper.Category.NAME, c.getName());

        return values;
    }
}
