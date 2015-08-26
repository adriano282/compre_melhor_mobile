package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "address")
public class Address extends DomainEntity {
    @DatabaseField(id = true, generatedId = true)
    private Long id;
    @DatabaseField
    private String street;
    @DatabaseField
    private String number;
    @DatabaseField
    private String quarter;
    @DatabaseField
    private String city;
    @DatabaseField
    private String state;
    @DatabaseField
    private String zipcode;

    public Address() {
        // ORMLite needs a no-arg constructor
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
