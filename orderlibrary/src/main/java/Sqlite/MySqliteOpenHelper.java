package Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import Models.Item;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "contapro_ruteros.db";
    public static final int VERSION = 1;

    private static final String CREATE_TABLE_ARTICULOS
            = "CREATE TABLE "+ Item.TABLE_NAME
            + "("+ Item._ID + " TEXT NOT NULL,"
            + Item._NAME    + " TEXT NOT NULL,"
            + Item._PRICE   + " NUMERIC DEFAULT 0,"
            + Item._CAT     + " TEXT,"
            + Item._UNIT    + " TEXT,"
            + Item._STOCK   + " NUMERIC DEFAULT 0,"
            + Item._PHOTO   + " TEXT,"
            + Item._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Item._ID + ")"
            + ");";


    public MySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ARTICULOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            sqLiteDatabase.rawQuery("DROP TABLE IF EXISTS "+Item.TABLE_NAME, null);
        }
    }


}
