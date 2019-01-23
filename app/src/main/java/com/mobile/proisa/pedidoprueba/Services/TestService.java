package com.mobile.proisa.pedidoprueba.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.Random;

public class TestService extends IntentService {
    public static final String ACTION_PROGRESS = "action_progress";
    public static final String ACTION_FINISH = "action_finish";

    Random random  = new Random();
    public TestService() {
        super("TestService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int count = intent.getIntExtra("count", 0);

        for(int index = 0; index < count; index++) {
            doSomething();
            sendBroadcast(new Intent().setAction(ACTION_PROGRESS).putExtra("progress", convertIndexToPercentage(index, count)));
        }

        sendBroadcast(new Intent().setAction(ACTION_FINISH));
    }

    private int convertIndexToPercentage(int idx, int top){
        float d = (idx + 1) / (top * 1.0f);

        idx = (int) (d * 100.0f);
        return idx;
    }

    private void doSomething() {
        if(random == null)
            random = new Random();

        try {
            int millis = (int) (random.nextFloat() * 1000);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
