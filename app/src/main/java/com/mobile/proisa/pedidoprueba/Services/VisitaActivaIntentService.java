package com.mobile.proisa.pedidoprueba.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import Models.Diary;

public class VisitaActivaIntentService extends IntentService {
    private static final String TAG = "VisitaActivaService";
    public  static final String EXTRA_VISIT            = "com.mobile.proisa.pedidoprueba.Services.EXTRA_VISIT";
    public  static final String ACTION_VISIT_START     = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_START";
    public  static final String ACTION_VISIT_RUNNING   = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_RUNNING";
    public  static final String ACTION_VISIT_FINISH    = "com.mobile.proisa.pedidoprueba.Services.ACTION_VISIT_FINISH";

    private Diary visit;

    public VisitaActivaIntentService() {
        super("VisitaActivaIntentService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Date currenTime = Calendar.getInstance().getTime();
        visit = intent.getParcelableExtra(EXTRA_VISIT);
        visit.setStartTime(currenTime);

        sendOrderedBroadcast(new Intent().setAction(ACTION_VISIT_START), null);



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

}
