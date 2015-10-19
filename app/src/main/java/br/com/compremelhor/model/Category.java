package br.com.compremelhor.model;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "category")
public class Category extends DomainEntity {
    String name;

    public Category() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
