package Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String user;
    private String password;
    private char level;
    private Vendor vendor;

    public User(String user, char level, Vendor vendor) {
        this.user = user;
        this.level = level;
        this.vendor = vendor;
    }

    protected User(Parcel in) {
        user = in.readString();
        password = in.readString();
        level = (char) in.readInt();
        vendor = in.readParcelable(Vendor.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getLevel() {
        return level;
    }

    public void setLevel(char level) {
        this.level = level;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Uri getProfilePhoto(){
        return this.vendor != null ? this.vendor.getProfilePhoto() : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user);
        parcel.writeString(password);
        parcel.writeInt((int) level);
        parcel.writeParcelable(vendor, i);
    }
}
