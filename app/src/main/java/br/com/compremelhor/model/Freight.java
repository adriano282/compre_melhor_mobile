package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class Freight extends EntityModel {
    private Address shipAddress;
    private BigDecimal valueRide;
    private Purchase purchase;
    private FreightType type;
    private FreightSetup freightSetup;

    public Freight() {}

    public Address getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(Address shipAddress) {
        this.shipAddress = shipAddress;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public FreightType getType() {
        return type;
    }

    public void setType(FreightType type) {
        this.type = type;
    }

    public FreightSetup getFreightSetup() {
        return freightSetup;
    }

    public void setFreightSetup(FreightSetup freightSetup) {
        this.freightSetup = freightSetup;
    }

    public Address getshipAddress() {
        return shipAddress;
    }

    public void setAshipAddress(Address shipAddress) {
        this.shipAddress = shipAddress;
    }

    public BigDecimal getValueRide() {
        return valueRide;
    }

    public void setValueRide(BigDecimal valueRide) {
        this.valueRide = valueRide;
    }

    public enum FreightType {
        EXPRESS, SCHEDULED;
    }
}
