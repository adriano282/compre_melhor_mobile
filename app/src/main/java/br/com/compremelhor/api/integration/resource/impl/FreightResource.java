package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonArray;
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
        freight.setRideValue(jsonObject.get("rideValue").getAsBigDecimal());

        if (jsonObject.get("purchase") != null) {
            JsonObject purchaseJson = jsonObject.get("purchase").getAsJsonObject();
            Purchase purchase = new Purchase();
            purchase.setId(purchaseJson.get("id").getAsInt());

            freight.setPurchase(purchase);
        }


        if (jsonObject.get("address") != null) {
            JsonObject addressJson = jsonObject.get("address").getAsJsonObject();
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

        if (jsonObject.get("freightType") != null) {
            freight.setFreightTypeId(jsonObject.get("freightType").getAsJsonObject().get("id").getAsInt());
            freight.setType(jsonObject.get("freightType").getAsJsonObject().get("typeName").getAsString());
        }

        FreightSetup freightSetup = new FreightSetup();

        if (jsonObject.get("startingDate") != null) {
            if (!jsonObject.get("startingDate").isJsonNull()) {
                JsonArray dateArray = jsonObject.get("startingDate").getAsJsonArray();
                freightSetup.setYear(dateArray.get(0).getAsInt());
                freightSetup.setMonth(dateArray.get(1).getAsInt());
                freightSetup.setDayOfMonth(dateArray.get(2).getAsInt());
            }
        }

        if (jsonObject.get("startingTime") != null) {
            if (!jsonObject.get("startingTime").isJsonNull()) {
                JsonArray timeArray = jsonObject.get("startingTime").getAsJsonArray();
                freightSetup.setHour(timeArray.get(0).getAsInt());
                freightSetup.setMinute(timeArray.get(1).getAsInt());
            }
        }

        freight.setFreightSetup(freightSetup);
        return freight;
    }

    @Override
    public String bindJsonFromEntity(Freight freight) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (sb.length() > 1)
            sb.append(", ");
        if (freight.getId() != 0) {
            sb.append("\"id\" : \"" + freight.getId() + "\"");
        }

        if (freight.getType() != null) {
            if (sb.length() > 1)
                sb.append(", ");

            sb.append("\"type\" : \"" + freight.getType() + "\"");
        }

        if (freight.getRideValue() != null) {
            if (sb.length() > 1)
                sb.append(", ");


            sb.append("\"rideValue\" : \"" + freight.getRideValue() + "\"");
        }

        if (sb.length() > 1)
            sb.append(", ");

        sb.append("\"freightType\" : {\"id\" : \"" + freight.getFreightTypeId() + "\" }" );

        if (freight.getShipAddress() != null) {
            if (sb.length() > 1)
                sb.append(", ");

            sb.append("\"shipAddress\" : {\"id\" : \"" + freight.getShipAddress().getId() + "\" }" );
        }

        if (freight.getPurchase() != null) {
            if (sb.length() > 1)
                sb.append(", ");

            sb.append("\"purchase\" : {\"id\" : \"" + freight.getPurchase().getId() + "\"}" );
        }

        if (freight.getFreightSetup() != null) {
            if (sb.length() > 1)
                sb.append(", ");

            sb.append("\"startingDate\" : [ " +
                    freight.getFreightSetup().getYear() +
                    ", " + freight.getFreightSetup().getMonth() +
                    ", " + freight.getFreightSetup().getDayOfMonth() + "] , ");

            sb.append("\"startingTime\" : [ " +
                    freight.getFreightSetup().getHour() +
                    ", " + freight.getFreightSetup().getMinute() + "]");
        }
        sb.append("}");
        return sb.toString();
    }
}
