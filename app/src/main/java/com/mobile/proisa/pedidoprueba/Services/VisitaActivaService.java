package com.mobile.proisa.pedidoprueba.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import Models.Diary;

public class VisitaActivaService extends Service {
    private static final String TAG = "VisitaActivaService";
    public  static final String EXTRA_VISIT = "com.mobile.proisa.pedidoprueba.Services.EXTRA_VISIT";
    public  static final String ACTION_VISIT_START = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_START";
    public  static final String ACTION_VISIT_RUNNING = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_RUNNING";
    public  static final String ACTION_VISIT_FINISH = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_FINISH";

    private Diary visit;

    private Thread mThread;
    private boolean canExit;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: El servicio se ha creado");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        canExit = false;

        visit = intent.getParcelableExtra(EXTRA_VISIT);

        Date currenTime = Calendar.getInstance().getTime();
        visit.setStartTime(currenTime);

        sendBroadcast(new Intent().setAction(ACTION_VISIT_START));


        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
               while (!canExit){
                   sendBroadcast(new Intent().setAction(ACTION_VISIT_RUNNING));

                   try {
                       Thread.sleep(1000L);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
            }
        });


        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        canExit = true;

        Date currenTime = Calendar.getInstance().getTime();
        visit.setEndTime(currenTime);

        sendBroadcast(
                new Intent()
                        .setAction(ACTION_VISIT_FINISH)
                        .putExtra(EXTRA_VISIT, visit)
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
