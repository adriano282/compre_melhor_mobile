package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Calendar;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.FreightType;

/**
 * Created by adriano on 18/07/16.
 */
public class FreightTypeResource extends AbstractResource<FreightType> {
    public FreightTypeResource(Context context, int partnerId) {
        super("partners/" + partnerId + "/freight_types", context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"typeName", "rideValue"};
    }

    @Override
    public FreightType bindResourceFromJson(JsonObject jsonObject) {
        FreightType ft = new FreightType();

        ft.setRideValue(jsonObject.get("rideValue").getAsBigDecimal());
        ft.setId(jsonObject.get("id").getAsInt());
        ft.setTypeName(jsonObject.get("typeName").getAsString());
        ft.setScheduled(jsonObject.get("scheduled").getAsBoolean());
        ft.setDelayInWorkdays(jsonObject.get("delayWorkdays").getAsInt());
        ft.setDescription((jsonObject.get("description").getAsString()));

        if (!jsonObject.get("availabilityScheduleWorkDays").isJsonNull())
            ft.setAvailabilityScheduleWorkDays(jsonObject.get("availabilityScheduleWorkDays").getAsInt());

        JsonArray ja;
        ja = jsonObject.get("dateCreated").getAsJsonArray();

        Calendar c = Calendar.getInstance();
        c.set(ja.get(0).getAsInt(),
                ja.get(1).getAsInt(),
                ja.get(2).getAsInt(),
                ja.get(3).getAsInt(),
                ja.get(4).getAsInt());

        ft.setDateCreated(c);

        ja = null;
        ja = jsonObject.get("lastUpdated").getAsJsonArray();

        c = Calendar.getInstance();
        c.set(ja.get(0).getAsInt(),
                ja.get(1).getAsInt(),
                ja.get(2).getAsInt(),
                ja.get(3).getAsInt(),
                ja.get(4).getAsInt());
        ft.setLastUpdated(c);
        return ft;
    }

    @Override
    public String bindJsonFromEntity(FreightType freightType) {
        return null;
    }
}
