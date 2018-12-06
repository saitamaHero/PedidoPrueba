package Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que representa la categoría de un {@link Item}
 * para más detalle vea {@link SimpleElement}
 */
public class Category extends SimpleElement implements Parcelable, ColumnsSqlite.ColumnsCategory {
    public static final Category UNKNOWN_CATEGORY = new Category("","Desconocido");

    public Category() {
    }

    public Category(String id, String name) {
        super(id, name);
    }


    protected Category(Parcel in) {
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

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        return String.format("Category{id='%s' name='%s' }",getId(), getName());
    }



}
