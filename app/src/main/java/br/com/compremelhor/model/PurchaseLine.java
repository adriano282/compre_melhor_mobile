package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class PurchaseLine {
    private BigDecimal quantity;
    private BigDecimal priceUnitary;
    private Product product;

    public PurchaseLine() {}

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return this.product;
    }

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
