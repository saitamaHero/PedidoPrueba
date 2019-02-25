package com.mobile.proisa.pedidoprueba.Services;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.mobile.proisa.pedidoprueba.MainActivity;
import com.mobile.proisa.pedidoprueba.R;

public class PruebaServicio extends Service {
    private static final String TAG = "PruebaServicio";

    private ServiceHandler mHandlerService;
    private int mStartId;

    private final class ServiceHandler extends Handler{
        public static final int LOOP = 0;
        public static final int FINISH = 1;


        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case LOOP:
                    sendBroadcast(new Intent("hola"));
                    break;

                case FINISH:
                    stopSelf(msg.arg1);

                    break;
            }

        }
    };

    public PruebaServicio() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: Se creo el Servicio");



        HandlerThread handlerThread = new HandlerThread("PruebaServicioThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();


        mHandlerService = new ServiceHandler(handlerThread.getLooper());

        /*
        *     Message msg = mHandlerService.obtainMessage();
        msg.arg1 = startId;
        mHandlerService.sendMessage(msg);
        * */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: esto fue llamado");

        mStartId = startId;

        return START_NOT_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: Servicio Destruido");
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}
