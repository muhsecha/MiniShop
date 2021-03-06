package com.pos.minishop.model;

import java.io.Serializable;

public class TransModel implements Serializable {

    private String cartImage;
    private String nameProduct;
    private String stock;
    private String price;
    private String productId;
    private int amount;
    private int priceInt;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getPriceInt() {
        return priceInt;
    }

    public void setPriceInt(int priceInt) {
        this.priceInt = priceInt;
    }

    public String getCartImage() {
        return cartImage;
    }

    public void setCartImage(String cartImage) {
        this.cartImage = cartImage;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
