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
import Models.Client;
import Models.Client;
import Models.Unit;
import Utils.DateUtils;
import Utils.NumberUtils;

public class ClientController extends Controller<Client> {

    public ClientController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    @Override
    public List<Client> getAll() {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Client> clients = new ArrayList<>();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Client.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Client client = new Client();
            client.setId(cursor.getString(cursor.getColumnIndex(Client._ID)));
            client.setName(cursor.getString(cursor.getColumnIndex(Client._NAME)));
            client.setIdentityCard(cursor.getString(cursor.getColumnIndex(Client._IDCARD)));
            client.setEmail(cursor.getString(cursor.getColumnIndex(Client._EMAIL)));
            client.setAddress(cursor.getString(cursor.getColumnIndex(Client._ADDRESS)));
            client.setCreditLimit(cursor.getDouble(cursor.getColumnIndex(Client._CR_LIMIT)));


            //Latitud y longitud que representa la ubicacionn en el mapa
            float lat = cursor.getFloat(cursor.getColumnIndex(Client._LAT));
            float lng = cursor.getFloat(cursor.getColumnIndex(Client._LNG));

            client.setLatlng(lat, lng);

            //Fecha de cumpleaño
            String bdate = cursor.getString(cursor.getColumnIndex(Client._BIRTH));
            client.setBirthDate(DateUtils.convertToDate(bdate, DateUtils.YYYY_MM_DD));

            //Fecha de entrada
            String edate = cursor.getString(cursor.getColumnIndex(Client._ENTERED));
            client.setEnteredDate(DateUtils.convertToDate(edate, DateUtils.YYYY_MM_DD));


            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Client._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            client.setLastModification(lstMod);

            //Foto del archivo
            File photo = new File(cursor.getString(cursor.getColumnIndex(Client._PHOTO)));
            client.setProfilePhoto(Uri.fromFile(photo));

            clients.add(client);

            cursor.moveToNext();
        }

        return clients;
    }

    @Override
    public List<Client> getAll(int count) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Client> clients = new ArrayList<>(count);
        Cursor cursor;

        cursor = sqLiteDatabase.query(Client.TABLE_NAME, null, null, null, null, null, null, String.valueOf(count));
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Client client = new Client();
            client.setId(cursor.getString(cursor.getColumnIndex(Client._ID)));
            client.setName(cursor.getString(cursor.getColumnIndex(Client._NAME)));
            client.setIdentityCard(cursor.getString(cursor.getColumnIndex(Client._IDCARD)));
            client.setEmail(cursor.getString(cursor.getColumnIndex(Client._EMAIL)));
            client.setAddress(cursor.getString(cursor.getColumnIndex(Client._ADDRESS)));
            client.setCreditLimit(cursor.getDouble(cursor.getColumnIndex(Client._CR_LIMIT)));


            //Latitud y longitud que representa la ubicacionn en el mapa
            float lat = cursor.getFloat(cursor.getColumnIndex(Client._LAT));
            float lng = cursor.getFloat(cursor.getColumnIndex(Client._LNG));

            client.setLatlng(lat, lng);

            //Fecha de cumpleaño
            String bdate = cursor.getString(cursor.getColumnIndex(Client._BIRTH));
            client.setBirthDate(DateUtils.convertToDate(bdate, DateUtils.YYYY_MM_DD));

            //Fecha de entrada
            String edate = cursor.getString(cursor.getColumnIndex(Client._ENTERED));
            client.setEnteredDate(DateUtils.convertToDate(edate, DateUtils.YYYY_MM_DD));


            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Client._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            client.setLastModification(lstMod);

            //Foto del archivo
            File photo = new File(cursor.getString(cursor.getColumnIndex(Client._PHOTO)));
            client.setProfilePhoto(Uri.fromFile(photo));

            clients.add(client);

            cursor.moveToNext();
        }

        return clients;
    }

    @Override
    public List<Client> getAllLike(String likeStr) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        List<Client> clients = new ArrayList<>();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Client.TABLE_NAME, null, Client._NAME.concat(" LIKE ?"),
                new String[]{"%" + likeStr + "%"}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Client client = new Client();
            client.setId(cursor.getString(cursor.getColumnIndex(Client._ID)));
            client.setName(cursor.getString(cursor.getColumnIndex(Client._NAME)));
            client.setIdentityCard(cursor.getString(cursor.getColumnIndex(Client._IDCARD)));
            client.setEmail(cursor.getString(cursor.getColumnIndex(Client._EMAIL)));
            client.setAddress(cursor.getString(cursor.getColumnIndex(Client._ADDRESS)));
            client.setCreditLimit(cursor.getDouble(cursor.getColumnIndex(Client._CR_LIMIT)));

            //Latitud y longitud que representa la ubicacionn en el mapa
            float lat = cursor.getFloat(cursor.getColumnIndex(Client._LAT));
            float lng = cursor.getFloat(cursor.getColumnIndex(Client._LNG));

            client.setLatlng(lat, lng);

            //Fecha de cumpleaño
            String bdate = cursor.getString(cursor.getColumnIndex(Client._BIRTH));
            client.setBirthDate(DateUtils.convertToDate(bdate, DateUtils.YYYY_MM_DD));

            //Fecha de entrada
            String edate = cursor.getString(cursor.getColumnIndex(Client._ENTERED));
            client.setEnteredDate(DateUtils.convertToDate(edate, DateUtils.YYYY_MM_DD));


            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Client._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            client.setLastModification(lstMod);

            //Foto del archivo
            File photo = new File(cursor.getString(cursor.getColumnIndex(Client._PHOTO)));
            client.setProfilePhoto(Uri.fromFile(photo));

            clients.add(client);

            cursor.moveToNext();
        }

        return clients;
    }

    @Override
    public Client getById(Object id) {
        SQLiteDatabase sqLiteDatabase = getSqLiteDatabase();
        Cursor cursor;

        cursor = sqLiteDatabase.query(Client.TABLE_NAME, null, Client._ID.concat(" =?"), new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToNext()) {
            Client client = new Client();
            client.setId(cursor.getString(cursor.getColumnIndex(Client._ID)));
            client.setName(cursor.getString(cursor.getColumnIndex(Client._NAME)));
            client.setIdentityCard(cursor.getString(cursor.getColumnIndex(Client._IDCARD)));
            client.setEmail(cursor.getString(cursor.getColumnIndex(Client._EMAIL)));
            client.setAddress(cursor.getString(cursor.getColumnIndex(Client._ADDRESS)));
            client.setCreditLimit(cursor.getDouble(cursor.getColumnIndex(Client._CR_LIMIT)));

            //Latitud y longitud que representa la ubicacionn en el mapa
            float lat = cursor.getFloat(cursor.getColumnIndex(Client._LAT));
            float lng = cursor.getFloat(cursor.getColumnIndex(Client._LNG));

            client.setLatlng(lat, lng);

            //Fecha de cumpleaño
            String bdate = cursor.getString(cursor.getColumnIndex(Client._BIRTH));
            client.setBirthDate(DateUtils.convertToDate(bdate, DateUtils.YYYY_MM_DD));

            //Fecha de entrada
            String edate = cursor.getString(cursor.getColumnIndex(Client._ENTERED));
            client.setEnteredDate(DateUtils.convertToDate(edate, DateUtils.YYYY_MM_DD));

            //Fecha de la ultima modificacion del archivo
            String date = cursor.getString(cursor.getColumnIndex(Client._LASTMOD));
            Date lstMod = DateUtils.convertToDate(date, DateUtils.YYYY_MM_DD_HH_mm_ss);
            client.setLastModification(lstMod);

            //Foto del archivo
            File photo = new File(cursor.getString(cursor.getColumnIndex(Client._PHOTO)));
            client.setProfilePhoto(Uri.fromFile(photo));

            return client;
        }

        return null;
    }

    @Override
    public boolean update(Client item) {
        ContentValues cv = new ContentValues();

        cv.put(Client._NAME, item.getName());
        cv.put(Client._IDCARD, item.getIdentityCard());
        cv.put(Client._PHOTO, item.getProfilePhoto().getPath());
        cv.put(Client._CR_LIMIT, item.getCreditLimit());
        cv.put(Client._EMAIL, item.getEmail());
        cv.put(Client._BIRTH, DateUtils.formatDate(item.getBirthDate(), DateUtils.YYYY_MM_DD));
        cv.put(Client._ENTERED, DateUtils.formatDate(item.getEnteredDate(), DateUtils.YYYY_MM_DD));
        cv.put(Client._LAT, item.getLatlng().x);
        cv.put(Client._LNG, item.getLatlng().y);
        cv.put(Client._ADDRESS, item.getAddress());

        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.update(Client.TABLE_NAME, cv, Client._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insert(Client item) {
        ContentValues cv = new ContentValues();

        cv.put(Client._ID,       item.getId());
        cv.put(Client._NAME,     item.getName());
        cv.put(Client._IDCARD,   item.getIdentityCard());
        cv.put(Client._PHOTO,    item.getProfilePhoto().getPath());
        cv.put(Client._CR_LIMIT, item.getCreditLimit());
        cv.put(Client._EMAIL,    item.getEmail());
        cv.put(Client._BIRTH,    DateUtils.formatDate(item.getBirthDate(), DateUtils.YYYY_MM_DD));
        cv.put(Client._ENTERED,  DateUtils.formatDate(item.getEnteredDate(), DateUtils.YYYY_MM_DD));
        cv.put(Client._LAT,      item.getLatlng().x);
        cv.put(Client._LNG,      item.getLatlng().y);
        cv.put(Client._ADDRESS, item.getAddress());

        SQLiteDatabase database = getSqLiteDatabase();

        long result = -1;

        try {
            result = database.insertOrThrow(Client.TABLE_NAME, null, cv);
        } catch (SQLException e) {
            Log.d("SqlitePrueba", "ErrorClient: " + e.getMessage());
        }

        return result != -1;
    }

    @Override
    public boolean delete(Client item) {
        SQLiteDatabase database = getSqLiteDatabase();

        int result = database.delete(Client.TABLE_NAME, Client._ID.concat("=?"), new String[]{item.getId()});

        return result == 1;
    }

    @Override
    public boolean insertAll(List<Client> items) {

        for (Client i : items) {
            if (!insert(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean deleteAll(List<Client> items) {
        SQLiteDatabase database = getSqLiteDatabase();

        if (items == null) {
            return database.delete(Client.TABLE_NAME, "1", null) > 0;
        } else {
            for (Client i : items) {
                if (!delete(i)) {
                    return false;
                }
            }

            return true;
        }
    }
}
