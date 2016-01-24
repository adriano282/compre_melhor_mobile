package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.util.Locale;

import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.DomainEntity;
import br.com.compremelhor.model.PurchaseLine;

public class DAOCart extends DAO {
    private Cart cart;
    private static DAOCart instance;

    public static DAOCart getInstance(Context context) {
        if (instance == null)
            instance = new DAOCart(context);
        return instance;
    }

    private DAOCart(Context context) {super(context);}

    @Override
    public long insertOrUpdate(DomainEntity o) {
        cart = (Cart) o;
        if (cart.getId() == null)
            return insertCart();

        return updateCart();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Cart getCart() {
        try (Cursor cursor = getDB().query(DatabaseHelper.Cart.TABLE,
                DatabaseHelper.Cart.COLUMNS,
                DatabaseHelper.Cart._ID + " = ?",
                new String[] {"1"}, null, null,null)) {


            Cart cart = null;
            if (cursor.moveToFirst()) {
                cart = (Cart) getBind().bind(new Cart(), cursor);
            }

            cursor.close();

            Cursor items = getDB()
                    .rawQuery("SELECT pl." + DatabaseHelper.PurchaseLine._ID  + ", "
                        + "pl."+ DatabaseHelper.PurchaseLine.UNITARY_PRICE + ", "
                        + "pl."+ DatabaseHelper.PurchaseLine.QUANTITY + ", "
                        + "pl."+ DatabaseHelper.PurchaseLine.SUB_TOTAL + ", "
                        + "pl."+ DatabaseHelper.PurchaseLine.CATEGORY + ", "
                        + "pl." + DatabaseHelper.PurchaseLine._PRODUCT_ID + ", "
                        + "pl." + DatabaseHelper.PurchaseLine.PRODUCT_NAME + ", "
                        + "pl." + DatabaseHelper.PurchaseLine.DATE_CREATED + ", "
                        + "pl." + DatabaseHelper.PurchaseLine.LAST_UPDATED + " "
                    + "FROM " + DatabaseHelper.PurchaseLine.TABLE + " as pl "
                    + "INNER JOIN " + DatabaseHelper.CartPurchaseLine.TABLE + " as cl "
                    + " ON cl." + DatabaseHelper.CartPurchaseLine._ID_PURCHASE_LINE
                            + " = pl." + DatabaseHelper.PurchaseLine._ID + " "
                    + " WHERE cl." + DatabaseHelper.CartPurchaseLine._ID_CART + " = ?",
                    new String[]{"1"});


            items.moveToFirst();

            while (!items.isAfterLast()) {
                cart.addItem((PurchaseLine) getBind().bind(new PurchaseLine(), items));
                items.moveToNext();
            }

            items.close();
            return cart;
        }
    }

    public long removeItem(PurchaseLine item) {
        if (getDB().delete(DatabaseHelper.PurchaseLine.TABLE,
                DatabaseHelper.PurchaseLine._ID + " = ?",
                new String[] {item.getId().toString()}) != -1) {

            return getDB().delete(DatabaseHelper.CartPurchaseLine.TABLE,
                    DatabaseHelper.CartPurchaseLine._ID_PURCHASE_LINE + " = ?",
                    new String[]{item.getId().toString()});
        }
        return -1;
    }

    public long addItem(PurchaseLine item) {
        if (item.getId() == null)
            return insertItem(item);

        return updateItem(item);
    }

    private long insertItem(PurchaseLine item) {
        long resultInserPurchaseLine = getDB().insert(DatabaseHelper.PurchaseLine.TABLE, null, getPurchaseLineValues(item));
        item.setId(resultInserPurchaseLine);
        long resultItem =  getDB().insert(DatabaseHelper.CartPurchaseLine.TABLE, null, getItemValues(item));

        Log.d("DATABASE", "Result from insert purchase line: " + resultInserPurchaseLine);
        Log.d("DATABASE", "Result from insert item on cart: " + resultItem);
        return resultItem;
    }

    private ContentValues getPurchaseLineValues(PurchaseLine item) {
        ContentValues content = new ContentValues();
        content.put(DatabaseHelper.PurchaseLine._PRODUCT_ID, item.getProduct() == null ? 0 : item.getProduct().getId());
        content.put(DatabaseHelper.PurchaseLine.QUANTITY, item.getQuantity().toString());
        content.put(DatabaseHelper.PurchaseLine.UNITARY_PRICE, item.getUnitaryPrice().toString());
        content.put(DatabaseHelper.PurchaseLine.SUB_TOTAL, item.getSubTotal().toString());
        content.put(DatabaseHelper.PurchaseLine.CATEGORY, item.getCategory());
        content.put(DatabaseHelper.PurchaseLine.PRODUCT_NAME, item.getProductName());

        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, new Locale("BR"));
        content.put(DatabaseHelper.PurchaseLine.DATE_CREATED, format.format(item.getDateCreated().getTime()));
        content.put(DatabaseHelper.PurchaseLine.LAST_UPDATED, format.format(item.getLastUpdated().getTime()));

        return content;
    }

    private long updateItem(PurchaseLine item) {
        long result = getDB().update(DatabaseHelper.PurchaseLine.TABLE, getPurchaseLineValues(item),
                DatabaseHelper.PurchaseLine._ID + " = ?",
                new String[]{item.getId().toString()});

        Log.d("DATABASE", "Result from updateItem: " + result);
        return result;
    }

    private long updateCart() {
        long result = getDB().update(DatabaseHelper.Cart.TABLE, getCartValues(cart),
                DatabaseHelper.Cart._ID + " = ?", new String[] {cart.getId().toString()});

        Log.d("DATABASE", "Result from updateCart: " + result);
        return result;
    }

    private long insertCart() {
        long r = getDB().insert(DatabaseHelper.Cart.TABLE, null, getCartValues(cart));
        if (r == -1)
            return r;

        cart.setId(r);
        return r;
    }

    private ContentValues getItemValues(PurchaseLine item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CartPurchaseLine._ID_CART, cart.getId());
        values.put(DatabaseHelper.CartPurchaseLine._ID_PURCHASE_LINE, item.getId());
        Log.d("DATABASE", "Content values: " + values);
        return values;
    }

    private ContentValues getCartValues(Cart c) {
        ContentValues content = new ContentValues();
        content.put(DatabaseHelper.Cart._ID, c.getId());
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, new Locale("BR"));
        content.put(DatabaseHelper.Cart.DATE_CREATED, format.format(c.getDateCreated().getTime()));
        content.put(DatabaseHelper.Cart.LAST_UPDATED, format.format(c.getLastUpdated().getTime()));
        return content;
    }
}
