package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.NCF;
import Utils.DateUtils;

public class NCFController extends Controller<NCF> {

    public NCFController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<NCF> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<NCF> items = new ArrayList<>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(NCF.TABLE_NAME, null, null, null, null, null, NCF._NAME.concat(" ASC"));

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            NCF ncf = getDataFromCursor(cursor);
            items.add(ncf);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public NCF getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(NCF.TABLE_NAME, null, NCF._ID.concat(" =?"), new String[]{String.valueOf(id).trim()}, null, null, null);

        if (cursor.moveToNext()) {
            NCF ncf = getDataFromCursor(cursor);
            cursor.close();

            return ncf;
        }

        return null;
    }


    @Override
    public boolean update(NCF item) {
        ContentValues cv = getContentValues(item);
        cv.remove(NCF._ID);

        SQLiteDatabase database = getSqLiteDatabase();
        int result = database.update(NCF.TABLE_NAME, cv, NCF._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(NCF item) {
        ContentValues cv = getContentValues(item);
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(NCF.TABLE_NAME, null, cv);
            Log.d("SqlitePrueba", item.toString());
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(NCF item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(NCF.TABLE_NAME, NCF._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<NCF> categories) {
        if(categories == null || categories.isEmpty()) return false;

        for(NCF ncf : categories){
            insert(ncf);
        }

        return true;
    }

    @Override
    public boolean deleteAll(List<NCF> item) {

        for(NCF ncf : item){
            if(!delete(ncf)){
                return false;
            }
        }

        return true;
    }


    @Override
    public NCF getDataFromCursor(Cursor cursor) {
        NCF ncf = new NCF();

        ncf.setId(cursor.getString(cursor.getColumnIndex(NCF._ID)));
        ncf.setName(cursor.getString(cursor.getColumnIndex(NCF._NAME)));
        ncf.setType(cursor.getString(cursor.getColumnIndex(NCF._TYPE)));

        //Fecha de la ultima modificacion del archivo
        String date = cursor.getString(cursor.getColumnIndex(NCF._LASTMOD));
        Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
        ncf.setLastModification(lstMod);

        return ncf;
    }


    @Override
    public ContentValues getContentValues(NCF ncf) {
        ContentValues cv = new ContentValues();
        cv.put(NCF._ID, ncf.getId());
        cv.put(NCF._NAME, ncf.getName());
        cv.put(NCF._TYPE, ncf.getType());

        return cv;
    }
}