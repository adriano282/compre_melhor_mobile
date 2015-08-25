package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Product extends DomainEntity {
    private String name;
    private String description;
    private Unit unit;
    private Manufacturer manufacturer;
    private Code code;
    private Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    enum Unit {
        CAIXA("CX"),
        UNIDADE("UN"),
        METRO("MT"),
        LITRO("LT");

        private String unit;

        Unit(String unit) {
            this.unit = unit;
        }
    }
}
