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

    public User getUserById(Long id) {
        try(Cursor cursor = getDB().query(
                DatabaseHelper.User.TABLE,
                DatabaseHelper.User.COLUMNS,
                DatabaseHelper.User._ID + " = ?" ,
                new String[] {id.toString()}, null, null, null)) {

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

        values.put(DatabaseHelper.User.EMAIL, user.getEmail());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());
        values.put(DatabaseHelper.User.BYTES_PICTURE, user.getBytesPicture());

        if (user.getTypeDocument() != null) {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        } else {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, "");
        }

        return getDB().insert(DatabaseHelper.User.TABLE, null, values);
    }

    public long insertOrUpdate(EntityModel o) {
        User user = (User) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.User.EMAIL, user.getEmail());
        values.put(DatabaseHelper.User.NAME, user.getName());
        values.put(DatabaseHelper.User.DOCUMENT, user.getDocument());
        values.put(DatabaseHelper.User.PASSWORD, user.getPassword());
        values.put(DatabaseHelper.User.BYTES_PICTURE, user.getBytesPicture());

        if (user.getTypeDocument() != null) {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, user.getTypeDocument().toString());
        } else {
            values.put(DatabaseHelper.User.TYPE_DOCUMENT, "");
        }

        if ((user.getId() == null || user.getId() == 0))
            return getDB().insert(DatabaseHelper.User.TABLE, null, values);

        return new Long(getDB().update(DatabaseHelper.User.TABLE, values,
                DatabaseHelper.User._ID + " = ?", new String[] {user.getId().toString()}));
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
