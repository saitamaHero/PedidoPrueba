package Sqlite;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import Models.Category;
import Models.Item;
import Models.Unit;

public class ItemController extends Controller<Item> {

    public ItemController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Item> getAll() {
        return null;
    }

    @Override
    public Item getById(Object id) {
        return null;
    }

    @Override
    public boolean update(Item item) {
        return false;
    }

    @Override
    public boolean insert(Item item) {
        ContentValues cv = new ContentValues();

        cv.put(Item._ID, item.getId());
        cv.put(Item._NAME, item.getName());
        cv.put(Item._PRICE, item.getPrice());
        cv.put(Item._PHOTO, item.getPhoto().getPath());
        cv.put(Item._STOCK, item.getStock());

        if(!Category.UNKNOWN_CATEGORY.equals(item.getCategory())){
            cv.put(Item._CAT, item.getCategory().getId());
        }

        if(!Unit.UNKNOWN_UNIT.equals(item.getUnit())){
            cv.put(Item._UNIT, item.getCategory().getId());
        }

        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try
        {
            result = database.insertOrThrow(Item.TABLE_NAME, null, cv);
        }catch (SQLException e){
            Log.d("SqlitePrueba", "Error: "+e.getMessage());
        }




        return result != -1;
    }

    @Override
    public boolean delete(Item item) {
        return false;
    }

    @Override
    public boolean insertAll(List<Item> item) {
        return false;
    }

    @Override
    public boolean deleteAll(List<Item> item) {
        return false;
    }
}
