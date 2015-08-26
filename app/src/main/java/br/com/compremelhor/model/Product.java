package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adriano on 25/08/15.
 */
@DatabaseTable(tableName = "product")
public class Product extends DomainEntity {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @DatabaseField
    private Unit unit;
    @DatabaseField(foreign = true)
    private Manufacturer manufacturer;
    @DatabaseField
    private Code code;
    @DatabaseField(foreign = true)
    private Category category;

    public Product() {}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
