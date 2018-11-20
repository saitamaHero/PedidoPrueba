package com.mobile.proisa.pedidoprueba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.ItemsAdapter;
import com.mobile.proisa.pedidoprueba.Models.Item;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MainActivity extends AppCompatActivity {

    private List<Item> products;
    private ItemsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        products = createListItem(3, 0);

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


        //adapter.notifyDataSetChanged();
    }


    private static Item getItem(String id, String name){
        Item item = new Item(id, name);
        Random random = new Random();


        item.setId("COD-".concat(String.valueOf(random.nextInt(1000) * (1 +random.nextInt(99))) ));
        item.setPrice(random.nextDouble() * 100.00 + 100.00);
        item.setQuantity(random.nextInt(10) * random.nextInt(10));
        item.setStock(random.nextInt(100) * random.nextInt(5));

        return item;
    }


    public static List<Item> createListItem(int count, int startPosition){
        List<Item> items = new ArrayList<>(count);

        for(int i = startPosition; i < startPosition + count; i++){
            items.add(getItem("","PRODUCTO DE PRUEBA " + i));
        }

        return items;
    }

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


}

