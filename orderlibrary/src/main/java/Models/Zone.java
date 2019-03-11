package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que representa la zona de un {@link Client}
 * para más detalle vea {@link SimpleElement}
 */
public class Zone extends SimpleElement implements Parcelable, ColumnsSqlite.ColumnsZone {
    public static final Zone UNKNOWN_ZONE = new Zone("","");

    public Zone() {
    }

    public Zone(String id, String name) {
        super(id, name);
    }


    protected Zone(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Zone> CREATOR = new Creator<Zone>() {
        @Override
        public Zone createFromParcel(Parcel in) {
            return new Zone(in);
        }

        @Override
        public Zone[] newArray(int size) {
            return new Zone[size];
        }
    };

    @Override
    public String toString() {
        return String.format("Zone{id='%s' name='%s' }",getId(), getName());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
