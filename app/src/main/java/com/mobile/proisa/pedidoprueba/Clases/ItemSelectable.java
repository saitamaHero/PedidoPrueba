package com.mobile.proisa.pedidoprueba.Clases;

import android.os.Parcel;
import android.os.Parcelable;

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
}
