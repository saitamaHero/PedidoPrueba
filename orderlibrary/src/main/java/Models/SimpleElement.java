package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class SimpleElement implements Parcelable{
    private String id;
    private String name;
    private Date lastModification;

    public SimpleElement() {
    }

    public SimpleElement(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected SimpleElement(Parcel in) {
        id = in.readString();
        name = in.readString();
        lastModification = (Date) in.readSerializable();
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }
    public static final Creator<SimpleElement> CREATOR = new Creator<SimpleElement>() {
        @Override
        public SimpleElement createFromParcel(Parcel in) {
            return new SimpleElement(in);
        }

        @Override
        public SimpleElement[] newArray(int size) {
            return new SimpleElement[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeSerializable(this.lastModification);
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
        SimpleElement element = (SimpleElement) obj;

        if(element == null) return false;

        return element.getId().equals(this.id) && element.getName().equals(this.name);
    }
}
