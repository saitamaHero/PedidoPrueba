package Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import Utils.DateUtils;

import static Models.ColumnsSqlite.*;

public class Client extends Person implements Parcelable, ColumnsClient, Updatable<Client> {
    public static final char CREDIT_OPENED = 'A';
    public static final char CREDIT_CLOSED = 'C';
    private List<Invoice> invoices;
    private double creditLimit;
    private double distance;
    private Diary visitDate;
    private int status;
    private String remoteId;
    private char creditStatus;


    public Client() {
        super();
        distance = 0.0;
    }

    protected Client(Parcel in) {
        super(in);
        creditLimit = in.readDouble();
        visitDate = in.readParcelable(Diary.class.getClassLoader());
        distance = in.readDouble();
        invoices = in.readArrayList(Invoice.class.getClassLoader());
        status = in.readInt();
        remoteId = in.readString();
        creditStatus = (char) in.readInt();
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

    public boolean hasInvoices(){
        return this.invoices != null || !(this.invoices.isEmpty());
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;

        for (Invoice i : this.invoices) {
            i.setClient(this);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(creditLimit);
        dest.writeParcelable(visitDate, flags);
        dest.writeDouble(distance);
        dest.writeList(invoices);
        dest.writeInt(status);
        dest.writeString(remoteId);
        dest.writeInt(creditStatus);
    }

    public char getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(char creditStatus) {
        this.creditStatus = creditStatus;
    }

    public boolean isCreditClosed(){
        return this.creditStatus == CREDIT_CLOSED;
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
        setBirthDate(item.getBirthDate());
        setIdentityCard(item.getIdentityCard());
        setCreditLimit(item.getCreditLimit());

        return true;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public boolean isPending() {
        return getStatus() == STATUS_PENDING;
    }

    @Override
    public void setRemoteId(Object remote) {
        if(remote != null){
            this.remoteId = remote.toString();
        }
    }

    @Override
    public Object getRemoteId() {
        return this.remoteId;
    }
}
