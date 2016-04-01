package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 29/09/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOUser extends DAO {

    public DAOUser(Context context) {
        super(context);
    }

    public User getUserById(int id) {
        try(Cursor cursor = getDB().query(
                DatabaseHelper.User.TABLE,
                DatabaseHelper.User.COLUMNS,
                DatabaseHelper.User._ID + " = ?" ,
                new String[] {String.valueOf(id)}, null, null, null)) {

            User user = null;
            if (cursor.moveToFirst()) {
                user =  (User) getBind().bind(new User(), cursor);
            }
            return user;
        }
    }

    public User getUserByEmail(String email) {
        try(Cursor cursor = getDB().query(
            DatabaseHelper.User.TABLE,
            DatabaseHelper.User.COLUMNS,
            DatabaseHelper.User.EMAIL + " = ?" ,
        new String[] {email}, null, null, null)){
            User user = null;
            if (cursor.moveToNext()) {
                user =  (User) getBind().bind(new User(), cursor);
            }
            return user;
        }
    }

    public long insert(EntityModel o) {
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

        long result = getDB().insert(DatabaseHelper.User.TABLE, null, values);

        if (result != -1) {
            o.setId(getUserByEmail(user.getEmail()).getId());
        }
        return result;
    }

    public long insertOrUpdate(EntityModel o) {
        User user = (User) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.User.EMAIL, user.getEmail());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());
        values.put(DatabaseHelper.User.BYTES_PICTURE, user.getBytesPicture());
        values.put(DatabaseHelper.User.LOGGED_BY_FACEBOOK, user.isLoggedByFacebook() ? 1 : 0);

        if (user.getTypeDocument() != null) {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        } else {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, "");
        }

        if ((user.getId() == 0 || user.getId() == 0))
            return getDB().insert(DatabaseHelper.User.TABLE, null, values);

        return new Long(getDB().update(DatabaseHelper.User.TABLE, values,
                DatabaseHelper.User._ID + " = ?", new String[] {String.valueOf(user.getId())}));
    }

    public long updateByEmail(EntityModel o) {
        User user = (User) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.User._ID, user.getId());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());
        values.put(DatabaseHelper.User.BYTES_PICTURE, user.getBytesPicture());
        values.put(DatabaseHelper.User.LOGGED_BY_FACEBOOK, user.isLoggedByFacebook() ? 1 : 0);

        if (user.getTypeDocument() != null) {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        } else {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, "");
        }

        return new Long(getDB().update(DatabaseHelper.User.TABLE, values,
                DatabaseHelper.User.EMAIL + " = ?", new String[] {user.getEmail()}));
    }

    private boolean userAlreadyRegistered(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().equals(""))
            return false;

        User userStored =  getUserByEmail(user.getEmail());
        if (userStored != null) {
            user.setId(userStored.getId());
            user.setDocument(userStored.getDocument());
            user.setName(userStored.getName());
            user.setPassword(userStored.getPassword());
            user.setTypeDocument(userStored.getTypeDocument() == null? null:userStored.getTypeDocument().getType().toString());
            return true;
        }
        return false;
    }
}
