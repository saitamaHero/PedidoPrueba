package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.Invoice;
import Models.Item;

public class InvoiceDetailsController extends ControllerDetails<Item> {

    public InvoiceDetailsController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Item> getAllById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Item> items = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(Invoice.TABLE_NAME_DETAILS, null, Invoice._ID.concat("=?"),
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            items.add(getDataFromCursor(cursor));
            cursor.moveToNext();
        }

        return items;
    }

    @Override
    public boolean insertAllWithId(List<Item> items, Object id) {
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        for(Item i : items){
            ContentValues cv = getContentValues(i, id);

            try {
                result = database.insertOrThrow(Invoice.TABLE_NAME_DETAILS, null, cv);
            } catch (SQLException e) {
                Log.d("SqlitePrueba", "ErrorClient: " + e.getMessage());
            }
        }

        return result != -1;
    }

    @Override
    public boolean deleteAllWithId(Object id) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Invoice.TABLE_NAME_DETAILS, Invoice._ID.concat("=?"), new String[]{String.valueOf(id)});

        return result > 0;
    }

    @Override
    public Item getDataFromCursor(Cursor cursor) {
        ItemController controller = new ItemController(getSqLiteDatabase());

        Item item = controller.getById(cursor.getString(cursor.getColumnIndex(Invoice.ITEM_ID)));

        if(item != null) {
            item.setTaxRate(cursor.getDouble(cursor.getColumnIndex(Invoice._TAX_RATE)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndex(Invoice._QTY)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndex(Invoice._PRICE)));

            return item;
        }

        return null;
    }

    @Override
    public ContentValues getContentValues(Item item, Object id) {
        ContentValues cv = new ContentValues();

        cv.put(Invoice._ID, String.valueOf(id));
        cv.put(Invoice.ITEM_ID, item.getId());
        cv.put(Invoice._PRICE, item.getPrice());
        cv.put(Invoice._QTY, item.getId());
        cv.put(Invoice._TAX_RATE, item.getTaxRate());

        return cv;
    }
}
