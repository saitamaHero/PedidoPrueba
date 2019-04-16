package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Models.Diary;
import Models.Invoice;
import Models.Item;

public class InvoiceDiaryController extends ControllerDetails<Invoice>{
    InvoiceController invoiceController;

    public InvoiceDiaryController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
        this.invoiceController = new InvoiceController(sqLiteDatabase);
    }


    @Override
    public List<Invoice> getAllById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Invoice> invoices = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(Diary.TABLE_DIARY_INV, null, Diary._ID.concat("=?"),
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String invoiceNumber = cursor.getString(cursor.getColumnIndex(Invoice._ID));
            Invoice invoice =  this.invoiceController.getByInvoiceId(invoiceNumber);

            if(invoice != null){
                invoices.add(invoice);
            }

            cursor.moveToNext();
        }

        return invoices;
    }

    @Override
    public boolean insertAllWithId(List<Invoice> items, Object id) {
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        database.beginTransaction();

        for(Invoice i : items){
            ContentValues cv = getContentValues(i, id);
            result = database.insert(Diary.TABLE_DIARY_INV, null, cv);
        }

        database.setTransactionSuccessful();
        database.endTransaction();




        return result != -1;
    }

    @Override
    public boolean deleteAllWithId(Object id) {
        return false;
    }

    @Override
    public ContentValues getContentValues(Invoice item, Object id) {
        ContentValues cv = new ContentValues();
        cv.put(Diary._ID, (Long) id);
        cv.put(Invoice._ID, item.getId());
        return cv;
    }
}
