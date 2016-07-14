package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.HashMap;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.Stock;

/**
 * Created by adriano on 05/04/16.
 */
public class SKUResource extends AbstractResource<Product> {
    private PartnerResource partnerResource;
    private StockResource stockResource;

    public SKUResource(Context context) {
        super("skus", context);
        partnerResource = new PartnerResource("partners", context);
        stockResource = new StockResource("stock", context);
    }

    public BigDecimal getPriceUnitaryBySkuIdAndPartnerId(String skuId, String partnerName) {

        HashMap<String, String> paramsPartner = new HashMap<>();
        paramsPartner.put("name", partnerName);

        Establishment es = partnerResource.getResource(paramsPartner);

        if (es == null) {
            throw new RuntimeException("Unknown partner with name " + partnerName);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("skuPartner.sku.id", skuId);
        params.put("skuPartner.partner.id", String.valueOf(es.getId()));
        Stock stock = stockResource.getResource(params);
        if (stock == null) {
            throw new RuntimeException("Unknown stock with skuId " + skuId + " and partnerId " + es.getId());
        }

        return stock.getUnitPrice();
    }

    @Override
    public Product bindResourceFromJson(JsonObject jsonObject) {
        Product product = new Product();
        product.setId(jsonObject.get("id").getAsInt());
        product.setName(jsonObject.get("name").getAsString());
        product.setDescription(jsonObject.get("description").getAsString());
        product.setUnit(Product.Unit.valueOf(jsonObject.get("unit").getAsString()));
        product.setCode(jsonObject.get("code").getAsString());

        Manufacturer m = new Manufacturer();
        m.setCompanyName(jsonObject.get("manufacturer").getAsJsonObject().get("name").getAsString());
        product.setManufacturer(m);

        if (jsonObject.get("category") != null) {
            Category category = new Category();
            category.setName(jsonObject.get("category").getAsJsonObject().get("name").getAsString());
            product.setCategory(category);
        }
        product.setPriceUnitary(getPriceUnitaryBySkuIdAndPartnerId(jsonObject.get("id").getAsString(), "Supermercado da Gente"));
        return product;
    }

    @Override
    public String bindJsonFromEntity(Product product) {
        Gson gson = new Gson();
        return gson.toJson(product);
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"id", "name", "description", "unit", "typeCode", "code", "manufacturerId"};
    }
}
