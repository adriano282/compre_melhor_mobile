package br.com.compremelhor.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Created by adriano on 24/08/15.
 */
public class Purchase extends EntityModel {
    private BigDecimal totalValue;
    private Status status;
    private Collection<PurchaseLine> items;
    private Freight freight;
    private Establishment establishment;

    public Purchase() {}

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Collection<PurchaseLine> getItems() {
        return items;
    }

    public void setItems(List<PurchaseLine> items) {
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
