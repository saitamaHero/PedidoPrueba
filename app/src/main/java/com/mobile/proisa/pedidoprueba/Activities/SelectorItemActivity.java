package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.ItemSelectableAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.MyOnItemSelectedListener;
import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.Item;

public class SelectorItemActivity extends AppCompatActivity implements MyOnItemSelectedListener, SearchView.OnQueryTextListener {
    public static final String EXTRA_ITEMS = "EXTRA_ITEMS";

    public List<Item> itemList;
    private RecyclerView recyclerView;
    private ItemSelectableAdapter itemSelectableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_item);

        itemList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        loadAdapter();
    }

    private void loadAdapter() {
         itemSelectableAdapter =
                new ItemSelectableAdapter(ItemListFragment.getSelectableList(
                        ItemListFragment.createListItem(5,0)),
                        R.layout.item_selectable_card,
                        true);

        recyclerView.setAdapter(itemSelectableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemSelectableAdapter.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_selector_item,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.action_select_items);

        if(itemList.size() == 0){
            menuItem.setVisible(false);
        }else{
            menuItem.setVisible(true);
        }

        menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_select_items:
                Toast.makeText(getApplicationContext(), "Enviar elementos seleccionados",
                        Toast.LENGTH_SHORT).show();

                sendData();
                break;
        }

        return true;
    }

    private void sendData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ITEMS, new ArrayList<>(this.itemList));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onItemSelected(ItemSelectable itemSelectable) {
        if(!itemList.contains(itemSelectable)){
            itemList.add(itemSelectable);

            Log.d("itemAdded", itemSelectable.toString());
        }else{
            itemList.remove(itemSelectable);
        }



        setTitle(String.format(Locale.getDefault(), "%s %s", itemList.size(), itemList.size() == 1? "producto" : "productos"));
        invalidateOptionsMenu();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
