package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que representa el numero de comprobante fiscal
 * para más detalle vea {@link SimpleElement}
 */
public class NCF extends SimpleElement implements Parcelable, ColumnsSqlite.ColumnsNcf{
    public static final NCF UNKNOWN_NCF = new NCF("","");
    private String type;

    public NCF() {
    }

    public NCF(String id, String name) {
        super(id, name);
    }


    protected NCF(Parcel in) {
        super(in);
        type = in.readString();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NCF> CREATOR = new Creator<NCF>() {
        @Override
        public NCF createFromParcel(Parcel in) {
            return new NCF(in);
        }

        @Override
        public NCF[] newArray(int size) {
            return new NCF[size];
        }
    };

    @Override
    public String toString() {
        return String.format("NCF{id='%s', name='%s', type='%s' }", getId(), getName(), getType());
    }
}
