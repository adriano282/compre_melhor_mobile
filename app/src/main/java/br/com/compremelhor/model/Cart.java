package br.com.compremelhor.model;

import java.util.List;

/**
 * Created by adriano on 25/08/15.
 */
public class Cart {
    private List<Item> itens;


    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }
}
