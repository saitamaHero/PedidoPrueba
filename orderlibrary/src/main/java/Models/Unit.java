package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que representa la unidad de un {@link Item}
 * para más detalle vea {@link SimpleElement}
 */
public class Unit extends SimpleElement implements Parcelable, ColumnsSqlite.ColumnsUnit{
    public static final Unit UNKNOWN_UNIT = new Unit("","Desconocida");


    public Unit() {
    }

    public Unit(String id, String name) {
        super(id, name);
    }

    protected Unit(Parcel in) {
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

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    @Override
    public String toString() {
        return String.format("Unit{id='%s' name='%s' }", getId(), getName());
    }
}
