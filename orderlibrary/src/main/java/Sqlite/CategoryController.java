package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Category;
import Models.Item;
import Utils.DateUtils;

public class CategoryController extends Controller<Category> {

    public CategoryController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Category> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Category> items = new ArrayList<>();

        Cursor cursor;
        cursor = sqLiteDatabase.query(Category.TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Category category = new Category();
            category.setId(cursor.getString(cursor.getColumnIndex(Category._ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(Category._NAME)));


            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Category._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, Utils.DateUtils.YYYY_MM_DD_HH_mm_ss);
            category.setLastModification(lstMod);

            items.add(category);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public Category getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Category.TABLE_NAME, null, Category._ID.concat(" =?"), new String[]{String.valueOf(id).trim()}, null, null, null);

        if (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getString(cursor.getColumnIndex(Category._ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(Category._NAME)));

            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Category._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, Utils.DateUtils.YYYY_MM_DD_HH_mm_ss);
            category.setLastModification(lstMod);

            cursor.close();

            return category;
        }

        return null;
    }


    @Override
    public boolean update(Category item) {
        ContentValues cv = new ContentValues();
        cv.put(Category._NAME, item.getName());

        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.update(Category.TABLE_NAME, cv, Category._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(Category item) {
        ContentValues cv = new ContentValues();
        cv.put(Category._ID, item.getId());
        cv.put(Category._NAME, item.getName());

        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Category.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Category item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Category.TABLE_NAME, Category._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Category> categories) {
        if(categories == null || categories.isEmpty()) return false;

        for(Category category : categories){
            insert(category);
        }

        return true;
    }

    @Override
    public boolean deleteAll(List<Category> item) {

        for(Category category : item){
            if(!delete(category)){
                return false;
            }
        }

        return true;
    }
}
