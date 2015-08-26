package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "manufacturer")
public class Manufacturer extends DomainEntity {
    @DatabaseField
    String companyName;

    public Manufacturer() {}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
