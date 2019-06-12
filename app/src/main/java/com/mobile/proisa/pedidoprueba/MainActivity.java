package com.mobile.proisa.pedidoprueba;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Activities.SeeAcitivitiesActivity;
import com.mobile.proisa.pedidoprueba.Activities.VentaActivity;
import com.mobile.proisa.pedidoprueba.Adapters.MainPagerAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.VendorProfileFragment;
import com.mobile.proisa.pedidoprueba.Services.SyncAllService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import Models.Category;
import Models.Constantes;
import Models.Diary;
import Models.Invoice;
import Models.Item;
import Models.Unit;
import Models.User;
import Models.Vendor;
import Sqlite.Controller;
import Utils.DateUtils;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClientsFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 100;
    private static final int PERMISO_MEMORIA_REQUEST = 321;

    private ViewPager viewPager;
    private BottomNavigationView mBottomNavigationView;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUpNavigationView();
        setUpViewPager(2);

        checkPreferences();


    }

    private String getPhoneName() {
        return String.format("%s %s", Build.BRAND.toUpperCase(), Build.MODEL.toUpperCase());
    }

    private void checkPreferences() {
        if (!areUserThere()) {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
        } else {
            checkPermissionStorage();
        }
    }

    private void setUpNavigationView() {
        mBottomNavigationView = findViewById(R.id.nav_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        Log.d(TAG, "setUpNavigationView: true");
    }

    private void setUpViewPager(int positionForStart) {
        viewPager = findViewById(R.id.view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), getFragmentsForViewPager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(positionForStart);

        Log.d(TAG, "setUpViewPager: true");
    }

    private List<Fragment> getFragmentsForViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ItemListFragment.newInstance());
        fragments.add(ClientsFragment.newInstance());
        fragments.add(new VendorProfileFragment());

        return fragments;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem menuItem = mBottomNavigationView.getMenu().getItem(position);
        mBottomNavigationView.setSelectedItemId(menuItem.getItemId());

        setTitle(menuItem.getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    User mUser = data.getExtras().getParcelable("user");
                    saveUserInPreferences(mUser);
                    checkPermissionStorage();

                    Intent serviceSyncAll = new Intent(this, SyncAllService.class);
                    startService(serviceSyncAll);


                } else {
                    finish();
                }
                break;
        }

    }

    private boolean areUserThere() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);

        return preferences.contains(Constantes.USER);
    }

    private User getUserFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        User user = new User();
        user.setUser(preferences.getString(Constantes.USER, ""));

        Vendor vendor = new Vendor();
        vendor.setId(preferences.getString(Constantes.VENDOR_CODE, ""));
        vendor.setName(preferences.getString(Constantes.VENDOR_NAME, ""));
        user.setVendor(vendor);

        user.setLogged(true);

        return user;
    }

    private void saveUserInPreferences(User user) {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();
        editor.putString(Constantes.USER, user.getUser());

        Vendor vendor = user.getVendor();

        if (vendor != null) {
            editor.putString(Constantes.VENDOR_CODE, vendor.getId());
            editor.putString(Constantes.VENDOR_NAME, vendor.getName());
            editor.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int position = item.getOrder() - 1;
        viewPager.setCurrentItem(position, true);

        return true;
    }

    @Override
    public void requestChangePage() {
        viewPager.setCurrentItem(3);
    }

    private void checkPermissionStorage() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_MEMORIA_REQUEST);

            Log.d(TAG, "checkPermissionStorage: Solicitando permiso de memoria");
        } else {
            Log.d(TAG, "checkPermissionStorage: El permiso de memoria ya esta concedido");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISO_MEMORIA_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria concedido");
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria denegado");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (SyncAllService.EXTRA_SYNC_START.equals(action)) {
                    Toast.makeText(getApplicationContext(), R.string.sync, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.got_data, Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncAllService.EXTRA_SYNC_START);
        intentFilter.addAction(SyncAllService.EXTRA_SYNC_FINISH);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    private void toogle() {
        View v = findViewById(R.id.progressBar);
        int visivility = v.getVisibility();

        v.setVisibility(visivility == View.GONE ? View.VISIBLE : View.GONE);
    }



}