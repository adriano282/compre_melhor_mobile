package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by adriano on 24/08/15.
 */
@DatabaseTable(tableName = "purchase")
public class Purchase extends DomainEntity {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private Calendar datePurchase;
    @DatabaseField
    private BigDecimal valueTotal;
    @DatabaseField
    private Status status;
    @ForeignCollectionField
    private Collection<Item> items;
    @DatabaseField(foreign = true)
    private Freight freight;
    @DatabaseField(foreign = true)
    private Establishment establishment;

    public Purchase() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Calendar getDatePurchase() {
        return datePurchase;
    }

    public void setDatePurchase(Calendar datePurchase) {
        this.datePurchase = datePurchase;
    }

    public BigDecimal getValueTotal() {
        return valueTotal;
    }

    public void setValueTotal(BigDecimal valueTotal) {
        this.valueTotal = valueTotal;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Collection<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Freight getFreight() {
        return freight;
    }

    public void setFreight(Freight freight) {
        this.freight = freight;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    enum Status {
        FINISHED, PENDING
    }

}
