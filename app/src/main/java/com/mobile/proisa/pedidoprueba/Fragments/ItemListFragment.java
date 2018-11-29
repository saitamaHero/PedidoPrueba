package com.mobile.proisa.pedidoprueba.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Activities.DetailsClientActivity;
import com.mobile.proisa.pedidoprueba.Adapters.ClientAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ItemListAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Models.Client;
import Models.Item;

public class ItemListFragment extends Fragment {
    private static final String PARAM_ITEMS = "";
    private List<Item> items;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;

    public ItemListFragment() { }

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

        if(this.items == null){
            items = createListItem(10,0);
        }

        setAdapter();
    }

    private void setAdapter() {
        itemListAdapter = new ItemListAdapter(this.items, R.layout.item_basic_card);
        recyclerView.setAdapter(itemListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemListAdapter.setOnItemClickListener(new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
}
