package Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Models.Category;
import Models.Client;
import Models.ColumnsSqlite;
import Models.Company;
import Models.Diary;
import Models.Invoice;
import Models.Item;
import Models.NCF;
import Models.Unit;
import Models.Zone;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static final String PREFIX_TRIGGER_UPDATE_LM = "update_lastmod_";
    private static final String PREFIX_TRIGGER_INSERT_LM = "insert_lastmod_";
    public static final String DBNAME = "contapro_ruteros.db";
    public static final int VERSION = 5;

    private static final String CREATE_TABLE_COMPANY
            = "CREATE TABLE "  + Company.TABLE_NAME
            + "("
            +  Company._COMPANY_NAME      + " TEXT NOT NULL,"
            +  Company._COMPANY_ADDRESS   + " TEXT NOT NULL DEFAULT '',"
            +  Company._COMPANY_INFO      + " TEXT NOT NULL DEFAULT ''"
            + ");\n";

    private static final String CREATE_TABLE_ARTICULOS
            = "CREATE TABLE "+ Item.TABLE_NAME
            + "("+ Item._ID  + " TEXT NOT NULL,"
            + Item._NAME     + " TEXT NOT NULL,"
            + Item._PRICE    + " NUMERIC DEFAULT 0,"
            + Item._TAX_RATE + " NUMERIC DEFAULT 0,"
            + Item._CAT      + " TEXT,"
            + Item._UNIT     + " TEXT,"
            + Item._STOCK    + " NUMERIC DEFAULT 0,"
            + Item._COST     + " NUMERIC DEFAULT 0,"
            + Item._PHOTO    + " TEXT,"
            + Item._LASTMOD  + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY(" + Item._ID + ")"
            + ");";


    private static final String TRIGGER_REDUCE_STOCK = "reduce_stock_products";

    private static final String CREATE_PROCEDURE_STOCK
            = "CREATE TRIGGER "   + TRIGGER_REDUCE_STOCK
            + " AFTER INSERT ON " + Invoice.TABLE_NAME_DETAILS
            + " FOR EACH ROW"
            + " BEGIN "
            + "  UPDATE " + Item.TABLE_NAME
            + "    SET  " + Item._STOCK + " = " + Item._STOCK + " - NEW." + Invoice._QTY
            + "  WHERE  " + Item._ID + " = NEW." + Invoice._ITEM_ID +"; "
            + " END;";

    private static final String CREATE_TABLE_DEPARTAMENTOS
            = "CREATE TABLE "   + Category.TABLE_NAME
            + "("+ Category._ID + " TEXT NOT NULL,"
            + Category._NAME    + " TEXT NOT NULL,"
            + Category._LASTMOD + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY("    + Category._ID + ")"
            + ");";

    private static final String CREATE_TABLE_NCF
            = "CREATE TABLE " + NCF.TABLE_NAME
            + "("+ NCF._ID    + " TEXT NOT NULL,"
            + NCF._NAME       + " TEXT NOT NULL,"
            + NCF._LASTMOD    + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + NCF._TYPE       + " TEXT DEFAULT '',"
            + "PRIMARY KEY("  + NCF._ID + ")"
            + ");\n";

    private static final String CREATE_TABLE_UNIDADES
            = "CREATE TABLE " + Unit.TABLE_NAME
            + "("+ Unit._ID   + " TEXT NOT NULL,"
            + Unit._NAME      + " TEXT NOT NULL,"
            + Unit._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY("  + Unit._ID + ")"
            + ");\n";

    private static final String CREATE_TABLE_ZONAS
            = "CREATE TABLE " + Zone.TABLE_NAME
            + "("+ Zone._ID   + " TEXT NOT NULL,"
            + Zone._NAME      + " TEXT NOT NULL,"
            + Zone._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY("  + Zone._ID + ")"
            + ");\n";

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
            + Client._ZONE_ID   + " TEXT DEFAULT '',"
            + Client._LAT       + " REAL DEFAULT 0,"
            + Client._LNG       + " REAL DEFAULT 0,"
            + Client._CR_LIMIT  + " NUMERIC DEFAULT 0,"
            + Client._PHONE     + " TEXT DEFAULT '',"
            + Client._STATUS    + " INTEGER NOT NULL,"
            + Client._CR_STATUS + " TEXT NOT NULL DEFAULT '"+Client.CREDIT_OPENED+"',"
            + Client._ID_REMOTE + " TEXT,"
            + Client._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + Client._NCF_ID    + " TEXT DEFAULT '',"
            + "PRIMARY KEY(" + Client._ID + ")"
            + ");\n";

    private static final String CREATE_TABLE_FACTURAS
            = "CREATE TABLE "    + Invoice.TABLE_NAME
            + "("+ Invoice._ID   + " TEXT NOT NULL,"
            + Invoice._CLIENT    + " TEXT NOT NULL,"
            + Invoice._COMMENT   + " TEXT NOT NULL,"
            + Invoice._INV_TYPE  + " INTEGER NOT NULL,"
            + Invoice._DISCOUNT  + " NUMERIC DEFAULT 0,"
            + Invoice._DATE      + " TEXT DEFAULT '',"
            + Invoice._NCF_SEQ   + " TEXT DEFAULT '',"
            + Invoice._LASTMOD   + " TEXT DEFAULT CURRENT_TIMESTAMP,"
            + Invoice._STATUS     + " INTEGER NOT NULL,"
            + Invoice._ID_REMOTE  + " TEXT,"
            + Invoice._MONEY      + " NUMERIC DEFAULT 0,"
            + "PRIMARY KEY(" + Invoice._ID + ")"
            + ");\n";

    private static final String CREATE_TABLE_FACTURAS_DETALLE
            = "CREATE TABLE "    + Invoice.TABLE_NAME_DETAILS
            + "("+ Invoice._ID   + " TEXT NOT NULL,"
            + Invoice._ITEM_ID   + " TEXT NOT NULL,"
            + Invoice._ITEM_NAME + " TEXT NOT NULL,"
            + Invoice._QTY       + " NUMERIC DEFAULT 1,"
            + Invoice._PRICE     + " NUMERIC DEFAULT 1,"
            + Invoice._TAX_RATE  + " NUMERIC DEFAULT 0,"
            + "FOREIGN KEY("+Invoice._ID+") REFERENCES "+Invoice.TABLE_NAME+"("+Invoice._ID+")"
            + ");\n";

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
            + ");\n";

    /*Tabla para almacenar las facturas que se producen durante un dia determinado*/
    private static final String CREATE_TABLE_VISTAS_FACTURAS
            = "CREATE TABLE "  + Diary.TABLE_DIARY_INV
            + "("+ Diary._ID   + " INTEGER NOT NULL,"
            + Invoice._ID      + " TEXT NOT NULL"
            //+ "PRIMARY KEY("   + Diary._ID + ")"
            + ");\n";



    public static final String VIEW_VISITAS_NAME = "v_get_diary_next_visits";


    /**
     * instancia unica para el OpenHelper
     */
    private static MySqliteOpenHelper instance;

    protected MySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_COMPANY);

        sqLiteDatabase.execSQL(CREATE_TABLE_ARTICULOS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Item.TABLE_NAME, Item._LASTMOD, Item._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Item.TABLE_NAME, Item._LASTMOD, Item._ID));


        sqLiteDatabase.execSQL(CREATE_TABLE_DEPARTAMENTOS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Category.TABLE_NAME, Category._LASTMOD, Category._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Category.TABLE_NAME, Category._LASTMOD, Category._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_UNIDADES);
        sqLiteDatabase.execSQL(createTriggerUpdate(Unit.TABLE_NAME, Unit._LASTMOD, Unit._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Unit.TABLE_NAME, Unit._LASTMOD, Unit._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_ZONAS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Zone.TABLE_NAME, Zone._LASTMOD, Zone._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Zone.TABLE_NAME, Zone._LASTMOD, Zone._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_NCF);
        sqLiteDatabase.execSQL(createTriggerUpdate(NCF.TABLE_NAME, NCF._LASTMOD, NCF._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(NCF.TABLE_NAME, NCF._LASTMOD, NCF._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_CLIENTES);
        sqLiteDatabase.execSQL(createTriggerUpdate(Client.TABLE_NAME, Client._LASTMOD, Client._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Client.TABLE_NAME, Client._LASTMOD, Client._ID));

        sqLiteDatabase.execSQL(CREATE_TABLE_FACTURAS);
        sqLiteDatabase.execSQL(CREATE_TABLE_FACTURAS_DETALLE);
        sqLiteDatabase.execSQL(createTriggerUpdate(Invoice.TABLE_NAME, Invoice._LASTMOD, Invoice._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Invoice.TABLE_NAME, Invoice._LASTMOD, Invoice._ID));

        sqLiteDatabase.execSQL(CREATE_PROCEDURE_STOCK);

        sqLiteDatabase.execSQL(CREATE_TABLE_VISITAS);
        sqLiteDatabase.execSQL(createTriggerUpdate(Diary.TABLE_NAME, Diary._LASTMOD, Diary._ID));
        sqLiteDatabase.execSQL(createTriggerInsert(Diary.TABLE_NAME, Diary._LASTMOD, Diary._ID));
        sqLiteDatabase.execSQL(CREATE_TABLE_VISTAS_FACTURAS);
        sqLiteDatabase.execSQL(createViewDiary());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            //Información de la empresa
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Company.TABLE_NAME);
            //Articulos
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Item.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Item.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Item.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + TRIGGER_REDUCE_STOCK);
            //Departamentos
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Category.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Category.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Category.TABLE_NAME));
            //Unidades
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Unit.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Unit.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Unit.TABLE_NAME));
            //Zonas de clientes
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Zone.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Zone.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Zone.TABLE_NAME));
            //NCF
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + NCF.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(NCF.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(NCF.TABLE_NAME));
            //Clientes
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Client.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Client.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Client.TABLE_NAME));
            //Facturas
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Invoice.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Invoice.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Invoice.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Invoice.TABLE_NAME_DETAILS);
            //Visitas
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Diary.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_UPDATE_LM.concat(Diary.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + PREFIX_TRIGGER_INSERT_LM.concat(Diary.TABLE_NAME));
            sqLiteDatabase.execSQL("DROP VIEW IF EXISTS "    + VIEW_VISITAS_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + Diary.TABLE_DIARY_INV);
            onCreate(sqLiteDatabase);
        }
    }

    private String createTriggerUpdate(String table, String fieldUpdate, String pk){
        String trigger = "CREATE TRIGGER {name} AFTER UPDATE ON {table} BEGIN update {table} SET {fu} = datetime('now', 'localtime') where {pk}=NEW.{pk}; END;\n";
        trigger = trigger.replace("{name}", PREFIX_TRIGGER_UPDATE_LM.concat(table));
        trigger = trigger.replace("{table}", table);
        trigger = trigger.replace("{fu}", fieldUpdate);
        trigger = trigger.replace("{pk}", pk);

        return trigger;
    }

    private String createTriggerInsert(String table, String fieldUpdate, String pk){
        String trigger = "CREATE TRIGGER {name} AFTER INSERT ON {table} BEGIN update {table} SET {fu} = datetime('now', 'localtime') where {pk}=NEW.{pk}; END;\n";
        trigger = trigger.replace("{name}", PREFIX_TRIGGER_INSERT_LM.concat(table));
        trigger = trigger.replace("{table}", table);
        trigger = trigger.replace("{fu}", fieldUpdate);
        trigger = trigger.replace("{pk}", pk);

        return trigger;
    }

    public static String createViewDiary(){
        return  "CREATE VIEW "+ VIEW_VISITAS_NAME
                +" AS "
                + "SELECT * FROM "+ Diary.TABLE_NAME
                + " WHERE strftime('%s',"+Diary._EVENT
                + ") >= strftime('%s',date('now','localtime')) ORDER BY "
                + Diary._EVENT + " ASC;\n";
    }

    public static MySqliteOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new MySqliteOpenHelper(context, MySqliteOpenHelper.DBNAME, null, MySqliteOpenHelper.VERSION);

        }

        return instance;
    }

    /**
     * Verfica que no hayan registros pendientes de actualizar en el servidor remoto, las tablas deben tener
     * la colummna {@link Models.ColumnsSqlite.ColumnsRemote#_STATUS}
     * @param database
     * @param tables
     * @return
     */
    public static boolean anyRegisterPending(SQLiteDatabase database, String... tables)
    {
        //Evitar excepciones del tipo NullPointerException
        if(database == null || tables == null || tables.length == 0 ){
            return false;
        }

        for(String table : tables) {
            Cursor cursor = database.query(table, null, ColumnsSqlite.ColumnsRemote._STATUS + " = ? "
                    , new String[]{String.valueOf(ColumnsSqlite.ColumnsRemote.STATUS_PENDING)}, null, null, null);


            if(cursor.moveToNext() && cursor.getCount() > 0){
                return true;
            }

        }

        return false;
    }

    /**
     * Borra el contenido de todas las tablas de la base de datos usada por {@link MySqliteOpenHelper}
     * @param context
     * @return
     */
    public static boolean deleteDataFromDb(Context context) {
        SQLiteDatabase database = getInstance(context).getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        while(cursor.moveToNext()){
           database.delete(cursor.getString(0),"1",null);
        }

        return true;
    }

    /**
     * Genera un script con las sentencias de las vistas, triggers y tablas una base de datos {@link SQLiteDatabase}
     * @param database Base de datos usada para obtener el script
     */
    public static void generateFile(SQLiteDatabase database){
        generateFile(database, new File(Environment.getExternalStorageDirectory(), "dbcreate.sql"));
    }

    /**
     * Genera un script con las sentencias de las vistas, triggers y tablas una base de datos {@link SQLiteDatabase}
     * @param database Base de datos usada para obtener el script
     * @param f archivo donde será guardado el script
     */
    public static  void generateFile(SQLiteDatabase database, File f){
        StringBuilder builder = new StringBuilder();
        Cursor cursor = database.rawQuery("SELECT sql FROM sqlite_master WHERE type IN ('table', 'trigger', 'view');", null);

        while(cursor.moveToNext()){
            builder.append(cursor.getString(0));
            builder.append('\n');
        }

        try {
            FileOutputStream stream = new FileOutputStream(f);
            String sqlStatements = builder.toString();
            stream.write(sqlStatements.getBytes(), 0, sqlStatements.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
