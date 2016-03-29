package br.com.compremelhor.model;

import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "product")
public class Product extends EntityModel {
    private String name;
    private String description;
    private Unit unit;
    private Manufacturer manufacturer;
    private Code code;
    private Category category;
    private BigDecimal priceUnitary;


    public Product() {}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceUnitary(BigDecimal price) {
        this.priceUnitary = price;
    }

    public BigDecimal getPriceUnitary() {
        return priceUnitary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public enum Unit {
        BOX("CX"),
        UNIT("UN"),
        METRO("MT"),
        LITRO("LT"),
        KILO("KG");

        private String unit;

        Unit(String unit) {
            this.unit = unit;
        }
    }
}
