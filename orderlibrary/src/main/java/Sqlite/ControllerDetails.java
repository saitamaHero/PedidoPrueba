package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public abstract class ControllerDetails<T> {
    private SQLiteDatabase sqLiteDatabase;

    public ControllerDetails(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public abstract List<T> getAllById(Object id);

    /**
     * Obtains data from {@link Cursor}  for type T
     * @param cursor
     * @return a object filled from cursor
     */
    public T getDataFromCursor(Cursor cursor){
        return null;
    }

    /**
     * Override this method for create a contentValues
     * @param item for create the {@link ContentValues}
     * @return
     */
    public ContentValues getContentValues(T item, Object id){
        return new ContentValues();
    }

    public abstract boolean insertAllWithId(List<T> item, Object id);
    public abstract boolean deleteAllWithId(Object id);

    //public abstract boolean deleteAll(List<T> item);
}
