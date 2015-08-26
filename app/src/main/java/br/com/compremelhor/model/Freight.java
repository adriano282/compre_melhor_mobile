package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "freight")
public class Freight extends DomainEntity {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(foreign = true)
    private Purchase purchase;
    @DatabaseField(foreign = true)
    private Address address;
    @DatabaseField
    private BigDecimal valueTotalDrive;

    public Freight() {}

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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}
