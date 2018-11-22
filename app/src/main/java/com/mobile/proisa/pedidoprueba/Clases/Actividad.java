package com.mobile.proisa.pedidoprueba.Clases;

import android.os.Parcel;
import android.os.Parcelable;

public class Actividad implements Parcelable{
    private String numeric;
    private String descrition;
    private String info;

    public Actividad(String numeric, String descrition, String info) {
        this.numeric = numeric;
        this.descrition = descrition;
        this.info = info;
    }

    protected Actividad(Parcel in) {
        numeric = in.readString();
        descrition = in.readString();
        info = in.readString();
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

    public String getDescrition() {
        return descrition;
    }

    public void setDescrition(String descrition) {
        this.descrition = descrition;
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
        parcel.writeString(numeric);
        parcel.writeString(descrition);
        parcel.writeString(info);
    }
}
