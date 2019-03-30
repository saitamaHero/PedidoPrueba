package com.mobile.proisa.pedidoprueba;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Adapters.MainPagerAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.VendorProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import BaseDeDatos.SqlUpdater;
import Models.Constantes;
import Models.Invoice;
import Models.User;
import Models.Vendor;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener,
        ClientsFragment.OnFragmentInteractionListener, BluetoothListFragment.OnBluetoothSelectedListener
{
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 100;
    private static final int PERMISO_MEMORIA_REQUEST = 321;


    private ViewPager viewPager;
    private BottomNavigationView mBottomNavigationView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigationView();
        setUpViewPager(1);

        checkPreferences();

        //Log.d("phoneModel", String.format("%s,   %s,   %s,   %s",Build.MODEL, Build.BRAND, Build.BOARD, Build.ID));
        //MySqliteOpenHelper.generateFile(MySqliteOpenHelper.getInstance(this).getReadableDatabase());


    }


    private String getPhoneName(){
        return String.format("%s %s", Build.BRAND.toUpperCase(), Build.MODEL.toUpperCase());
    }

    private void checkPreferences() {
        if (!areUserThere()) {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
        }else{
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
        fragments.add(ActividadFragment.newInstance(getActividadesDePrueba()));
        fragments.add(new VendorProfileFragment());

        return fragments;
    }

    public static List<Actividad> getActividadesDePrueba() {
        List<Actividad> actividads = new ArrayList<>();

        return actividads;
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


        switch (requestCode){
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    User mUser = data.getExtras().getParcelable("user");
                    guardarUsuario(mUser);
                    checkPermissionStorage();

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

    private void guardarUsuario(User user) {
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

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionStorage(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISO_MEMORIA_REQUEST);

            Log.d(TAG, "checkPermissionStorage: Solicitando permiso de memoria");
        }else{
            Log.d(TAG, "checkPermissionStorage: El permiso de memoria ya esta concedido");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISO_MEMORIA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria concedido");
                }else{
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria denegado");
                }
                break;
        }
    }


}