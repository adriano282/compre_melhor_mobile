package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Manufacturer extends DomainEntity {
    String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
