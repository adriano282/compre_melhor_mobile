package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Sync;

/**
 * Created by adriano on 23/07/16.
 */
public class SyncResource extends AbstractResource<Sync> {

    public SyncResource(Context context) {
        super("sync", context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"id", "entityName", "entityId", "action", "mobileUserIdRef"};
    }

    @Override
    public Sync bindResourceFromJson(JsonObject jsonObject) {
        String action = jsonObject.get("action") != null ?
                jsonObject.get("action").getAsString() :
                "";

        String entityName = jsonObject.get("entityName") != null ?
                jsonObject.get("entityName").getAsString() :
                "";

        int entityId = jsonObject.get("entityId") != null ?
                jsonObject.get("entityId").getAsInt() : 0;

        int id = jsonObject.get("id") != null ?
                jsonObject.get("id").getAsInt() : 0;

        Sync s = new Sync();
        s.setAction(action);
        s.setEntityName(entityName);
        s.setEntityId(entityId);
        s.setId(id);
        return s;
    }

    @Override
    public String bindJsonFromEntity(Sync sync) {
        return null;
    }
}
