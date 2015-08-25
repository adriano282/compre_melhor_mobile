package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Category extends DomainEntity {
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
