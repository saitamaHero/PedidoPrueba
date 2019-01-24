package com.mobile.proisa.pedidoprueba.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import Models.Diary;

public class VisitaActivaService extends Service {
    private static final String TAG = "VisitaActivaService";
    public  static final String EXTRA_VISIT            = "com.mobile.proisa.pedidoprueba.Services.EXTRA_VISIT";
    public  static final String ACTION_VISIT_START     = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_START";
    public  static final String ACTION_VISIT_RUNNING   = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_RUNNING";
    public  static final String ACTION_VISIT_FINISH    = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_FINISH";

    private Diary visit;

    private ServiceHandler mServiceHandler;
    private Looper looper;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: El servicio se ha creado");

        /*HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();


        looper = thread.getLooper();

        mServiceHandler = new ServiceHandler(looper);*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date currenTime = Calendar.getInstance().getTime();
        visit = intent.getParcelableExtra(EXTRA_VISIT);
        visit.setStartTime(currenTime);

        sendOrderedBroadcast(new Intent().setAction(ACTION_VISIT_START), null);


        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

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

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //getApplicationContext().sendBroadcast(new Intent().setAction(ACTION_VISIT_RUNNING).putExtra(EXTRA_VISIT, visit));

            //while (true){
                sendBroadcast(new Intent().setAction(ACTION_VISIT_RUNNING).putExtra(EXTRA_VISIT, visit));

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //break;
                }
            //}

            stopSelf(msg.arg1);
        }
    }

}
