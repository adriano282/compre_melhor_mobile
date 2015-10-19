package br.com.compremelhor.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.DomainEntity;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOCode extends DAO {
    public DAOCode(Context context) {
        super(context);
    }

    @Override
    public long insertOrUpdate(DomainEntity o) {
        Code code = (Code) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Code._ID, code.getId());
        values.put(DatabaseHelper.Code.CODE, code.getCode());
        values.put(DatabaseHelper.Code.CODE_TYPE, code.getType());

        if (code.getId() == null)
            return getDB().insert(DatabaseHelper.Code.TABLE, null, values);

        return getDB().update(DatabaseHelper.Code.TABLE, values,
                DatabaseHelper.Code._ID + " = ?", new String[]{code.getId().toString()});
    }


    public Code getCodeById(Long id) {
        if (id == null)
            return null;

        try (Cursor cursor = getDB().query(DatabaseHelper.Code.TABLE,
                DatabaseHelper.Code.COLUMNS,
                DatabaseHelper.Code._ID + " = ?",
                new String[] {id.toString()}, null, null, null)) {
            if (cursor.moveToNext()) {
                return (Code) getBind().bind(new Code(), cursor);
            }
            return null;
        }
    }

    public Code getCodeByCode(String code) {
        if (code == null || code.equals(""))
            return null;

        try (Cursor cursor = getDB().query(DatabaseHelper.Code.TABLE,
                DatabaseHelper.Code.COLUMNS,
                DatabaseHelper.Code.CODE + " = ?",
                new String[] {code}, null, null, null)) {
            if (cursor.moveToNext()) {
                return (Code) getBind().bind(new Code(), cursor);
            }
            return null;
        }
    }
}
