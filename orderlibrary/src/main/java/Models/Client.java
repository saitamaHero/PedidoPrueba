package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Client extends Person implements Parcelable {
    private double creditLimit;

    public Client() {
        super();
    }

    protected Client(Parcel in) {
        super(in);
        creditLimit = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(creditLimit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
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

    @Override
    public String toString() {
        return "Client{" +
                "creditLimit=" + creditLimit +
                "name=" + getName() +
                "id="   + getId() +
                "identityCard=" + getIdentityCard() +
                '}';
    }
}
