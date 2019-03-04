package com.mobile.proisa.pedidoprueba.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
    public  static final String ACTION_IS_VISIT_RUNNING   = "com.mobile.proisa.pedidoprueba.Services.ACTION_IS_VISIT_RUNNING";
    public  static final String ACTION_VISIT_FINISH    = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_FINISH";

    private Diary visit;

    private ServiceHandler mServiceHandler;
    private Looper looper;
    private HandlerThread thread;
    private BroadcastReceiver broadcastReceiver;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: El servicio se ha creado");

        thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();


        looper = thread.getLooper();

        mServiceHandler = new ServiceHandler(looper);


         broadcastReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mServiceHandler.sendEmptyMessage(ServiceHandler.VISIT_RUNNING);
            }
        };

         IntentFilter filter = new IntentFilter();
         filter.addAction(ACTION_IS_VISIT_RUNNING);
         registerReceiver(broadcastReceiver, filter);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date currenTime = Calendar.getInstance().getTime();
        visit = intent.getParcelableExtra(EXTRA_VISIT);
        visit.setStartTime(currenTime);

        mServiceHandler.sendEmptyMessage(ServiceHandler.VISIT_START);



        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        Date currenTime = Calendar.getInstance().getTime();
        visit.setEndTime(currenTime);

        mServiceHandler.sendEmptyMessage(ServiceHandler.VISIT_FINISH);
        //sendBroadcast(new Intent().setAction(ACTION_VISIT_FINISH).putExtra(EXTRA_VISIT, visit));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            thread.quitSafely();
        }else{
            thread.quit();
        }

        unregisterReceiver(broadcastReceiver);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public static final int VISIT_START = 1;
        public static final int VISIT_RUNNING = 2;
        public static final int VISIT_FINISH = 3;


        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //getApplicationContext().sendBroadcast(new Intent().setAction(ACTION_VISIT_RUNNING).putExtra(EXTRA_VISIT, visit));


            switch (msg.what){
                case VISIT_START:
                    sendBroadcast(new Intent().setAction(ACTION_VISIT_START));
                    break;

                case VISIT_FINISH:
                    sendBroadcast(new Intent().setAction(ACTION_VISIT_FINISH).putExtra(EXTRA_VISIT, visit));
                    break;

                case VISIT_RUNNING:
                    sendBroadcast(new Intent().setAction(ACTION_VISIT_RUNNING).putExtra(EXTRA_VISIT, visit));
                    break;
            }





            //stopSelf(msg.arg1);
        }
    }

}
