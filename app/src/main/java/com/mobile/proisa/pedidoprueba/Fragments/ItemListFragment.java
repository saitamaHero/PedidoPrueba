package com.mobile.proisa.pedidoprueba.Fragments;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Activities.DetailsItemActivity;
import com.mobile.proisa.pedidoprueba.Adapters.ItemListAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ItemSelectableAdapter;
import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import Models.Category;
import Models.Item;
import Models.Unit;
import Sqlite.Controller;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;
import Utils.DateUtils;

public class ItemListFragment extends Fragment implements ItemListAdapter.OnItemClickListener {
    private static final String PARAM_ITEMS = "param_items";
    private static final int ITEMS_COUNT_DEFAULT = 3;
    private List<Item> items;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;


    public static final String[] CATEGORIES = {"Bebidas", "Alimentos", "Limpieza", "Dulces"};


    public ItemListFragment() {
    }

    public static ItemListFragment newInstance() {
        Bundle args = new Bundle();
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);

        if (this.items == null) {
            this.items = getItems(ITEMS_COUNT_DEFAULT);

            if(this.items.size() == 0){
                ItemController controller = new ItemController(MySqliteOpenHelper.getInstance(getActivity()).getWritableDatabase());

                List<Item> items = createListItem(2000, 0);

               if(controller.insertAll(items))
               {
                  this.items = getItems(ITEMS_COUNT_DEFAULT);
               }
            }

            printTime(this.items);
        }

        setAdapter();
    }

    private List<Item> getItems(int count) {
        ItemController controller = new ItemController(MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase());
        return controller.getAll(count);
    }

    private List<Item> getItems(String str) {
        ItemController controller = new ItemController(MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase());
        return controller.getAllLike(str);
    }

    private void printTime(List<Item> items){
        Date currentDate = Calendar.getInstance().getTime();

        for(Item i : items){
            DateUtils.DateConverter converter = new DateUtils.DateConverter(i.getLastModification(), currentDate);
            Log.d("itemDiffTime", converter.toString());
        }
    }


    private void setAdapter() {
        itemListAdapter = new ItemListAdapter(this.items, R.layout.item_basic_card);
        recyclerView.setAdapter(itemListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                items.removeAll(items);

                items.addAll(getItems(query));
                itemListAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                items.removeAll(items);

                if (TextUtils.isEmpty(newText)) {
                    items.addAll(getItems(ITEMS_COUNT_DEFAULT));
                } else {
                    items.addAll(getItems(newText));
                }

                itemListAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    public static Item getItem(String id, String name) {
        Item item = new Item(id, name);
        Random random = new Random();


        item.setId("COD-".concat(String.valueOf(random.nextInt(1000) * (1 + random.nextInt(99)))));
        item.setPrice(random.nextDouble() * 100.00 + 100.00);
        item.setQuantity(random.nextInt(10) * random.nextInt(10));
        item.setStock(random.nextInt(100) * random.nextInt(5));
        item.setPhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));
        item.setCategory(getRandomCategory());
        item.setUnit(new Unit("UD", "UNIDAD"));

        return item;
    }


    public static List<Item> createListItem(int count, int startPosition) {
        List<Item> items = new ArrayList<>(count);

        for (int i = startPosition; i < startPosition + count; i++) {
            items.add(getItem("", "PRODUCTO DE PRUEBA " + i));
        }

        return items;
    }

    public static Category getRandomCategory() {
        Random random = new Random();
        return new Category("CAT-" + random.nextInt(50), CATEGORIES[random.nextInt(CATEGORIES.length)]);
    }

    @Override
    public void onItemClick(Item item) {
        Intent seeMoreIntent = new Intent(getActivity().getApplicationContext(), DetailsItemActivity.class);
        seeMoreIntent.putExtra(DetailsItemActivity.EXTRA_ITEM_DATA, item);
        getActivity().startActivity(seeMoreIntent);
    }
}