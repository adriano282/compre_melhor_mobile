package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class Item {
    private BigDecimal quantity;
    private BigDecimal priceUnitary;

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
