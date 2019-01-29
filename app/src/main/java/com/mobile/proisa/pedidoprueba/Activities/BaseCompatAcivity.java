package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class BaseCompatAcivity extends AppCompatActivity {
    public static final String EXTRA_ITEMS      = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_ITEMS";
    public static final String EXTRA_CLIENT     = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_CLIENT";
    public static final String EXTRA_INIT_VISIT = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_INIT_VISIT";
    public static final String EXTRA_ITEM_DATA  = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_ITEM_DATA";
    public static final String EXTRA_INVOICE    = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_INVOICE";
    public static final String EXTRA_USER       = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_USER";

    //public static final String {NAME} = "com.mobile.proisa.pedidoprueba.Activities.{NAME}";

    public static final int REQUEST_CODE_CAMERA  = 1000;
    public static final int REQUEST_CODE_STORAGE = 1001;
    public static final int REQUEST_CODE_GALLERY = 1002;
    //public static final int REQUEST_CODE_


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBindUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        onBindUI();
    }

    /**
     * Puedes usar este método para enlazar los componentes de tú interfaz gráfica.
     * Este método es llamando en el onCreate de la actividad
     */
    protected void onBindUI() {
    }


    private int setCurrentFragment(int containerId, Fragment fragment, boolean statusLosses){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerId, fragment);

        if(statusLosses)
            return fragmentTransaction.commitAllowingStateLoss();
        else
            return fragmentTransaction.commit();
    }

    public int setCurrentFragment(int containerId, Fragment fragment){
        return setCurrentFragment(containerId, fragment, false);
    }

    public int setCurrentFragmentWithStateLoss(int containerId, Fragment fragment){
        return setCurrentFragment(containerId, fragment, true);
    }

  
}
