package com.mobile.proisa.pedidoprueba.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobile.proisa.pedidoprueba.Services.VisitaActivaService;

import Models.Diary;

public class DiaryBroadcastReceiver extends BroadcastReceiver {

    private OnDiaryStateListener onDiaryState;

    public DiaryBroadcastReceiver(OnDiaryStateListener onDiaryState) {
        this.onDiaryState = onDiaryState;

        if(this.onDiaryState == null){
            throw new NullPointerException("Must be implement "+ OnDiaryStateListener.class.getSimpleName());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null? "" : intent.getAction();

        if(intent == null || !intent.getExtras().containsKey(VisitaActivaService.EXTRA_VISIT)){
            return;
        }

        Diary diary = intent.getExtras().getParcelable(VisitaActivaService.EXTRA_VISIT);

        if(VisitaActivaService.ACTION_VISIT_START.equals(action)) {
            onDiaryState.onVisitStatusChanged(OnDiaryStateListener.VISIT_START, diary);
        }else if(VisitaActivaService.ACTION_VISIT_RUNNING.equals(action)){
            onDiaryState.onVisitStatusChanged(OnDiaryStateListener.VISIT_RUNNING, diary);
        }else if(VisitaActivaService.ACTION_VISIT_FINISH.equals(action)){
            onDiaryState.onVisitStatusChanged(OnDiaryStateListener.VISIT_FINISH, diary);
        }
    }

    public interface OnDiaryStateListener {
        int VISIT_START  = 0;
        int VISIT_FINISH = 1;
        int VISIT_RUNNING = 2;

        void onVisitStatusChanged(int status, Diary diary);

    }
}
