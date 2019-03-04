package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dionicio on 25/11/18.
 * Representa la agenda de el usuario Actual
 */
public class Diary implements Parcelable, ColumnsSqlite.ColumnsDiary{
    public final long NEW_DIARY_ENTRY = -1;
    public final static int ONE_HOUR = 60;
    private long id;
    private Date dateEvent;
    private String comment;
    private Client clientToVisit;
    private Date lastModification;
    private int duration;
    private int status;
    private String remoteId;
    private Date startTime;
    private Date endTime;

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
        dateEvent = (Date) in.readSerializable();
        lastModification = (Date) in.readSerializable();
        duration = in.readInt();
        status = in.readInt();
        remoteId = in.readString();
        startTime = (Date) in.readSerializable();
        endTime = (Date)in.readSerializable();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
    @Override
    public String toString() {
        return "Diary{" + "id=" + id + ", dateEvent=" + dateEvent + ", comment='" + comment + '\''
                + ", clientToVisit=" + clientToVisit + ", duration=" + duration + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(comment);
        parcel.writeSerializable(dateEvent);
        parcel.writeSerializable(lastModification);
        parcel.writeInt(duration);
        parcel.writeInt(status);
        parcel.writeString(remoteId);
        parcel.writeSerializable(startTime);
        parcel.writeSerializable(endTime);
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

    public static class SortByDateDesc implements Comparator<Diary>{

        @Override
        public int compare(Diary diary, Diary t1) {
            return t1.getDateEvent().compareTo(diary.getDateEvent());
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
