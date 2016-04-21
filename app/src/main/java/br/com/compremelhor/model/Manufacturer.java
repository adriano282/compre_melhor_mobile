package br.com.compremelhor.model;

public class Manufacturer extends EntityModel {

    String companyName;

    public Manufacturer() {}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
