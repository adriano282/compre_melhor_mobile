package br.com.compremelhor.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import java.util.Calendar;

import br.com.compremelhor.dao.AbstractDAO;
import br.com.compremelhor.model.EntityModel;
import br.com.compremelhor.model.FreightType;
import br.com.compremelhor.util.helper.DatabaseHelper;

/**
 * Created by adriano on 18/07/16.
 */
public class DAOFreightType extends AbstractDAO<FreightType> {
    private static DAOFreightType instance;
    public static DAOFreightType getInstance(Context context) {
        if (instance == null)
            instance = new DAOFreightType(context);

        return instance;
    }

    private DAOFreightType(Context context) {
        super(context, FreightType.class, DatabaseHelper.FreightType.TABLE, DatabaseHelper.FreightType.COLUMNS);
    }

    @Override
    public ContentValues bindContentValues(EntityModel o) {
        FreightType ft = (FreightType) o;

        ContentValues values = new ContentValues();

        if (ft.getId() != 0)
        values.put(DatabaseHelper.FreightType._ID, ft.getId());

        values.put(DatabaseHelper.FreightType.RIDE_VALUE, String.valueOf(ft.getRideValue()));
        values.put(DatabaseHelper.FreightType.DESCRIPTION, ft.getDescription());
        values.put(DatabaseHelper.FreightType.TYPE_NAME, ft.getTypeName());
        values.put(DatabaseHelper.FreightType.SCHEDULED, ft.isScheduled());
        values.put(DatabaseHelper.FreightType.DELAY_WORK_DAYS, ft.getDelayInWorkdays());
        values.put(DatabaseHelper.FreightType.AVAILABILITY_SCHEDULE_WORK_DAYS, ft.getAvailabilityScheduleWorkDays());
        values.put(DatabaseHelper.FreightType._ESTABLISHMENT_ID, ft.getEstablishmentId());
        values.put(DatabaseHelper.FreightType.DATE_CREATED, ft.getDateCreated() == null ? Calendar.getInstance().getTimeInMillis() : ft.getDateCreated().getTimeInMillis());
        values.put(DatabaseHelper.FreightType.LAST_UPDATED, ft.getLastUpdated() == null ? Calendar.getInstance().getTimeInMillis() : ft.getLastUpdated().getTimeInMillis());

        return values;
    }
}
