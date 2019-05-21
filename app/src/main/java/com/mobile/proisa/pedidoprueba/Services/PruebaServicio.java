package com.mobile.proisa.pedidoprueba.Services;


import android.content.Intent;
import android.support.annotation.Nullable;

import BaseDeDatos.DiaryUpdater;
import BaseDeDatos.SqlUpdater;
import Models.Diary;
import Sqlite.DiaryController;
import Sqlite.MySqliteOpenHelper;

public class PruebaServicio extends MyIntentService<Diary> {
    public static final String EXTRA_STATUS = "com.mobile.proisa.pedidoprueba.Services.PruebaServicio.EXTRA_DATA_STATUS";
    public static final String ACTION_DATA_STATUS = "com.mobile.proisa.pedidoprueba.Services.PruebaServicio.ACTION_DATA_PROCESS";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PruebaServicio() {
        super("PruebaServicio");
    }


    @Override
    public SqlUpdater<Diary> getUpdater() {
        return new DiaryUpdater(getApplicationContext(), getSqlConnection(), new DiaryController(MySqliteOpenHelper.getInstance(this).getWritableDatabase()));
    }


    @Override
    public void onError(int error) {
        //Ocurrió un error
    }


    @Override
    public void onDataUpdate(Diary data, int action) {
        //La data entró en proceso de actualizacion
    }

    @Override
    public void onDataUpdated(Diary data) {
        //La data ha sido actualizada correctamente
    }


}

