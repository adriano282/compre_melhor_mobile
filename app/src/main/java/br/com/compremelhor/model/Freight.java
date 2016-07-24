package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class Freight extends EntityModel {
    private Address shipAddress;
    private BigDecimal rideValue;
    private Purchase purchase;
    private String type;
    private FreightSetup freightSetup;

    public Boolean getComplete() {
        return complete;
    }

    private int freightTypeId;
    private Boolean complete;
    private int version;

    public int getVersion() {return version;}
    public void setVersion(int version) { this.version = version;}

    public Boolean isComplete() {
        if (complete == null) return true;
        
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
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

    public BigDecimal getRideValue() {
        return rideValue;
    }

    public void setRideValue(BigDecimal rideValue) {
        this.rideValue = rideValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFreightTypeId() {
        return freightTypeId;
    }

    public void setFreightTypeId(int freightTypeId) {
        this.freightTypeId = freightTypeId;
    }

}
