package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Zone;
import Utils.DateUtils;

public class ZoneController extends Controller<Zone> {

    public ZoneController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Zone> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Zone> items = new ArrayList<>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(Zone.TABLE_NAME, null, null, null, null, null, Zone._NAME.concat(" ASC"));

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Zone zone = getDataFromCursor(cursor);
            items.add(zone);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public Zone getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Zone.TABLE_NAME, null, Zone._ID.concat(" =?"), new String[]{String.valueOf(id).trim()}, null, null, null);

        if (cursor.moveToNext()) {
            Zone zone = getDataFromCursor(cursor);
            cursor.close();

            return zone;
        }

        return null;
    }


    @Override
    public boolean update(Zone item) {
        ContentValues cv = getContentValues(item);
        cv.remove(Zone._ID);

        SQLiteDatabase database = getSqLiteDatabase();
        int result = database.update(Zone.TABLE_NAME, cv, Zone._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(Zone item) {
        ContentValues cv = getContentValues(item);


        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Zone.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Zone item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Zone.TABLE_NAME, Zone._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Zone> categories) {
        if(categories == null || categories.isEmpty()) return false;

        for(Zone zone : categories){
            insert(zone);
        }

        return true;
    }

    @Override
    public boolean deleteAll(List<Zone> item) {

        for(Zone zone : item){
            if(!delete(zone)){
                return false;
            }
        }

        return true;
    }


    @Override
    public Zone getDataFromCursor(Cursor cursor) {
        Zone zone = new Zone();

        zone.setId(cursor.getString(cursor.getColumnIndex(Zone._ID)));
        zone.setName(cursor.getString(cursor.getColumnIndex(Zone._NAME)));

        //Fecha de la ultima modificacion del archivo
        String date = cursor.getString(cursor.getColumnIndex(Zone._LASTMOD));
        Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
        zone.setLastModification(lstMod);

        return zone;
    }


    @Override
    public ContentValues getContentValues(Zone item) {
        ContentValues cv = new ContentValues();
        cv.put(Zone._ID, item.getId());
        cv.put(Zone._NAME, item.getName());

        return cv;
    }
}