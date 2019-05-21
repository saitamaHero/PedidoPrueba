package com.mobile.proisa.pedidoprueba.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import BaseDeDatos.SqlConnection;
import BaseDeDatos.SqlUpdater;

public abstract class MyIntentService<T extends Parcelable> extends IntentService implements SqlUpdater.OnDataUpdateListener<T>, SqlUpdater.OnErrorListener {
    public static final String EXTRA_DATA = "com.mobile.proisa.pedidoprueba.Services.PruebaServicio.EXTRA_DATA";
    public static final String EXTRA_SYNC_ALL = "com.mobile.proisa.pedidoprueba.Services.PruebaServicio.EXTRA_SYNC_ALL";
    private SqlUpdater<T> mSqlUpdater;
    private SqlConnection mSqlConnection;

    private boolean mSyncAll;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Establecer conexión con el servidor por defecto
        mSqlConnection = new SqlConnection(SqlConnection.getDefaultServer());
        mSqlUpdater = getUpdater();
    }

    public abstract SqlUpdater<T> getUpdater();

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(!intent.getExtras().containsKey(EXTRA_DATA)){
            return; // Premature exit if not data is not sended
        }

         mSyncAll = intent.getBooleanExtra(EXTRA_SYNC_ALL, false);

        try {
            T data = intent.getExtras().getParcelable(EXTRA_DATA);

            addDataToSync(data);
        }catch (ClassCastException ex){
            ex.printStackTrace();
        }

        runSync();

    }

    private void addDataToSync(T data) {
        if(updaterIsNotNull()){
            mSqlUpdater.addData(data);
        }
    }

    public SqlConnection getSqlConnection() {
        return mSqlConnection;
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onDataUpdate(T data, int action) {

    }

    @Override
    public void onDataUpdated(T data) {

    }

    protected void runSync(){
        if(updaterIsNotNull()){
            mSqlUpdater.apply();

            if(mSyncAll){
                mSqlUpdater.retriveData();
            }
        }
    }

    public boolean updaterIsNotNull(){
        return mSqlUpdater != null;
    }
}