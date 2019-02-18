package com.mobile.proisa.pedidoprueba.Clases;

import android.os.Parcel;
import android.os.Parcelable;

public class Actividad implements Parcelable{
    private int id;
    private String numeric;
    private String description;
    private String info;
    private boolean isGood;

    public Actividad() {
    }

    public Actividad(int id, String numeric, String descrition) {
        this.id = id;
        this.numeric = numeric;
        this.description = descrition;
    }

    public Actividad(int id, String numeric, String description, String info, boolean isGood) {
        this.id = id;
        this.numeric = numeric;
        this.description = description;
        this.info = info;
        this.isGood = isGood;
    }

    public Actividad(String numeric, String descrition, String info) {
        this.numeric = numeric;
        this.description = descrition;
        this.info = info;
        this.isGood = true;
    }

    public Actividad(String numeric, String descrition, String info, boolean isGood) {
        this.numeric = numeric;
        this.description = descrition;
        this.info = info;
        this.isGood = isGood;
    }

    protected Actividad(Parcel in) {
        id = in.readInt();
        numeric = in.readString();
        description = in.readString();
        info = in.readString();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public static final Creator<Actividad> CREATOR = new Creator<Actividad>() {
        @Override
        public Actividad createFromParcel(Parcel in) {
            return new Actividad(in);
        }

        @Override
        public Actividad[] newArray(int size) {
            return new Actividad[size];
        }
    };

    public String getNumeric() {
        return numeric;
    }

    public void setNumeric(String numeric) {
        this.numeric = numeric;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(numeric);
        parcel.writeString(description);
        parcel.writeString(info);
    }

    public static class Builder{
        private Actividad mActividad;

        public Builder() {
            mActividad = new Actividad();
        }

        public Builder addId(int id){
            mActividad.setId(id);
            return this;
        }

        public Builder addDescription(String description){
            mActividad.setDescription(description);
            return this;
        }

        public Builder addInfo(String info){
            mActividad.setInfo(info);
            return this;
        }

        public Builder addNumeric(String numeric){
            mActividad.setNumeric(numeric);
            return this;
        }

        public Builder addStatus(boolean isGood){
            mActividad.setGood(isGood);
            return this;
        }


        public Actividad create(){
            return this.mActividad;
        }
    }
}
