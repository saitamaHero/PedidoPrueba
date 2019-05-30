package com.mobile.proisa.pedidoprueba.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.R;

public class SeeAcitivitiesActivity extends BaseCompatAcivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_acitivities);
    }

    @Override
    protected void onBindUI() {
        super.onBindUI();

        setCurrentFragment(R.id.container, ActividadFragment.newInstance());
    }
}
