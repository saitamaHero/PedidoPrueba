package com.example.orderlibrary.Models;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Order implements ITotal, Comparable<Order>{
    public final static String NEW_ORDER = "NEW_ORDER";
    private String id;
    private Date date;
    private List<Item> itemList;
    private Client client;
    private String comment;

    public Order() {
        this.id = NEW_ORDER;
        this.itemList = new ArrayList<>();
        this.date = Calendar.getInstance().getTime();
    }

    public Order(String id, Date date) {
        this();
        this.id = id;
        this.date = date;
    }

    public Order(String id, List<Item> itemList) {
        this.id = id;
        this.itemList = itemList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean addItem(Item item){
        return this.itemList.add(item);
    }

    public boolean addAllItems(List<Item> items){
        return this.itemList.addAll(items);
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public double getTotal() {
        double total = 0.0;

        for(Item i : itemList){ total += i.getTotal(); }

        return total;
    }

    @Override
    public int compareTo(@NonNull Order order) {
        return this.getDate().compareTo(order.getDate());
    }

    public static Order newInstance(Client client) {
        Order order = new Order();
        order.setClient(client);
        return order;
    }

    public static class SortByOrderDate implements Comparator<Order> {
        @Override
        public int compare(Order order, Order t1) {
            return order.getDate().compareTo(t1.getDate());
        }
    }

    public static class SortByOrderId implements Comparator<Order> {
        @Override
        public int compare(Order order, Order t1) {
            return order.getId().compareTo(t1.getId());
        }
    }
}



