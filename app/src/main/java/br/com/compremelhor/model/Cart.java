package br.com.compremelhor.model;

import java.util.List;

/**
 * Created by adriano on 25/08/15.
 */
public class Cart extends DomainEntity {
    private List<PurchaseLine> itens;


    public List<PurchaseLine> getItens() {
        return itens;
    }

    public void setItens(List<PurchaseLine> itens) {
        this.itens = itens;
    }
}
