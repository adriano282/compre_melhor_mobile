package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;

import java.text.DateFormat;
import java.util.Locale;

import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.DomainEntity;
import br.com.compremelhor.model.PurchaseLine;

public class DAOCart extends DAO {
    private Cart cart;
    public DAOCart(Context context) {super(context);}

    @Override
    public long insertOrUpdate(DomainEntity o) {
        cart = (Cart) o;
        if (cart.getId() == null)
            return insertCart();

        return updateCart();
    }

    public Cart getCart() {
        return cart;
    }

    public long removeItem(PurchaseLine item) {
        return getDB().delete(DatabaseHelper.CartPurchaseLine.TABLE,
                DatabaseHelper.CartPurchaseLine._ID_PURCHASE_LINE + " = ?",
                new String[]{item.getId().toString()});
    }

    public long addItem(PurchaseLine item) {
        if (item.getId() == null)
            return insertItem(item);

        return updateItem(item);
    }

    private long insertItem(PurchaseLine item) {
        return getDB().insert(DatabaseHelper.CartPurchaseLine.TABLE, null, getItemValues(item));
    }

    private long updateItem(PurchaseLine item) {
        return getDB().update(DatabaseHelper.CartPurchaseLine.TABLE, getItemValues(item),
                DatabaseHelper.CartPurchaseLine._ID_CART + " = ? and " +
                        DatabaseHelper.CartPurchaseLine._ID_PURCHASE_LINE + " = ?",
                new String[]{cart.getId().toString(), item.getId().toString()});
    }

    private long updateCart() {
        return getDB().update(DatabaseHelper.Cart.TABLE, getCartValues(cart),
                DatabaseHelper.Cart._ID + " = ?", new String[] {cart.getId().toString()});
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
