package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
public class Freight extends DomainEntity {
    private Address address;
    private BigDecimal totalValueDrive;

    public Freight() {}

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BigDecimal getTotalValueDrive() {
        return totalValueDrive;
    }

    public void setTotalValueDrive(BigDecimal totalValueDrive) {
        this.totalValueDrive = totalValueDrive;
    }
}
