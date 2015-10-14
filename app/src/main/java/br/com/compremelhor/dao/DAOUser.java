package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import br.com.compremelhor.model.User;

/**
 * Created by adriano on 29/09/15.
 */
public class DAOUser extends DAO {

    public DAOUser(Context context) {
        super(context);
    }

    public User getUser() {
        Cursor cursor = getDB().query(
            DatabaseHelper.User.TABLE,
            DatabaseHelper.User.COLUMNS,
            null, null, null, null, null);

        User user = null;
        if (cursor.moveToNext()) {
            user =  (User) getBind().bind(new User(), cursor);
        }

        cursor.close();
        return user;
    }

    public int insertOrUpdate(User user) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.User.EMAIL, user.getEmail());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());

        if (user.getId() == null || user.getId() == 0)
            return (int) getDB().insert(DatabaseHelper.User.TABLE, null, values);

        return getDB().update(DatabaseHelper.User.TABLE, values,
                DatabaseHelper.User._ID + " = ?", new String[] {user.getId().toString()});
    }
}
