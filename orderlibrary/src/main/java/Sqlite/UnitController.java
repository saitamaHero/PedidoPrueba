package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Unit;
import Models.Item;
import Models.Unit;
import Utils.DateUtils;

public class UnitController extends Controller<Unit> {
    
    public UnitController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Unit> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Unit> items = new ArrayList<>();
        
        Cursor cursor;
        cursor = sqLiteDatabase.query(Unit.TABLE_NAME, null, null, null, null, null, null);
        
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Unit unit = new Unit();
            unit.setId(cursor.getString(cursor.getColumnIndex(Unit._ID)));
            unit.setName(cursor.getString(cursor.getColumnIndex(Unit._NAME)));


            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Unit._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            unit.setLastModification(lstMod);

            items.add(unit);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public Unit getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Unit.TABLE_NAME, null, Unit._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            Unit unit = new Unit();
            unit.setId(cursor.getString(cursor.getColumnIndex(Unit._ID)));
            unit.setName(cursor.getString(cursor.getColumnIndex(Unit._NAME)));
            

            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Unit._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            unit.setLastModification(lstMod);

            return unit;
        }


        return null;
    }

    @Override
    public boolean update(Unit item) {
        ContentValues cv = new ContentValues();
        cv.put(Unit._NAME, item.getName());
        
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.update(Unit.TABLE_NAME, cv, Unit._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(Unit item) {
        ContentValues cv = new ContentValues();
        cv.put(Unit._ID, item.getId());
        cv.put(Unit._NAME, item.getName());
        
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Unit.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Unit item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Unit.TABLE_NAME, Unit._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Unit> categories) {
        if(categories == null || categories.isEmpty()) return false;
        
        for(Unit category : categories){ 
            insert(category);
        }
        
        return true;
    }

    @Override
    public boolean deleteAll(List<Unit> item) {

        for(Unit category : item){
            if(!delete(category)){
                return false;
            }
        }

        return true;
    }
}
