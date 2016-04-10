package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.Stock;

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
        PurchaseLine item = new PurchaseLine();

        item.setQuantity(jsonObject.get("quantity").getAsBigDecimal());
        item.setUnitaryPrice(jsonObject.get("unitPrice").getAsBigDecimal());
        item.setSubTotal(jsonObject.get("subTotal").getAsBigDecimal());

        if (jsonObject.get("stock") != null) {

            JsonObject jsonStock = jsonObject.get("stock").getAsJsonObject();

            JsonObject jsonSkuPartner = jsonStock.get("skuPartner").getAsJsonObject();

            if (jsonSkuPartner != null && jsonSkuPartner.get("sku") != null && jsonObject.get("partner") != null) {
                Stock st = new Stock(jsonSkuPartner.get("sku").getAsJsonObject().get("id").getAsInt(),
                        jsonSkuPartner.get("partner").getAsJsonObject().get("id").getAsInt());
                item.setStock(st);

                item.setProductName(jsonObject.get("sku").getAsJsonObject().get("name").getAsString());
            }
        }

        return item;
    }

    @Override
    public String bindJsonFromEntity(PurchaseLine purchaseLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (purchaseLine.getQuantity() != null) {
            sb.append("\"quantity\" : \"" + purchaseLine.getQuantity() + "\", ");
        }

        if (purchaseLine.getUnitaryPrice() != null) {
            sb.append("\"unitPrice\" : \"" + purchaseLine.getUnitaryPrice() + "\", ");
        }

        if (purchaseLine.getSubTotal() != null) {
            sb.append("\"subTotal\" : \"" + purchaseLine.getSubTotal() + "\", ");
        }

        if (purchaseLine.getStock() != null) {
            sb.append("\"stock\" : { \"id\" : \"" +purchaseLine.getStock().getId() +"\"}, ");
        }

        if (purchaseLine.getPurchase() != null) {
            sb.append("\"purchase\" : {\"id\" : \"" + purchaseLine.getPurchase().getId() + "\"}");
        }

        sb.append("}");
        return sb.toString();
    }
}
