package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Vendor extends Person implements Parcelable{
    private List<Diary> diaryList;

    public Vendor() {
        super();
    }

    protected Vendor(Parcel in) {
        super(in);
        diaryList = in.createTypedArrayList(Diary.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(diaryList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel in) {
            return new Vendor(in);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };

    public List<Diary> getDiaryList() {
        return diaryList;
    }

    public void setDiaryList(List<Diary> diaryList) {
        this.diaryList = diaryList;
    }

    @Override
    public String toString() {
        return "Vendor{" + "codigo=" + getId() +",nombre=" + getName() + '}';
    }
}
