package com.mobile.proisa.pedidoprueba.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import BaseDeDatos.CategoryUpdater;
import BaseDeDatos.ClientUpdater;
import BaseDeDatos.CompanyUpdater;
import BaseDeDatos.DiaryUpdater;
import BaseDeDatos.InvoiceUpdater;
import BaseDeDatos.ItemUpdater;
import BaseDeDatos.NCFUpdater;
import BaseDeDatos.SqlConnection;
import BaseDeDatos.SqlUpdater;
import BaseDeDatos.UnitUpdater;
import BaseDeDatos.UpdaterManager;
import BaseDeDatos.ZoneUpdater;
import Sqlite.CategoryController;
import Sqlite.ClientController;
import Sqlite.CompanyController;
import Sqlite.DiaryController;
import Sqlite.InvoiceController;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;
import Sqlite.NCFController;
import Sqlite.UnitController;
import Sqlite.ZoneController;

public class SyncAllService extends IntentService implements SqlUpdater.OnDataUpdateListener, SqlUpdater.OnErrorListener{
    public static final String EXTRA_SYNC_START = "com.mobile.proisa.pedidoprueba.Services.SYNC_START";
    public static final String EXTRA_SYNC_FINISH = "com.mobile.proisa.pedidoprueba.Services.SYNC_FINISH";

    public SyncAllService() {
        super("SyncAllService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendBroadcast(new Intent(EXTRA_SYNC_START));
        Context context =  getApplicationContext();

        SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());
        SQLiteDatabase sqLiteDatabase = MySqliteOpenHelper.getInstance(context).getWritableDatabase();

        UpdaterManager updaterManager = new UpdaterManager(connection);
        updaterManager.setErrorListener(this);
        updaterManager.setUpdateListener(this);

        //Datos de la empresa
        updaterManager.addUpdater(new CompanyUpdater    (context,null, new CompanyController(sqLiteDatabase)));
        //Articulos
        updaterManager.addUpdater(new ItemUpdater       (context,null, new ItemController(sqLiteDatabase)));
        //Categoria de los articulos
        updaterManager.addUpdater(new CategoryUpdater   (context,null, new CategoryController(sqLiteDatabase)));
        //Unidad de los articulos
        updaterManager.addUpdater(new UnitUpdater       (context,null, new UnitController(sqLiteDatabase)));
        //Cliente
        updaterManager.addUpdater(new ClientUpdater     (context,null, new ClientController(sqLiteDatabase)));
        //Visitas
        updaterManager.addUpdater(new DiaryUpdater      (context,null, new DiaryController(sqLiteDatabase)));
        //Zonas
        updaterManager.addUpdater(new ZoneUpdater       (context,null, new ZoneController(sqLiteDatabase)));
        //NCF
        updaterManager.addUpdater(new NCFUpdater        (context,null, new NCFController(sqLiteDatabase)));
        //Facturas
        updaterManager.addUpdater(new InvoiceUpdater    (context,null, new InvoiceController(sqLiteDatabase)));

        // Decirle al UpdaterManager que comience el proceso de actualizacion
        updaterManager.execute();


    }

    @Override
    public void onDataUpdate(Object data, int action) {
        
    }

    @Override
    public void onDataUpdated(Object data) {
        Log.d("SyncAllService","Dato Actualizado:"+data.toString());
    }

    @Override
    public void onError(int error) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(EXTRA_SYNC_FINISH));
    }
}
