package com.mobile.proisa.pedidoprueba.Models;

public class Item implements ITotal {
    private String id;
    private String name;
    private double stock;
    private double quantity;
    private double price;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public double getTotal() {
        return this.price * this.quantity;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + this.id.hashCode();
        result = result * 31 + this.name.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Item item = ((Item)obj);

        if(item == null) return false;

        return this.id.compareTo(item.getId()) == 0 && this.name.compareTo(item.getName()) == 0;
    }

    @Override
    public String toString() {
        return "Item{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", stock=" + stock + ", quantity=" + quantity + ", price=" + price + '}';
    }
}
