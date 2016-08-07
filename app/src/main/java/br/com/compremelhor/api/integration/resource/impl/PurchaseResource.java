package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Calendar;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 09/04/16.
 */
public class PurchaseResource extends AbstractResource<Purchase>{

    public PurchaseResource(String RESOURCE_ROOT, Context context) {
        super(RESOURCE_ROOT, context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"status", "user.id", "user.username"};
    }

    @Override
    public Purchase bindResourceFromJson(JsonObject jsonObject) {
        Purchase purchase = new Purchase();
        purchase.setId(jsonObject.get("id").getAsInt());
        purchase.setStatus(Purchase.Status.valueOf(jsonObject.get("status").getAsString()));

        if (!isJsonNullField(jsonObject, "totalValue")) {
            purchase.setTotalValue(jsonObject.get("totalValue").getAsBigDecimal());
        }

        if (!isJsonNullField(jsonObject, "user")) {
            User user = new User();
            user.setId(jsonObject.get("user").getAsJsonObject().get("id").getAsInt());
            purchase.setUser(user);
        }

        if (!isJsonNullField(jsonObject, "freight")) {
            Freight freight = new Freight();
            freight.setId(jsonObject.get("freight").getAsJsonObject().get("id").getAsInt());
            purchase.setFreight(freight);
        }

        Calendar dateCreated = Calendar.getInstance();
        if (!isJsonNullField(jsonObject, "dateCreated")) {
            JsonArray data = jsonObject.get("dateCreated").getAsJsonArray();
            dateCreated.set(data.get(0).getAsInt(), data.get(1).getAsInt(), data.get(2).getAsInt(),
                    data.get(3).getAsInt(), data.get(4).getAsInt());
            purchase.setDateCreated(dateCreated);
        }

        Calendar lastUpdated = Calendar.getInstance();
        if (!isJsonNullField(jsonObject, "lastUpdated")) {
            JsonArray data = jsonObject.get("lastUpdated").getAsJsonArray();
            lastUpdated.set(data.get(0).getAsInt(),data.get(1).getAsInt(), data.get(2).getAsInt(),
                    data.get(3).getAsInt(), data.get(4).getAsInt());
            purchase.setLastUpdated(lastUpdated);
        }

        // Need implement for Freight //

        return purchase;
    }


    @Override
    public String bindJsonFromEntity(Purchase purchase) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (purchase.getId() != 0) {
            sb.append("\"id\" : \"" + purchase.getId() + "\"");
        }

        if (sb.length() > 1) sb.append(",");
        if (purchase.getStatus() != null) {
            sb.append("\"status\" : \"" + purchase.getStatus() + "\"");
        }


        if (sb.length() > 1) sb.append(",");
        if (purchase.getTotalValue() != null) {
            sb.append("\"totalValue\" : \"" + purchase.getTotalValue().doubleValue() + "\"");
        } else {
            sb.append("\"totalValue\" : \"0.00\"");
        }

        if (purchase.getUser() != null) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"user\" : {\"id\" : \"" + purchase.getUser().getId() + "\"}");
        }

        sb.append("}");
        return sb.toString();
    }
}
