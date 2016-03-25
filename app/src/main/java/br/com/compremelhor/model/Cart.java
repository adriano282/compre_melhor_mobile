package br.com.compremelhor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adriano on 25/08/15.
 */
public class Cart extends EntityModel {
    private List<PurchaseLine> items;

    public List<PurchaseLine> getItems() {
        return items == null ?
                new ArrayList<PurchaseLine>() :
                items;
    }

    public void setItems(List<PurchaseLine> items) {
        this.items = items;
    }

    public void addItem(PurchaseLine item) {
        List<PurchaseLine> items = getItems();
        items.add(item);
        setItems(items);
    }

    public void removeItem(PurchaseLine item) {
        int index = getItems().indexOf(item);
        List<PurchaseLine> items = getItems();
        items.remove(index);
        setItems(items);
    }

    public void updateItem(PurchaseLine item) {
        int index = getItems().indexOf(item);
        List<PurchaseLine> items = getItems();
        items.set(index, item);
        setItems(items);
    }
}
