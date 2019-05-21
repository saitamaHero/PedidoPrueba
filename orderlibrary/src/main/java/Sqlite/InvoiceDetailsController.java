package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Invoice;
import Models.Item;

public class InvoiceDetailsController extends ControllerDetails<Item> {
    private static final String TAG = "InvoiceDetailsControlle";

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

        database.beginTransaction();

        for(Item i : items){
            ContentValues cv = getContentValues(i, id);
            result = database.insert(Invoice.TABLE_NAME_DETAILS, null, cv);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

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

        Item item = controller.getById(cursor.getString(cursor.getColumnIndex(Invoice._ITEM_ID)));

        if(item != null) {
            item.setStock(0.0);
            item.setName(       cursor.getString(cursor.getColumnIndex(Invoice._ITEM_NAME)));
            item.setTaxRate(    cursor.getDouble(cursor.getColumnIndex(Invoice._TAX_RATE)));
            item.setQuantity(   cursor.getDouble(cursor.getColumnIndex(Invoice._QTY)));
            item.setPrice(      cursor.getDouble(cursor.getColumnIndex(Invoice._PRICE)));

            Log.d(TAG, "getDataFromCursor: taxRate: "+item.getTaxRate());
            return item;
        }

        return null;
    }

    @Override
    public ContentValues getContentValues(Item item, Object id) {
        ContentValues cv = new ContentValues();

        cv.put(Invoice._ID,         String.valueOf(id));
        cv.put(Invoice._ITEM_ID,    item.getId());
        cv.put(Invoice._ITEM_NAME,  item.getName());
        cv.put(Invoice._PRICE,      item.getPrice());
        cv.put(Invoice._QTY,        item.getQuantity());
        cv.put(Invoice._TAX_RATE,   item.getTaxRate());


        return cv;
    }
}
