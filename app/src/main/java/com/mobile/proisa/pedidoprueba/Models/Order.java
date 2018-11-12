package com.mobile.proisa.pedidoprueba.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements ITotal {
    public final static String NEW_ORDER = "NEW_ORDER";
    private String id;
    private Date date;
    private List<Item> itemList;


    public Order() {
        itemList = new ArrayList<>();
    }

    public Order(String id) {
        this();
        this.id = id;
    }

    public Order(String id, List<Item> itemList) {
        this.id = id;
        this.itemList = itemList;
    }

    public boolean addItem(Item item){
        return this.itemList.add(item);
    }

    public boolean addAll(List<Item> items){
        return this.itemList.addAll(items);
    }

    @Override
    public double getTotal() {
        return 0;
    }
}



