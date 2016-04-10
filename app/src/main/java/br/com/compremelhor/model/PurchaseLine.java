package br.com.compremelhor.model;

import java.math.BigDecimal;

public class PurchaseLine extends EntityModel implements Comparable<String> {

    private BigDecimal quantity;
    private BigDecimal subTotal;
    private BigDecimal unitaryPrice;
    private Product product;
    private Purchase purchase;
    private String productName;
    private String category;
    private Stock stock;

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitaryPrice() {
        return unitaryPrice;
    }

    public void setUnitaryPrice(BigDecimal unitaryPrice) {
        this.unitaryPrice = unitaryPrice;
    }

    public PurchaseLine() {}

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return this.product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public int hashCode() {
        if (product != null && product.getId() != 0)
            return product.getId();

        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  PurchaseLine) {
            PurchaseLine other = (PurchaseLine) o;
            if (other.getPurchase() != null &&
                    other.getPurchase().getId() != 0 &&
                    other.getProduct() != null &&
                    other.getProduct().getId() != 0 &&
                    this.getPurchase() != null &&
                    this.getPurchase().getId() != 0 &&
                    this.getProduct() != null &&
                    this.getProduct().getId() != 0 &&
                    this.getProduct().getId() == other.getProduct().getId() &&
                    this.getPurchase().getId() == other.getPurchase().getId())
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(String another) {
        return this.category.compareTo(another);
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
