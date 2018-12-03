package com.mobile.proisa.pedidoprueba;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Activities.SelectorItemActivity;
import com.mobile.proisa.pedidoprueba.Adapters.MainPagerAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.TestFragment;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import Models.Client;
import Models.Diary;
import Models.Item;
import Utils.DateUtils;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    //private List<Item> products;
    //private ItemsAdapter adapter;
    //private RecyclerView recyclerView;

    ViewPager viewPager;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = DateUtils.convertToDate("29-11-2018", DateUtils.DD_MM_YYYY);
        Diary diary = new Diary(1,date, "Hola oo");

        Log.d("dateDiff", "days are: " + String.valueOf(diary.getDays(Calendar.getInstance().getTime())));
        Log.d("dateDiff", "horas are: " + String.valueOf(diary.getHours(Calendar.getInstance().getTime())));
        navigationView = findViewById(R.id.nav_bottom);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = item.getOrder() - 1;
                viewPager.setCurrentItem(position, true);
                /*switch (item.getItemId()){
                    case R.id.stock:
                        viewPager.setCurrentItem(0, true);
                        break;
                    case R.id.clients:
                        viewPager.setCurrentItem(1, true);
                        break;
                    case R.id.vendor_activity:
                        viewPager.setCurrentItem(2, true);
                        break;

                    case R.id.vendor_profile:
                        viewPager.setCurrentItem(3, true);
                        break;

                    default:
                        return false;

                }*/
                //Snackbar.make(navigationView, ""+item.getOrder()+" "+item.toString(), Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });

        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Toast.makeText(getApplicationContext(), "Reselected: "+item.toString().trim(), Toast.LENGTH_SHORT).show();

            }
        });

        setUpViewPager(1);

       // startActivityForResult(new Intent(getApplicationContext(), SelectorItemActivity.class),100);
        /*products = createListItem(3, 0);

        recyclerView = findViewById(R.id.recycler_view);

        //Create de adapter for items
        adapter = new ItemsAdapter(products, R.layout.item_card_view);
        adapter.setMyItemClick(new ItemsAdapter.MyItemClick() {
            @Override
            public void onItemClickListener(Object item, int position) {
                Item i = (Item) item;

                Toast.makeText(getApplicationContext(), i.toString() + " in the position: "+position, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new ScaleInBottomAnimator());

        //recyclerView.setItemAnimator(new ScaleInBottomAnimator());//new ScaleInBottomAnimator()
        //((DefaultItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getItemAnimator().setChangeDuration(0);


        //set the layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //adapter.notifyDataSetChanged();*/
    }

/*


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int currSize;

        switch (item.getItemId()){
            case R.id.action_add:
                currSize = adapter.getItemCount();

                products.addAll(createListItem(5, adapter.getItemCount()));

                adapter.notifyItemRangeInserted(currSize, products.size());
                //recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                Log.d("ItemCount", String.format("Hay %s productos", NumberUtils.formatNumber(products.size(), NumberUtils.FORMAT_NUMER_INTEGER)));
                break;

            case R.id.action_add_one:
                currSize = adapter.getItemCount();

                products.addAll(createListItem(1, adapter.getItemCount()));

                adapter.notifyItemRangeInserted(currSize, products.size());
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                Log.d("ItemCount", String.format("Hay %s productos", NumberUtils.formatNumber(products.size(), NumberUtils.FORMAT_NUMER_DOUBLE)));
                break;

            case R.id.action_clear:
                int currSz = adapter.getItemCount();
                products.clear();
                adapter.notifyItemRangeRemoved(0, currSz);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
*/

    private void setUpViewPager(int positionForStart){
        viewPager = findViewById(R.id.view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), getFragmentsForViewPager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(positionForStart);

    }

   private List<Fragment> getFragmentsForViewPager()
   {
       List<Fragment> fragments = new ArrayList<>();

       fragments.add(ItemListFragment.newInstance());
       fragments.add(ClientsFragment.newInstance(getClientsForTest(5)));
       fragments.add(ActividadFragment.newInstance(getActividadesDePrueba()));
       fragments.add(TestFragment.newInstance("Perfil", "Perfil del vendedor"));

       return fragments;
   }

   private List<Client> getClientsForTest(int count){
        List<Client> clients = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < count; i++){
            Client client = new Client();

            client.setId(String.valueOf(random.nextInt(5000)));
            client.setName("Cliente de Prueba #"+(i+1));
            client.setCreditLimit(random.nextDouble() * 5000.00);
            client.setDistance(random.nextDouble() * 100.00 * 5.00);
            client.setAddress("Calle #"+(i+1)+" Santiago de los Caballeros");
            client.setIdentityCard("402-2570666-8");
            client.addPhone("8098608075");
            client.setEmail("tec.dionicioacevedo@gmail.com");

            int id = (client.getDistance() < 300)? (R.drawable.photo ): (R.drawable.photo2);

            Uri path = Uri.parse("android.resource://"+getPackageName()+"/"+ id);
            client.setProfilePhoto(path);
            clients.add(client);
        }

        return clients;
   }

   private List<Actividad> getActividadesDePrueba(){
        List<Actividad> actividads = new ArrayList<>();

       Random random = new Random();

       int visitas = random.nextInt(50);
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

   private int getPercent(float min, float max){
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

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            List<Item> items = data.getParcelableArrayListExtra(SelectorItemActivity.EXTRA_ITEMS);

            for(Item i : items){
                Log.d("selectedItems", i.toString());
            }
        }
    }
}

