package Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Models.Category;
import Models.Client;
import Models.Item;
import Models.Unit;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final String PREFIX_TRIGGER_UPDATE_LM = "update_lastmod_";
    private static final String PREFIX_TRIGGER_INSERT_LM = "insert_lastmod_";
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

    private static final String CREATE_TABLE_DEPARTAMENTOS
            = "CREATE TABLE "+ Category.TABLE_NAME
            + "("+ Category._ID + " TEXT NOT NULL,"
            + Category._NAME    + " TEXT NOT NULL,"
            + Category._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Category._ID + ")"
            + ");";

    private static final String CREATE_TABLE_UNIDADES
            = "CREATE TABLE "+ Category.TABLE_NAME
            + "("+ Unit._ID + " TEXT NOT NULL,"
            + Unit._NAME    + " TEXT NOT NULL,"
            + Unit._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Unit._ID + ")"
            + ");";

    private static final String CREATE_TABLE_CLIENTES
            = "CREATE TABLE "+ Client.TABLE_NAME
            + "("+ Client._ID   + " TEXT NOT NULL,"
            + Client._NAME      + " TEXT NOT NULL,"
            + Client._IDCARD    + " TEXT NOT NULL,"
            + Client._BIRTH     + " TEXT NOT NULL,"
            + Client._ENTERED   + " TEXT NOT NULL,"
            + Client._ADDRESS   + " TEXT DEFAULT '',"
            + Client._PHOTO     + " TEXT DEFAULT '',"
            + Client._EMAIL     + " TEXT DEFAULT '',"
            + Client._LAT       + " REAL DEFAULT 0,"
            + Client._LNG       + " REAL DEFAULT 0,"
            + Client._CR_LIMIT  + " NUMERIC DEFAULT 0,"
            + Client._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Client._ID + ")"
            + ");";


    public MySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_ARTICULOS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Item.TABLE_NAME, Item._LASTMOD, Item._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Item.TABLE_NAME, Item._LASTMOD, Item._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_DEPARTAMENTOS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Category.TABLE_NAME, Category._LASTMOD, Category._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Category.TABLE_NAME, Category._LASTMOD, Category._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_UNIDADES);
        sqLiteDatabase.execSQL(createTriggerUpdate(Unit.TABLE_NAME, Unit._LASTMOD, Unit._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Unit.TABLE_NAME, Unit._LASTMOD, Unit._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_CLIENTES);
        sqLiteDatabase.execSQL(createTriggerUpdate(Client.TABLE_NAME, Client._LASTMOD, Client._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Client.TABLE_NAME, Client._LASTMOD, Client._ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Item.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS "+ PREFIX_TRIGGER_UPDATE_LM.concat(Item.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Category.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS "+ PREFIX_TRIGGER_UPDATE_LM.concat(Category.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Unit.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS "+ PREFIX_TRIGGER_UPDATE_LM.concat(Unit.TABLE_NAME));
            onCreate(sqLiteDatabase);
        }
    }

    private String createTriggerUpdate(String table, String fieldUpdate, String pk){
        String trigger = "CREATE TRIGGER {name} AFTER UPDATE ON {table} BEGIN update {table} SET {fu} = datetime('now', 'localtime') where {pk}=NEW.{pk}; END";
        trigger = trigger.replace("{name}", PREFIX_TRIGGER_UPDATE_LM.concat(table));
        trigger = trigger.replace("{table}", table);
        trigger = trigger.replace("{fu}", fieldUpdate);
        trigger = trigger.replace("{pk}", pk);

        return trigger;
    }

    private String createTriggerInsert(String table, String fieldUpdate, String pk){
        String trigger = "CREATE TRIGGER {name} AFTER INSERT ON {table} BEGIN update {table} SET {fu} = datetime('now', 'localtime') where {pk}=NEW.{pk}; END";
        trigger = trigger.replace("{name}", PREFIX_TRIGGER_INSERT_LM.concat(table));
        trigger = trigger.replace("{table}", table);
        trigger = trigger.replace("{fu}", fieldUpdate);
        trigger = trigger.replace("{pk}", pk);

        return trigger;
    }

}
