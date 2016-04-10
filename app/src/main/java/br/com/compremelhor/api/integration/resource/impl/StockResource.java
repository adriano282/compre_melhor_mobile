package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import java.math.BigDecimal;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Stock;

/**
 * Created by adriano on 09/04/16.
 */
public class StockResource extends AbstractResource<Stock> {
    public StockResource(String RESOURCE_ROOT, Context context) {
        super(RESOURCE_ROOT, context);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"skuPartner.sku.id", "skuPartner.partner.id",
        "skuPartner.sku.code", "skuPartner.partner.name"};
    }

    @Override
    public Stock bindResourceFromJson(JsonObject jsonObject) {
        JsonObject skuPartner = jsonObject.get("skuPartner").getAsJsonObject();
        Stock stock = new Stock(skuPartner.get("sku").getAsJsonObject().get("id").getAsInt(),
                skuPartner.get("partner").getAsJsonObject().get("id").getAsInt());

        stock.setUnitPrice(BigDecimal.valueOf(jsonObject.get("unitPrice").getAsDouble()));
        stock.setQuantity(BigDecimal.valueOf(jsonObject.get("quantity").getAsDouble()));
        return stock;
    }

    @Override
    public String bindJsonFromEntity(Stock stock) {
        throw new RuntimeException("Bind json from Stock not implemented yet");
    }
}
