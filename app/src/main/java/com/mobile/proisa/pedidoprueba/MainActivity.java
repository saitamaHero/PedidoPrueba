package com.mobile.proisa.pedidoprueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Adapters.MainPagerAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;

import Models.Constantes;

import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.VendorProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Models.User;
import Models.Vendor;
import Utils.NumberUtils;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 100;
    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigationView();
        setUpViewPager(1);

        checkPreferences();
    }

    private void checkPreferences() {
        if (!areUserThere()) {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
        }
    }

    private void setUpNavigationView() {
        navigationView = findViewById(R.id.nav_bottom);
        navigationView.setOnNavigationItemSelectedListener(this);

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

        Random random = new Random();

        int visitas = 1 + random.nextInt(50);
        //visitas = visitas == 0 ? 2 : visitas;
        int visitasCompletas = random.nextInt(visitas);
        int visitanIncompletas = visitas - visitasCompletas;

        actividads.add(new Actividad("RD$ " + NumberUtils.formatNumber(random.nextDouble() * 100.00 + 1000.00, NumberUtils.FORMAT_NUMER_DOUBLE), "Venta Total", "Todo lo vendido en el día"));


        if (visitasCompletas > 0) {
            actividads.add(new Actividad(NumberUtils.formatNumber(visitasCompletas, NumberUtils.FORMAT_NUMER_INTEGER), "Visitas Completas", String.format("%d%% de las visitas", NumberUtils.getPercent(visitasCompletas, visitas))));
        }

        if (visitanIncompletas > 0) {
            actividads.add(new Actividad(NumberUtils.formatNumber(visitanIncompletas, NumberUtils.FORMAT_NUMER_INTEGER), "Visitas Incompletas", String.format("%d%% de las visitas", NumberUtils.getPercent(visitanIncompletas, visitas)), false));
        }

        actividads.add(new Actividad(NumberUtils.formatNumber(random.nextInt(50), NumberUtils.FORMAT_NUMER_INTEGER), "Cobros Realizados", ""));
        actividads.add(new Actividad(NumberUtils.formatNumber(random.nextInt(100), NumberUtils.FORMAT_NUMER_INTEGER), "Articulos Devueltos", "Articulos devueltos por los clientes :'(", false));



        return actividads;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem menuItem = navigationView.getMenu().getItem(position);
        navigationView.setSelectedItemId(menuItem.getItemId());

        setTitle(menuItem.getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                User mUser = data.getExtras().getParcelable("user");
                guardarUsuario(mUser);
            } else {
                finish();
            }
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

    private void deletePreferences() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();
        editor.clear().commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int position = item.getOrder() - 1;
        viewPager.setCurrentItem(position, true);
        return true;
    }


}