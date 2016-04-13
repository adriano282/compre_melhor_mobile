package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.model.Purchase;

/**
 * Created by adriano on 12/04/16.
 */
public class FreightResource extends AbstractResource<Freight>{
    public FreightResource(String RESOURCE_ROOT, Context context) {
        super(RESOURCE_ROOT, context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"address.id", "purchase.id", "type"};
    }

    @Override
    public Freight bindResourceFromJson(JsonObject jsonObject) {
        Freight freight = new Freight();

        freight.setId(jsonObject.get("id").getAsInt());
        freight.setValueRide(jsonObject.get("valueRide").getAsBigDecimal());

        JsonObject purchaseJson = jsonObject.get("purchase").getAsJsonObject();
        if (purchaseJson != null) {
            Purchase purchase = new Purchase();
            purchase.setId(purchaseJson.get("id").getAsInt());

            freight.setPurchase(purchase);
        }

        JsonObject addressJson = jsonObject.get("address").getAsJsonObject();
        if (addressJson != null) {
            Address address = new Address();
            address.setId(addressJson.get("id").getAsInt());
            address.setStreet(addressJson.get("street").getAsString());
            address.setQuarter(addressJson.get("quarter").getAsString());
            address.setState(addressJson.get("state").getAsString());
            address.setNumber(addressJson.get("number").getAsString());
            address.setZipcode(addressJson.get("zipcode").getAsString());
            address.setCity(addressJson.get("city").getAsString());

            if (addressJson.get("user").getAsJsonObject() != null)
                address.setUserId(addressJson.get("user").getAsJsonObject().get("id").getAsInt());

            freight.setAshipAddress(address);
        }

        Freight.FreightType type = Freight.FreightType
                .valueOf(jsonObject.get("type").getAsJsonObject().getAsString());

        freight.setType(type);

        FreightSetup freightSetup = new FreightSetup();

        return freight;
    }

    @Override
    public String bindJsonFromEntity(Freight freight) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (freight.getId() != 0) {
            sb.append("\"id\" : \"" + freight.getId() + "\"");
        }

        if (freight.getType() != null) {
            sb.append(", \"type\" : \"" + freight.getType().toString() + "\"");
        }

        if (freight.getValueRide() != null) {
            sb.append(", \"valueRide\" : \"" + freight.getValueRide() + "\"");
        }

        if (freight.getShipAddress() != null) {
            sb.append(", \"shipAddress\" : {\"id\" : \"" + freight.getShipAddress().getId() + "\" }" );
        }

        if (freight.getPurchase() != null) {
            sb.append(", \"purchase\" : {\"id\" : \"" + freight.getPurchase().getId() + "\"}" );
        }

        sb.append("}");
        return sb.toString();
    }
}
