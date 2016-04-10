package br.com.compremelhor.model;

import java.math.BigDecimal;

public class Stock extends EntityModel {
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private final int skuId;
    private final int partnerId;

    public Stock(int skuId, int partnerId) {
        this.skuId = skuId;
        this.partnerId = partnerId;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
