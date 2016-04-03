package br.com.compremelhor.api.integration.resource;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;

import br.com.compremelhor.model.Address;

/**
 * Created by adriano on 01/04/16.
 */
public class AddressResource extends AbstractResource<Address> {

    public AddressResource(Context context, int userId) {
        super("users/"+userId+"/addresses", context);
    }
    private String[] columns = {"id", "street", "number", "zipcode", "quarter", "city", "state", "state"};

    @Override
    public Address bindResourceFromJson(JsonObject jsonObject) {
        Address ad = new Address();
        ad.setId(jsonObject.get("id").getAsInt());
        ad.setStreet(jsonObject.get("street").getAsString());
        ad.setCity(jsonObject.get("city").getAsString());
        ad.setQuarter(jsonObject.get("quarter").getAsString());
        ad.setZipcode(jsonObject.get("zipcode").getAsString());
        ad.setNumber(jsonObject.get("number").getAsString());
        ad.setState(jsonObject.get("state").getAsString());

        return ad;
    }

    @Override
    public String bindJsonFromEntity(Address address) {
        Gson gson = new Gson();
        return gson.toJson(address);
    }

   @Override
    public boolean validAttributeName(String attributeName) {
        return Arrays.asList(columns).contains(attributeName.trim());
    }
}
