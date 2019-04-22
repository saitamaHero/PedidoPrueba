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

import Models.Client;
import Models.ColumnsSqlite;
import Models.Invoice;
import Models.Item;
import Utils.DateUtils;

public class InvoiceController extends Controller<Invoice> {

    public InvoiceController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Invoice> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Invoice> invoices = new ArrayList<>();
        Cursor cursor;

        //InvoiceDetailsController controller = new InvoiceDetailsController(sqLiteDatabase);

        cursor = sqLiteDatabase.query(Invoice.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Invoice invoice = getDataFromCursor(cursor);
            //invoice.setItems(controller.getAllById(invoice.getId()));

            invoices.add(invoice);

            cursor.moveToNext();
        }

        return invoices;
    }

    @Override
    public Invoice getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Invoice.TABLE_NAME, null, Invoice._CLIENT.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            return getDataFromCursor(cursor);
        }

        return null;
    }

    public Invoice getByInvoiceId(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Invoice.TABLE_NAME, null, Invoice._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            return getDataFromCursor(cursor);
        }

        return null;
    }

    @Override
    public List<Invoice> getAllById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Invoice> invoices = new ArrayList<>();
        Cursor cursor;

        //InvoiceDetailsController controller = new InvoiceDetailsController(sqLiteDatabase);

        cursor = sqLiteDatabase.query(Invoice.TABLE_NAME, null, Invoice._CLIENT.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Invoice invoice = getDataFromCursor(cursor);
            //invoice.setItems(controller.getAllById(invoice.getId()));

            invoices.add(invoice);

            cursor.moveToNext();
        }

        return invoices;
    }

    @Override
    public boolean update(Invoice item) {
        ContentValues cv = getContentValues(item);
        cv.remove(Invoice._ID);
        cv.remove(Invoice._CLIENT);
        cv.remove(Invoice._DATE);
        cv.remove(Invoice._NCF_SEQ);
        cv.remove(Invoice._MONEY);
        cv.remove(Invoice._INV_TYPE);

        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.update(Invoice.TABLE_NAME, cv, Invoice._ID.concat("=?"),new String[]{item.getId()});
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "ErrorClient: " + e.getMessage());
        }

        return result > 0; //&& detailsInserted;
    }

    @Override
    public boolean insert(Invoice item) {
        ContentValues cv = getContentValues(item);
        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;
        boolean detailsInserted = false;
        InvoiceDetailsController controller = new InvoiceDetailsController(database);

        try {
            result = database.insertOrThrow(Invoice.TABLE_NAME, null, cv);

            detailsInserted = controller.insertAllWithId(item.getItems(), item.getId());
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "ErrorClient: " + e.getMessage());
        }

        return result != -1 && detailsInserted;
    }

    @Override
    public boolean delete(Invoice item) {
        SQLiteDatabase database = getSqLiteDatabase();
        InvoiceDetailsController controller = new InvoiceDetailsController(database);

        boolean deleted;

        deleted = controller.deleteAllWithId(item.getId());

        int result = database.delete(Invoice.TABLE_NAME, Invoice._ID.concat("=?"), new String[]{item.getId()});


        return result == 1 && deleted;
    }

    @Override
    public boolean insertAll(List<Invoice> items) {
        for (Invoice i : items) {
            if (!insert(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean deleteAll(List<Invoice> items) {
        SQLiteDatabase database = getSqLiteDatabase();

        if (items == null) {
            return database.delete(Invoice.TABLE_NAME, "1", null) > 0;
        } else {
            for (Invoice i : items) {
                if (!delete(i)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public Invoice getDataFromCursor(Cursor cursor) {
        Invoice invoice = new Invoice();

        invoice.setId(cursor.getString( cursor.getColumnIndex(Invoice._ID)));
        int invoiceType = cursor.getInt(cursor.getColumnIndex(Invoice._INV_TYPE));
        invoice.setInvoiceType(Invoice.InvoicePayment.values()[invoiceType]);
        invoice.setComment(      cursor.getString(cursor.getColumnIndex(Invoice._COMMENT)));
        invoice.setNcfSequence(  cursor.getString(cursor.getColumnIndex(Invoice._NCF_SEQ)));
        invoice.setMoneyReceived(cursor.getDouble(cursor.getColumnIndex(Invoice._MONEY)));

        String date = cursor.getString(cursor.getColumnIndex(Invoice._DATE));
        invoice.setDate(DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss));

        invoice.setDiscount(cursor.getDouble(cursor.getColumnIndex(Invoice._DISCOUNT)));

        //Fecha de la ultima modificacion del archivo
        date = cursor.getString(cursor.getColumnIndex(Invoice._LASTMOD));
        Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
        invoice.setLastModification(lstMod);

        //Datos remotos
        invoice.setStatus(cursor.getInt(cursor.getColumnIndex(Client._STATUS)));
        invoice.setRemoteId(cursor.getString(cursor.getColumnIndex(Client._ID_REMOTE)));

        //Obteniendo los datos del detalle
        InvoiceDetailsController controller = new InvoiceDetailsController(getSqLiteDatabase());
        invoice.setItems(controller.getAllById(invoice.getId()));

        //Obtener datos del cliente cliente
        String id = cursor.getString(cursor.getColumnIndex(Invoice._CLIENT));
        ClientController clientController = new ClientController(getSqLiteDatabase());
        Client client = clientController.getById(id);
        invoice.setClient(client);

        return invoice;
    }

    @Override
    public ContentValues getContentValues(Invoice item) {
        ContentValues cv = new ContentValues();

        cv.put(Invoice._ID, item.getId());
        cv.put(Invoice._CLIENT, item.getClient().getId());
        cv.put(Invoice._DISCOUNT, item.getDiscount());
        cv.put(Invoice._MONEY, item.getMoneyReceived());
        cv.put(Invoice._COMMENT, item.getComment());
        cv.put(Invoice._DATE, DateUtils.formatDate(item.getDate(), DateUtils.YYYY_MM_DD_HH_mm_ss));
        cv.put(Invoice._INV_TYPE, item.getInvoiceType().ordinal());
        cv.put(Invoice._NCF_SEQ, item.getNcfSequence());

        ColumnsSqlite.ColumnsRemote columnsRemote = item;
        cv.put(Invoice._STATUS, columnsRemote.getStatus());
        cv.put(Invoice._ID_REMOTE, String.valueOf(columnsRemote.getRemoteId()));

        return cv;
    }

    @Override
    public boolean exists(String field, Object object) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor = sqLiteDatabase.query(Invoice.TABLE_NAME, null, field.concat(" =?"),
                new String[]{String.valueOf(object)}, null, null, null);

        return cursor.getCount() == 1;
    }
}
