package Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Utils.DateUtils;

import static Models.ColumnsSqlite.*;

public class Client extends Person implements Parcelable, ColumnsClient, Updatable<Client> {
    private double creditLimit;
    private double distance;
    private Diary visitDate;

    public Client() {
        super();
        distance = 0.0;


        /*Calendar calendar = new GregorianCalendar();
        calendar.setTime(visitDate);

        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,24);


        visitDate = calendar.getTime();*/
    }

    protected Client(Parcel in) {
        super(in);
        creditLimit = in.readDouble();
        visitDate = in.readParcelable(Diary.class.getClassLoader());
        distance = in.readDouble();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Diary getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Diary visitDate) {
        this.visitDate = visitDate;
    }


    public boolean isDayOfTheVisit(){
        if(this.visitDate == null) return false;

        Date date =  DateUtils.deleteTime(Calendar.getInstance().getTime());
        return this.visitDate.getDateEvent().compareTo(date) == 0;
    }

    public DateUtils.DateConverter getTimeToVisit(){
        if(visitDate == null) return null;

        return new DateUtils.DateConverter(this.visitDate.getDateEvent(), Calendar.getInstance().getTime());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(creditLimit);
        dest.writeParcelable(visitDate, flags);
        dest.writeDouble(distance);
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
                ", name=" + getName() +
                ", id="   + getId() +
                ", identityCard=" + getIdentityCard() +
                '}';
    }

    @Override
    public boolean update(Client item) {

        if(item == null) return  false;


        setName(item.getName());
        setEmail(item.getEmail());
        setPhoneNumbers(item.getPhoneNumbers());

        return true;
    }
}
