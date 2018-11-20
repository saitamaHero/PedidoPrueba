package com.example.orderlibrary.Models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Person {
    private String id;
    private String name;
    private List<String> phoneNumbers;
    //private Lat
    private String identityCard;
    private String address;
    private Date birthDate;
    private Date enteredDate;
    private String email;
    private Uri profilePhoto;

    public Person() {
        phoneNumbers = new ArrayList<>();
        birthDate = Calendar.getInstance().getTime();
        enteredDate = Calendar.getInstance().getTime();
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

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(Date enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private boolean addPhone(String phone){
        if(!this.phoneNumbers.contains(phone)){
            return this.phoneNumbers.add(phone);
        }
        return false;
    }

    public Uri getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Uri profilePhoto) {
        this.profilePhoto = profilePhoto;
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
        Person person = (Person) obj;

        if(person == null) return false;

        return person.getId().equals(this.id) && person.getName().equals(this.name)
                && person.getIdentityCard().equals(this.identityCard);
    }


    @Override
    public String toString() {
        return "Person{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", identityCard='"
                + identityCard + '\'' + ", birthDate=" + birthDate + ", email='" + email + '\'' + '}';
    }
}


/*public interface IAppend<T>{
    public boolean addItem(T item);
}*/