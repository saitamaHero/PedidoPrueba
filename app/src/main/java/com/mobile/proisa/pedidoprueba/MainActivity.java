package com.mobile.proisa.pedidoprueba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Adapters.ItemsAdapter;
import com.mobile.proisa.pedidoprueba.Models.Item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private List<Item> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        products = createListItem(50);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        //Create de adapter for items
        ItemsAdapter adapter = new ItemsAdapter(products, R.layout.item_card_view);
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


    public static List<Item> createListItem(int count){
        List<Item> items = new ArrayList<>(count);

        for(int i = 0; i < count; i++){
            items.add(getItem("","PRODUCTO DE PRUEBA " + (i+1)));
        }

        return items;
    }

}

