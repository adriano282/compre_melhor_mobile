package br.com.compremelhor.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by adriano on 24/08/15.
 */
public class Purchase {
    private Calendar datePurchase;
    private BigDecimal valueTotal;
    private Status status;
    private List<Item> itens;
    private Freight freight;

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

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    public Freight getFreight() {
        return freight;
    }

    public void setFreight(Freight freight) {
        this.freight = freight;
    }

    enum Status {
        FINISHED, PENDING
    }

}
