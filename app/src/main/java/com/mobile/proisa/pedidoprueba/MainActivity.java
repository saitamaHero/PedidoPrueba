package com.mobile.proisa.pedidoprueba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Adapters.ItemsAdapter;
import com.mobile.proisa.pedidoprueba.Models.Item;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        recyclerView.setAdapter(adapter);

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

        switch (item.getItemId()){
            case R.id.action_add:
                int currSize = adapter.getItemCount();

                products.addAll(createListItem(5, adapter.getItemCount()));

                adapter.notifyItemRangeInserted(currSize, products.size());
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
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

