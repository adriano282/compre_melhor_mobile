package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.Product;

/**
 * Created by adriano on 18/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOProduct extends DAO {

    @Override
    public long insertOrUpdate(EntityModel o) {
        return 0;
    }

    public DAOProduct(Context context) {
        super(context);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        return null;
    }

    public Product getProductById(Long id) {
        if (id == null)
            return null;

        try(Cursor cursor = getDB().query(DatabaseHelper.Product.TABLE,
                DatabaseHelper.Product.COLUMNS,
                DatabaseHelper.Product._ID,
                new String[] {id.toString()}, null, null, null)) {

            if (cursor.moveToNext()) {
                return (Product) getBind().bind(new Product(), cursor);
            }
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try(Cursor cursor = getDB().query(DatabaseHelper.Product.TABLE,
                DatabaseHelper.Product.COLUMNS,
                null, null, null, null, null)) {

            List<Product> products = new ArrayList<Product>();
            while (cursor.moveToNext()) {
                Product product = (Product) getBind().bind(new Product(), cursor);
                products.add(product);
            }
            return products;
        }

    }
}
