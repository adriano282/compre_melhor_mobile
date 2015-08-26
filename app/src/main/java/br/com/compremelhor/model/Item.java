package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "Item")
public class Item {
    @DatabaseField
    private BigDecimal quantity;
    @DatabaseField
    private BigDecimal priceUnitary;

    public Item() {}

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceUnitary() {
        return priceUnitary;
    }

    public void setPriceUnitary(BigDecimal priceUnitary) {
        this.priceUnitary = priceUnitary;
    }
}
