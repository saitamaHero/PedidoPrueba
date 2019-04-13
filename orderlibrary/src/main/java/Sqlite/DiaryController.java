package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Diary;
import Models.Item;
import Utils.DateUtils;

public class DiaryController extends Controller<Diary> {
    private static final String TAG = "DiaryController";

    public DiaryController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Diary> getAll() {
        SQLiteDatabase sqliteDatabase = getSqLiteDatabase();
        List<Diary> items = new ArrayList<>();
        
        Cursor cursor;
        cursor = sqliteDatabase.query(Diary.TABLE_NAME, null, null, null, null, null, null);
        
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Diary unit = getDataFromCursor(cursor);
            items.add(unit);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public List<Diary> getAllById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Diary> items = new ArrayList<>();

        String incompleteCondition = Diary._START_TIME +" IS NULL AND "+Diary._END_TIME + " IS NULL";

        Cursor cursor = sqLiteDatabase.query(MySqliteOpenHelper.VIEW_VISITAS_NAME, null,
                Diary._CLIENT_ID.concat(" =? AND ").concat(incompleteCondition)
                , new String[]{String.valueOf(id)},
                null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Diary unit = getDataFromCursor(cursor);
            items.add(unit);

            cursor.moveToNext();
        }

        return items;
    }


    @Override
    public boolean exists(String field, Object object) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.query(Diary.TABLE_NAME, null, field.concat(" =?"),
                new String[]{String.valueOf(object)}, null, null, null);

        return cursor.getCount() == 1;
    }

    public List<Diary> getAllCompleteById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Calendar calendar = Calendar.getInstance();
        List<Diary> items = new ArrayList<>();

        String completeCondition = Diary._START_TIME +" IS NOT NULL AND "+Diary._END_TIME + " IS NOT NULL";

        Cursor cursor = sqLiteDatabase.query(Diary.TABLE_NAME, null,
                Diary._CLIENT_ID.concat(" =?").concat(" AND ").concat(Diary._EVENT).concat( ">= ? AND ").concat(completeCondition)
                , new String[]{String.valueOf(id), DateUtils.formatDate(calendar.getTime(), DateUtils.YYYY_MM_DD_HH_mm_ss)},
                null, null, Diary._EVENT.concat(" ASC"));
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Diary unit = getDataFromCursor(cursor);
            items.add(unit);

            cursor.moveToNext();
        }

        return items;

    }

    @Override
    public Diary getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Diary.TABLE_NAME, null, Diary._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            return getDataFromCursor(cursor);
        }

        return null;
    }

    @Override
    public List<Diary> getAllRange(Object id, String lower, String upper) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Diary> items = new ArrayList<>();
        Cursor cursor;

        String selection = String.format(Locale.getDefault(), "%s=? AND %s>=? AND %s <=?",
                Diary._CLIENT_ID, Diary._EVENT, Diary._EVENT);
        cursor = sqLiteDatabase.query(Diary.TABLE_NAME, null,
                selection, new String[]{String.valueOf(id), lower, upper},
                null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Diary unit = getDataFromCursor(cursor);
            items.add(unit);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public boolean update(Diary item) {
        ContentValues cv = getContentValues(item);
        cv.remove(Diary._ID);
        cv.remove(Diary._EVENT);
        cv.remove(Diary._DURATION);
        
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.update(Diary.TABLE_NAME, cv, Diary._ID.concat("=?"), new String[]{String.valueOf(item.getId())});

        Log.d(TAG, "update: result="+result);
        return result == 1;
    }

    @Override
    public boolean insert(Diary item) {
        ContentValues cv = getContentValues(item);
        cv.remove(Diary._ID);
        
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Diary.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Diary item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Diary.TABLE_NAME, Diary._ID.concat("=?"), new String[]{String.valueOf(item.getId())});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Diary> categories) {
        if(categories == null || categories.isEmpty()) return false;
        
        for(Diary category : categories){ 
            insert(category);
        }
        
        return true;
    }

    @Override
    public boolean deleteAll(List<Diary> item) {

        for(Diary category : item){
            if(!delete(category)){
                return false;
            }
        }

        return true;
    }

    public Client getClient(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Client.TABLE_NAME, null, Client._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            Client client = new ClientController(getSqLiteDatabase()).getDataFromCursor(cursor);
            cursor.close();
            return client;
        }

        return null;
    }

    @Override
    public Diary getDataFromCursor(Cursor cursor) {
        Diary diary = new Diary();

        //ID
        diary.setId(cursor.getLong(cursor.getColumnIndex(Diary._ID)));
        diary.setComment(cursor.getString(cursor.getColumnIndex(Diary._COMMENT)));

        //Cliente
        Client client = getClient(cursor.getString(cursor.getColumnIndex(Diary._CLIENT_ID)));
        diary.setClientToVisit(client);

        //Duracion aprox. de la visita
        diary.setDuration(cursor.getInt(cursor.getColumnIndex(Diary._DURATION)));
        //diary.setComment(cursor.getString(cursor.getColumnIndex(Diary._COMMENT)));

        //Fecha en la que está planeada la visita
        String dataEvent = cursor.getString(cursor.getColumnIndex(Diary._EVENT));
        diary.setDateEvent(DateUtils.convertToDate(dataEvent, DateUtils.YYYY_MM_DD_HH_mm_ss));

        //Inicio y fin de la visita
        String startTime = cursor.getString(cursor.getColumnIndex(Diary._START_TIME));
        diary.setStartTime(DateUtils.convertToDate(startTime, DateUtils.YYYY_MM_DD_HH_mm_ss));

        String endTime = cursor.getString(cursor.getColumnIndex(Diary._END_TIME));
        diary.setEndTime(DateUtils.convertToDate(endTime, DateUtils.YYYY_MM_DD_HH_mm_ss));

        //Fecha de la ultima modificacion del archivo
        String date = cursor.getString(cursor.getColumnIndex(Client._LASTMOD));
        Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
        diary.setLastModification(lstMod);

        //Datos remotos
        diary.setStatus(cursor.getInt(cursor.getColumnIndex(Diary._STATUS)));
        diary.setRemoteId(cursor.getString(cursor.getColumnIndex(Diary._ID_REMOTE)));

        return diary;
    }

    @Override
    public ContentValues getContentValues(Diary item) {
        ContentValues cv = new ContentValues();

        cv.put(Diary._ID, item.getId());
        cv.put(Diary._DURATION, item.getDuration());
        cv.put(Diary._EVENT,    DateUtils.formatDate(item.getDateEvent(), DateUtils.YYYY_MM_DD_HH_mm_ss));
        cv.put(Diary._COMMENT, item.getComment());

        if(item.getStartTime() != null){
            cv.put(Diary._START_TIME, DateUtils.formatDate(item.getStartTime(), DateUtils.YYYY_MM_DD_HH_mm_ss) );
        }

        if(item.getEndTime() != null){
            cv.put(Diary._END_TIME, DateUtils.formatDate(item.getEndTime(), DateUtils.YYYY_MM_DD_HH_mm_ss) );
        }

        if(item.getClientToVisit() != null){
            cv.put(Diary._CLIENT_ID, item.getClientToVisit().getId());
        }

        ColumnsSqlite.ColumnsRemote columnsRemote = item;
        cv.put(Diary._STATUS, columnsRemote.getStatus());
        cv.put(Diary._ID_REMOTE, String.valueOf(columnsRemote.getRemoteId()));

        return cv;
    }
}