package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.PurchaseLine;

/**
 * Created by adriano on 09/04/16.
 */
public class PurchaseLineResource extends AbstractResource<PurchaseLine> {
    public PurchaseLineResource(String RESOURCE_ROOT, Context context) {
        super(RESOURCE_ROOT, context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {""};
    }

    @Override
    public PurchaseLine bindResourceFromJson(JsonObject jsonObject) {
        return null;
    }

    @Override
    public String bindJsonFromEntity(PurchaseLine purchaseLine) {
        return null;
    }
}
