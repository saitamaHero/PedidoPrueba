package com.mobile.proisa.pedidoprueba.Clases;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import Models.Item;

public class ItemSelectable extends Item implements Parcelable {
    private boolean selected;

    public ItemSelectable(boolean selected) {
        this.selected = selected;
    }

    public ItemSelectable(String id, String name, boolean selected) {
        super(id, name);
        this.selected = selected;
    }

    public ItemSelectable(Item item, boolean selected){
        super(item.getId(), item.getName());
        this.setCategory(item.getCategory());
        this.setPhoto(item.getPhoto());
        this.setLastModification(item.getLastModification());
        this.setPrice(item.getPrice());
        this.setStock(item.getStock());
        this.setQuantity(item.getQuantity());
        this.setUnit(item.getUnit());
        this.setCost(item.getCost());
        this.setTaxRate(item.getTaxRate());
        this.selected = selected;
    }

    public ItemSelectable(Parcel in) {
        super(in);
        this.selected = in.readByte() == 1;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemSelectable> CREATOR = new Creator<ItemSelectable>() {
        @Override
        public ItemSelectable createFromParcel(Parcel in) {
            return new ItemSelectable(in);
        }

        @Override
        public ItemSelectable[] newArray(int size) {
            return new ItemSelectable[size];
        }
    };

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public static List<ItemSelectable> getItemSelectableList(List<Item> items){
        List<ItemSelectable> selectables = new ArrayList<>();

        for (Item i : items) {
            selectables.add(new ItemSelectable(i, false));
        }

        return selectables;
    }

    public static List<ItemSelectable> checkItemsInTheList(List<ItemSelectable> selectables, List<Item> items){
        for (ItemSelectable i : selectables) {
            i.setSelected(items.contains(i));
        }

        return selectables;
    }
}
