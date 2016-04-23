package br.com.compremelhor.dao.impl;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 19/10/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DAOCode extends AbstractDAO<Code> {
    private static DAOCode instance;

    public static DAOCode getInstance(Context context) {
        if (instance == null)
            instance = new DAOCode(context);

        return instance;
    }

    private DAOCode(Context context) {
        super(context, Code.class, DatabaseHelper.Code.TABLE, DatabaseHelper.Code.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        Code code = (Code) o;
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.Code._ID, code.getId());
        values.put(DatabaseHelper.Code.CODE, code.getCode());
        values.put(DatabaseHelper.Code.CODE_TYPE, code.getType().toString());
        return values;
    }
}
