package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class Freight extends DomainEntity {
    private Address address;
    private BigDecimal valueTotalDrive;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BigDecimal getValueTotalDrive() {
        return valueTotalDrive;
    }

    public void setValueTotalDrive(BigDecimal valueTotalDrive) {
        this.valueTotalDrive = valueTotalDrive;
    }
}
