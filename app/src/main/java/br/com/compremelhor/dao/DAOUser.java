package br.com.compremelhor.dao;

import android.content.ContentValues;
import android.content.Context;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.User;

public class DAOUser extends AbstractDAO<User> {
    private static DAOUser instance;

    public static DAOUser getInstance(Context context) {
        if (instance == null)
            instance = new DAOUser(context);

        return instance;
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        User user = (User) o;
        ContentValues values = new ContentValues();

        if (user.getId() != 0) {values.put(DatabaseHelper.User._ID, user.getId());}
        values.put(DatabaseHelper.User.EMAIL, user.getEmail());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());
        values.put(DatabaseHelper.User.BYTES_PICTURE, user.getBytesPicture());
        values.put(DatabaseHelper.User.LOGGED_BY_FACEBOOK, user.isLoggedByFacebook());

        if (user.getTypeDocument() != null) {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        } else {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, "");
        }
        return values;
    }

    private DAOUser(Context context) {
        super(context, User.class, DatabaseHelper.User.TABLE, DatabaseHelper.User.COLUMNS);
    }

    public long updateByEmail(EntityModel o) {
        User user = (User) o;
        return getDB().update(DatabaseHelper.User.TABLE, bindContentValues(o),
                DatabaseHelper.User.EMAIL + " = ?", new String[] {user.getEmail()});
    }
}
