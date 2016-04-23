package br.com.compremelhor.model;

import java.math.BigDecimal;
import java.util.TreeSet;

/**
 * Created by adriano on 24/08/15.
 */
public class Purchase extends EntityModel {
    private BigDecimal totalValue;
    private Status status;
    private TreeSet<PurchaseLine> items;
    private Freight freight;
    private Establishment establishment;
    private User user;

    public Purchase() {}

    public BigDecimal getTotalValue() {
        if (totalValue == null)
            totalValue = new BigDecimal(0.0);

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

    public TreeSet<PurchaseLine> getItems() {
        return items;
    }

    public void setItems(TreeSet<PurchaseLine> items) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum Status {
        OPENED, READY, SHIPPED, FINISHED;

        public String getTranslatedValued() {
            switch (this) {
                case OPENED:
                    return "ABERTO";

                case READY:
                    return "PARA SER ENTREGUE";

                case SHIPPED:
                    return "ENTREGUE";

                case FINISHED:
                    return "CONCLUÍDO";
            }
            return null;
        }
    }

}
