package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Product;

public class DAOProduct extends AbstractDAO {
    private static DAOProduct instance;

    public static DAOProduct getInstance(Context context) {
        if (instance == null)
            instance = new DAOProduct(context);

        return instance;
    }
    public DAOProduct(Context context) {
        super(context, Product.class, DatabaseHelper.Product.TABLE, DatabaseHelper.Product.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        return null;
    }
}
