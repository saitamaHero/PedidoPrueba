package com.mobile.proisa.pedidoprueba.Services;

import android.app.IntentService;
import android.content.Intent;
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
        SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());
        MySqliteOpenHelper mySqliteOpenHelper = MySqliteOpenHelper.getInstance(getApplicationContext());



        CompanyUpdater companyUpdater = new CompanyUpdater(getApplicationContext(), connection, new CompanyController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase()));
        companyUpdater.setOnDataUpdateListener(this);
        companyUpdater.setOnErrorListener(this);
        companyUpdater.retriveData();


        /**
         * Articulos
         */
        ItemController itemController = new ItemController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());
        ItemUpdater itemUpdater = new ItemUpdater(getApplicationContext(), connection, itemController);
        itemUpdater.setOnDataUpdateListener(this);
        itemUpdater.setOnErrorListener(this);
        itemUpdater.retriveData();

        /**
         * Categoría
         */
        CategoryController categoryController = new CategoryController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());
        CategoryUpdater categoryUpdater = new CategoryUpdater(getApplicationContext(), connection, categoryController);
        categoryUpdater.setOnDataUpdateListener(this);
        categoryUpdater.setOnErrorListener(this);
        categoryUpdater.retriveData();

        /**
         * Unidad
         */
        UnitController unitController = new UnitController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());
        UnitUpdater unitUpdater = new UnitUpdater(getApplicationContext(), connection, unitController);
        unitUpdater.setOnDataUpdateListener(this);
        unitUpdater.setOnErrorListener(this);
        unitUpdater.retriveData();

        ClientController controller = new ClientController(mySqliteOpenHelper.getWritableDatabase());

        //Si no hay elementos en la base de datos no se analizara practicamente nada.
        ClientUpdater clientUpdater = new ClientUpdater(getApplicationContext(), connection, controller);
        clientUpdater.setOnDataUpdateListener(this);
        clientUpdater.setOnErrorListener(this);
        clientUpdater.addData(controller.getAll());
        clientUpdater.apply();

        //Llamar este metodo para que inserte los datos que hacen falta del servidor
        clientUpdater.retriveData();
        
        
        /*Visitas*/
        DiaryController diaryController = new DiaryController(mySqliteOpenHelper.getWritableDatabase());
        //Updater de las visitas
        DiaryUpdater diaryUpdater = new DiaryUpdater(getApplicationContext(), connection, diaryController);
        diaryUpdater.setOnDataUpdateListener(this);
        diaryUpdater.setOnErrorListener(this);
        diaryUpdater.addData(diaryController.getAll());
        diaryUpdater.apply();

        //Obtener visitas que estan en el servidor
        diaryUpdater.retriveData();
    
        
        /*Zonas*/
        ZoneController zoneController = new ZoneController(mySqliteOpenHelper.getWritableDatabase());
        //Updater de las visitas
        ZoneUpdater zoneUpdater = new ZoneUpdater(getApplicationContext(), connection, zoneController);
        zoneUpdater.setOnDataUpdateListener(this);
        zoneUpdater.setOnErrorListener(this);
        zoneUpdater.retriveData();

        /*NCF*/
        NCFController ncfController = new NCFController(mySqliteOpenHelper.getWritableDatabase());
        NCFUpdater ncfUpdater = new NCFUpdater(getApplicationContext(), connection, ncfController);
        ncfUpdater.setOnDataUpdateListener(this);
        ncfUpdater.setOnErrorListener(this);
        ncfUpdater.retriveData();
    
        /*Facturas*/
        InvoiceController invoiceController = new InvoiceController(mySqliteOpenHelper.getWritableDatabase());
        InvoiceUpdater invoiceUpdater = new InvoiceUpdater(getApplicationContext(), connection, invoiceController);
        invoiceUpdater.setOnDataUpdateListener(this);
        invoiceUpdater.setOnErrorListener(this);
        invoiceUpdater.apply();

        sendBroadcast(new Intent(EXTRA_SYNC_FINISH));
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
