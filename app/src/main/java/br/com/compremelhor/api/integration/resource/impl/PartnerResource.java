package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Establishment;

/**
 * Created by adriano on 09/04/16.
 */
public class PartnerResource extends AbstractResource<Establishment> {
    public PartnerResource(String RESOURCE_ROOT, Context context) {
        super(RESOURCE_ROOT, context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"name"};
    }

    @Override
    public Establishment bindResourceFromJson(JsonObject jsonObject) {
        Establishment establishment = new Establishment();

        establishment.setId(jsonObject.get("id").getAsInt());
        establishment.setName(jsonObject.get("name").getAsString());
        return establishment;
    }

    @Override
    public String bindJsonFromEntity(Establishment establishment) {
        return null;
    }
}
