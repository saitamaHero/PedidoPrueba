package com.mobile.proisa.pedidoprueba;

import android.app.Activity;
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

import Models.Category;
import Models.Constantes;

import com.mobile.proisa.pedidoprueba.Dialogs.ProgressDialog;
import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.VendorProfileFragment;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import Models.Diary;
import Models.Unit;
import Models.User;
import Models.Vendor;
import Sqlite.CategoryController;
import Sqlite.MySqliteOpenHelper;
import Sqlite.UnitController;
import Utils.DateUtils;
import Utils.FileUtils;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private BottomNavigationView navigationView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_bottom);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = item.getOrder() - 1;
                viewPager.setCurrentItem(position, true);

                return true;
            }
        });


        setUpViewPager(3);


        checkPreferences();

        //setTitle(FileUtils.createFileRoute(Constantes.MAIN_DIR, Constantes.ITEMS_PHOTOS).getPath());

/*
        UnitController controller = new UnitController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());
        List<Unit> units = controller.getAll();

        for(Unit u : units){
            Log.d("units",u.toString());
        }
*/

    }

    private void checkPreferences() {
        if(!areUserThere()){
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class),100);
        }
    }

    private void setUpViewPager(int positionForStart){
        viewPager = findViewById(R.id.view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), getFragmentsForViewPager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(positionForStart);
    }

   private List<Fragment> getFragmentsForViewPager() {
       List<Fragment> fragments = new ArrayList<>();
       fragments.add(ItemListFragment.newInstance());
       fragments.add(ClientsFragment.newInstance());
       fragments.add(ActividadFragment.newInstance(getActividadesDePrueba()));
       fragments.add(new VendorProfileFragment());

       return fragments;
   }


   private Date getRandomDate(Date dateBase){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtils.deleteTime(dateBase));

        int daysMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Random random = new Random();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + random.nextInt(daysMax));

        return calendar.getTime();
   }


   public static List<Actividad> getActividadesDePrueba(){
        List<Actividad> actividads = new ArrayList<>();

       Random random = new Random();

       int visitas = 1 + random.nextInt(50);
       //visitas = visitas == 0 ? 2 : visitas;
       int visitasCompletas = random.nextInt(visitas);
       int visitanIncompletas = visitas - visitasCompletas;

        actividads.add(new Actividad("RD$ "+ NumberUtils.formatNumber(random.nextDouble() * 100.00 + 1000.00, NumberUtils.FORMAT_NUMER_DOUBLE),
                "Venta Total", "Todo lo vendido en el día"));


        if(visitasCompletas > 0){
            actividads.add(new Actividad(NumberUtils.formatNumber(visitasCompletas, NumberUtils.FORMAT_NUMER_INTEGER),
                    "Visitas Completas", String.format("%d%% de las visitas",getPercent(visitasCompletas, visitas))
            ));
        }

        if(visitanIncompletas > 0){
            actividads.add(new Actividad(NumberUtils.formatNumber(visitanIncompletas, NumberUtils.FORMAT_NUMER_INTEGER),
                    "Visitas Incompletas", String.format("%d%% de las visitas",getPercent(visitanIncompletas, visitas)), false)
            );
        }

        actividads.add(new Actividad(NumberUtils.formatNumber(random.nextInt(50),
                NumberUtils.FORMAT_NUMER_INTEGER), "Cobros Realizados", ""));
        actividads.add(new Actividad(NumberUtils.formatNumber(random.nextInt(100),
                NumberUtils.FORMAT_NUMER_INTEGER), "Articulos Devueltos",
                "Articulos devueltos por los clientes :'(", false));


        return actividads;
   }

   private static int getPercent(float min, float max){
        float p = ( min / max) * 100.00f;
        return Math.round(p);
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

        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                User mUser = data.getExtras().getParcelable("user");
                guardarUsuario(mUser);
            }else{
                finish();
            }
        }
    }

    private boolean areUserThere(){
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA,MODE_PRIVATE);

        return preferences.contains(Constantes.USER);
    }

    private User getUserFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA,MODE_PRIVATE);
        User user = new User();
        user.setUser(preferences.getString(Constantes.USER,""));

        Vendor vendor = new Vendor();
        vendor.setId(preferences.getString(Constantes.VENDOR_CODE,""));
        vendor.setName(preferences.getString(Constantes.VENDOR_NAME, ""));
        user.setVendor(vendor);

        user.setLogged(true);

        return user;
    }

    private void guardarUsuario(User user) {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();

        editor.putString(Constantes.USER,user.getUser());

        Vendor vendor = user.getVendor();

        if(vendor != null){
            editor.putString(Constantes.VENDOR_CODE,vendor.getId());
            editor.putString(Constantes.VENDOR_NAME,vendor.getName());
            editor.commit();
        }
    }

    private void deletePreferences() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();
        editor.clear().commit();

        /*editor.remove(Constantes.USER);
        editor.remove(Constantes.VENDOR_CODE);
        editor.remove(Constantes.VENDOR_NAME);
        editor.commit();*/

    }


}