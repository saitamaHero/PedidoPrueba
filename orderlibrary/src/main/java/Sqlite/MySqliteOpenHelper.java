package Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import Models.Category;
import Models.Client;
import Models.Diary;
import Models.Invoice;
import Models.Item;
import Models.Unit;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final String PREFIX_TRIGGER_UPDATE_LM = "update_lastmod_";
    private static final String PREFIX_TRIGGER_INSERT_LM = "insert_lastmod_";
    public static final String DBNAME = "contapro_ruteros.db";
    public static final int VERSION = 7;

    private static final String CREATE_TABLE_ARTICULOS
            = "CREATE TABLE "+ Item.TABLE_NAME
            + "("+ Item._ID  + " TEXT NOT NULL,"
            + Item._NAME     + " TEXT NOT NULL,"
            + Item._PRICE    + " NUMERIC DEFAULT 0,"
            + Item._TAX_RATE + " NUMERIC DEFAULT 0,"
            + Item._CAT      + " TEXT,"
            + Item._UNIT     + " TEXT,"
            + Item._STOCK    + " NUMERIC DEFAULT 0,"
            + Item._PHOTO    + " TEXT,"
            + Item._LASTMOD  + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Item._ID + ")"
            + ");";

    private static final String CREATE_TABLE_DEPARTAMENTOS
            = "CREATE TABLE "   + Category.TABLE_NAME
            + "("+ Category._ID + " TEXT NOT NULL,"
            + Category._NAME    + " TEXT NOT NULL,"
            + Category._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Category._ID + ")"
            + ");";

    private static final String CREATE_TABLE_UNIDADES
            = "CREATE TABLE "+ Unit.TABLE_NAME
            + "("+ Unit._ID + " TEXT NOT NULL,"
            + Unit._NAME    + " TEXT NOT NULL,"
            + Unit._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Unit._ID + ")"
            + ");";

    //Mantener una ID_REMOTO para mantener sincronizacion,
    //Mantener un estado(FLAG_PENDING) para saber que esta pediente de insercion
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
            + Client._PHONE     + " TEXT DEFAULT '',"
            + Client._STATUS    + " INTEGER NOT NULL,"
            + Client._CR_STATUS + " TEXT NOT NULL DEFAULT '"+Client.CREDIT_OPENED+"',"
            + Client._ID_REMOTE + " TEXT,"
            + Client._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Client._ID + ")"
            + ");";

    private static final String CREATE_TABLE_FACTURAS
            = "CREATE TABLE "    + Invoice.TABLE_NAME
            + "("+ Invoice._ID   + " TEXT NOT NULL,"
            + Invoice._CLIENT    + " TEXT NOT NULL,"
            + Invoice._COMMENT   + " TEXT NOT NULL,"
            + Invoice._INV_TYPE  + " INTEGER NOT NULL,"
            + Invoice._DISCOUNT  + " NUMERIC DEFAULT 0,"
            + Invoice._DATE      + " TEXT DEFAULT '',"
            + Invoice._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + Client._STATUS     + " INTEGER NOT NULL,"
            + Client._ID_REMOTE  + " TEXT,"
            + "PRIMARY KEY(" + Invoice._ID + ")"
            + ");";

    private static final String CREATE_TABLE_FACTURAS_DETALLE
            = "CREATE TABLE "    + Invoice.TABLE_NAME_DETAILS
            + "("+ Invoice._ID   + " TEXT NOT NULL,"
            + Invoice.ITEM_ID    + " TEXT NOT NULL,"
            + Invoice._QTY       + " NUMERIC DEFAULT 1,"
            + Invoice._PRICE     + " NUMERIC DEFAULT 1,"
            + Invoice._TAX_RATE  + " NUMERIC DEFAULT 0,"
            + "FOREIGN KEY("+Invoice._ID+") REFERENCES "+Invoice.TABLE_NAME+"("+Invoice._ID+")"
            + ");";

    private static final String CREATE_TABLE_VISITAS
            = "CREATE TABLE "     + Diary.TABLE_NAME
            + "("+ Diary._ID      + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + Diary._EVENT        + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + Diary._COMMENT      + " TEXT NOT NULL DEFAULT '',"
            + Diary._DURATION     + " INTEGER DEFAULT 0,"
            + Diary._CLIENT_ID    + " TEXT NOT NULL,"
            + Diary._START_TIME   + " TEXT,"
            + Diary._END_TIME     + " TEXT,"
            + Client._STATUS      + " INTEGER NOT NULL,"
            + Client._ID_REMOTE   + " TEXT,"
            + Diary._LASTMOD      + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY("+Diary._CLIENT_ID+") REFERENCES "+Client.TABLE_NAME+"("+Client._ID+")"
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

        //////////////////
        sqLiteDatabase.execSQL(CREATE_TABLE_FACTURAS);
        sqLiteDatabase.execSQL(CREATE_TABLE_FACTURAS_DETALLE);
        sqLiteDatabase.execSQL(createTriggerUpdate(Invoice.TABLE_NAME, Invoice._LASTMOD, Invoice._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Invoice.TABLE_NAME, Invoice._LASTMOD, Invoice._ID));


        sqLiteDatabase.execSQL(CREATE_TABLE_VISITAS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Diary.TABLE_NAME, Diary._LASTMOD, Diary._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Diary.TABLE_NAME, Diary._LASTMOD, Diary._ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Item.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Item.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Item.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   +Category.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Category.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Category.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Unit.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Unit.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Unit.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Client.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Client.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Client.TABLE_NAME));

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Invoice.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Invoice.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Invoice.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Invoice.TABLE_NAME_DETAILS);

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Diary.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Diary.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Diary.TABLE_NAME));

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

    public static MySqliteOpenHelper getInstance(Context context){
        return new MySqliteOpenHelper(context, MySqliteOpenHelper.DBNAME, null, MySqliteOpenHelper.VERSION);
    }

}
