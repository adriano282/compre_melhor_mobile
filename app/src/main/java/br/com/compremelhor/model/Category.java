package br.com.compremelhor.model;

public class Category extends EntityModel {
    String name;

    public Category() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
