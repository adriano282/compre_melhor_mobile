package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Address extends EntityModel {
    private String street;
    private String number;
    private String quarter;
    private String city;
    private String state;
    private String zipcode;
    private String addressName;
    private Long userId;

    public Address() {
    }

    public Address(Long id, String street, String number,
                   String quarter, String city, String state,
                   String zipcode) {
        super(id);
        this.street = street;
        this.number = number;
        this.quarter = quarter;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public Address(Long id, String street, String number,
                   String quarter, String city, String state,
                   String zipcode, Long userId) {
        super(id);
        this.street = street;
        this.number = number;
        this.quarter = quarter;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.userId = userId;
    }

    public Address(Long id, String street, String number,
                   String quarter, String city, String state,
                   String zipcode, String addressName, Long userId) {
        super(id);
        this.street = street;
        this.number = number;
        this.quarter = quarter;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.userId = userId;
        this.addressName = addressName;
    }

    @Override
    public String toString() {
        return this.getZipcode() + " - " + this.getStreet();
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
    public String getAddressName() {
        return addressName;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId()  {
        return this.userId;
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
}
