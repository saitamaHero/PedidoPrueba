package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Category;
import Models.Item;
import Models.Unit;
import Utils.DateUtils;

public class ItemController extends Controller<Item> {

    public ItemController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Item> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Item> items = new ArrayList<>();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Item.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Item item = getDataFromCursor(cursor);
            items.add(item);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public List<Item> getAll(int count) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Item> items = new ArrayList<>(count);
        Cursor cursor;

        cursor = sqLiteDatabase.query(Item.TABLE_NAME, null, null, null, null, null, null, String.valueOf(count));
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Item item = getDataFromCursor(cursor);
            items.add(item);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public List<Item> getAllLike(String likeStr) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Item> items = new ArrayList<>();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Item.TABLE_NAME, null, Item._NAME.concat(" LIKE ?"),
                new String[]{"%" + likeStr + "%"}, null, null, null);//.concat(" AND ").concat(Item._STOCK).concat(">?"),String.valueOf(0.00)
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Item item = getDataFromCursor(cursor);
            items.add(item);

            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public List<Item> getAll(String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Item> items = new ArrayList<>();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Item.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Item item = getDataFromCursor(cursor);
            items.add(item);

            cursor.moveToNext();
        }

        cursor.close();

        return items;
    }

    @Override
    public Item getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Item.TABLE_NAME, null, Item._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            Item item = getDataFromCursor(cursor);
            return item;
        }

        return null;
    }

    @Override
    public boolean update(Item item) {
        ContentValues cv = getContentValues(item);
        cv.remove(Item._ID);

        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.update(Item.TABLE_NAME, cv, Item._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(Item item) {
        ContentValues cv = getContentValues(item);

        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Item.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "Error: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Item item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Item.TABLE_NAME, Item._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Item> items) {

        for (Item i : items) {
            if (!insert(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean deleteAll(List<Item> items) {
        SQLiteDatabase database = getSqLiteDatabase();

        if (items == null) {
            return database.delete(Item.TABLE_NAME, "1", null) > 0;
        } else {
            for (Item i : items) {
                if (!delete(i)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public Item getDataFromCursor(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getString(cursor.getColumnIndex(Item._ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(Item._NAME)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndex(Item._PRICE)));
        item.setStock(cursor.getDouble(cursor.getColumnIndex(Item._STOCK)));
        item.setTaxRate(cursor.getDouble(cursor.getColumnIndex(Item._TAX_RATE)));
        item.setQuantity(1);
        item.setCost(cursor.getDouble(cursor.getColumnIndex(Item._COST)));

        CategoryController categoryController = new CategoryController(getSqLiteDatabase());
        Category category  = categoryController.getById(cursor.getString(cursor.getColumnIndex(Item._CAT)));
        item.setCategory(category);

        UnitController unitController = new UnitController(getSqLiteDatabase());
        Unit unit = unitController.getById(cursor.getString(cursor.getColumnIndex(Item._UNIT)));
        item.setUnit(unit);

        //Fecha de la ultima modificacion del archivo
        String date = cursor.getString(cursor.getColumnIndex(Item._LASTMOD));
        Date lstMod = DateUtils.convertToDate(date, Utils.DateUtils.YYYY_MM_DD_HH_mm_ss);
        item.setLastModification(lstMod);

        //Foto del archivo
        File photo = new File(cursor.getString(cursor.getColumnIndex(Item._PHOTO)));
        item.setPhoto(Uri.fromFile(photo));

        return item;
    }


    @Override
    public ContentValues getContentValues(Item item) {
        ContentValues cv = new ContentValues();

        cv.put(Item._ID, item.getId());
        cv.put(Item._NAME, item.getName());
        cv.put(Item._PRICE, item.getPrice());
        cv.put(Item._PHOTO, item.getPhoto().getPath());
        cv.put(Item._STOCK, item.getStock());
        cv.put(Item._COST, item.getCost());
        cv.put(Item._TAX_RATE, item.getTaxRate());

        if (!Category.UNKNOWN_CATEGORY.equals(item.getCategory())) {
            cv.put(Item._CAT, item.getCategory().getId());
        }

        if (!Unit.UNKNOWN_UNIT.equals(item.getUnit())) {
            cv.put(Item._UNIT, item.getUnit().getId());
        }

        return cv;
    }
}
