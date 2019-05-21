package Models;

import android.os.Parcel;

public class Company extends SimpleElement implements ColumnsSqlite.ColumnsCompany{
    private String address;
    private String contactInfo;

    public Company() {
    }

    public Company(Parcel in) {
        super(in);
        address = in.readString();
        contactInfo = in.readString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(address);
        parcel.writeString(contactInfo);
    }
}
