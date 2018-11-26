package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dionicio on 25/11/18.
 * Representa la agenda de el usuario Actual
 */

public class Diary implements Parcelable, ColumnsSqlite.ColumnsDiary{
    public final long NEW_DIARY_ENTRY = -1;
    private long id;
    private Date dateEvent;
    private String comment;
    private Client clientToVisit;
    private Date lastModification;

    public Diary() {
        this.id = NEW_DIARY_ENTRY;
    }

    public Diary(long id, Date date, String comment) {
        this.id = id;
        this.dateEvent = date;
        this.comment = comment;
    }

    protected Diary(Parcel in) {
        id = in.readLong();
        comment = in.readString();
        clientToVisit = in.readParcelable(Client.class.getClassLoader());
        dateEvent = (Date) in.readSerializable();
        lastModification = (Date) in.readSerializable();
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    public Date getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Client getClientToVisit() {
        return clientToVisit;
    }

    public void setClientToVisit(Client clientToVisit) {
        this.clientToVisit = clientToVisit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    //Probando estos métodos para el calculo de fechas
    public long getDays(Date currentDate){
        long startTime = this.dateEvent.getTime();
        long endTime = currentDate.getTime();

        long diffTime = endTime - startTime;

        return TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    public long getHours(Date currentDate){
        long startTime = this.dateEvent.getTime();
        long endTime = currentDate.getTime();

        long diffTime = endTime - startTime;

        return TimeUnit.HOURS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(comment);
        parcel.writeParcelable(clientToVisit, i);
        parcel.writeSerializable(dateEvent);
        parcel.writeSerializable(lastModification);
    }
}
