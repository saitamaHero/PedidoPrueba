package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Company;
import Utils.DateUtils;

public class CompanyController extends Controller<Company> {

    public CompanyController(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase);
    }

    public static Company getCompany(SQLiteDatabase database) {
        Cursor cursor = database.query(Company.TABLE_NAME, null, null, null,null, null,null);
        Company company = new Company();

        cursor.moveToNext();

        company.setId("");
        company.setName(        cursor.getString(cursor.getColumnIndex(Company._COMPANY_NAME)));
        company.setAddress(     cursor.getString(cursor.getColumnIndex(Company._COMPANY_ADDRESS)));
        company.setContactInfo( cursor.getString(cursor.getColumnIndex(Company._COMPANY_INFO)));

        cursor.close();

        return company;
    }

    @Override
    public List<Company> getAll() {
        return null;
    }

    @Override
    public Company getById(Object id) {
        return null;
    }

    @Override
    public boolean update(Company item) {
        return false;
    }

    @Override
    public boolean insert(Company item) {
        getSqLiteDatabase().delete(Company.TABLE_NAME, "1", null);
        long result = getSqLiteDatabase().insert(Company.TABLE_NAME, null, getContentValues(item));
        return result > 0;
    }

    @Override
    public boolean delete(Company item) {
        return false;
    }

    @Override
    public boolean insertAll(List<Company> item) {
        return false;
    }

    @Override
    public boolean deleteAll(List<Company> item) {
        return false;
    }

    @Override
    public ContentValues getContentValues(Company company) {
        ContentValues cv = new ContentValues();
        cv.put(Company._COMPANY_NAME,    company.getName());
        cv.put(Company._COMPANY_ADDRESS, company.getAddress());
        cv.put(Company._COMPANY_INFO,    company.getContactInfo());

        return cv;
    }
}