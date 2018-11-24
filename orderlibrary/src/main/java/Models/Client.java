package Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Utils.DateUtils;

public class Client extends Person implements Parcelable {
    private double creditLimit;
    private double distance;
    private Date visitDate;

    public Client() {
        super();
        visitDate = Calendar.getInstance().getTime();
        distance = 0.0;


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(visitDate);

        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,24);


        visitDate = calendar.getTime();
    }

    protected Client(Parcel in) {
        super(in);
        creditLimit = in.readDouble();
        visitDate = (Date) in.readSerializable();
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

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }


    public boolean isDayOfTheVisit(){
        Date date =  Calendar.getInstance().getTime();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,24);


        date = calendar.getTime();

        Log.d("tiempoActual", DateUtils.formatDate(date, DateUtils.DD_MM_YYYY_hh_mm_ss) + "visitDate: "+DateUtils.formatDate(visitDate, DateUtils.DD_MM_YYYY_hh_mm_ss));

        return this.visitDate.compareTo(date) == 0;
    }




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(creditLimit);
        dest.writeSerializable(visitDate);
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
}
