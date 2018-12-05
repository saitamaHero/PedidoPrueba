package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;

public class SelectorItemActivity extends AppCompatActivity implements MyOnItemSelectedListener, SearchView.OnQueryTextListener {
    public static final String EXTRA_ITEMS = "EXTRA_ITEMS";

    public List<Item> itemList;
    public List<ItemSelectable> searchItemList;
    private RecyclerView recyclerView;
    private ItemSelectableAdapter itemSelectableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_item);

        itemList = new ArrayList<>();
        searchItemList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        loadAdapter();
    }

    private void loadAdapter() {
         itemSelectableAdapter =
                new ItemSelectableAdapter(searchItemList,
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

        /*if(itemList.size() == 0){
            menuItem.setVisible(false);
        }else{
            menuItem.setVisible(true);
        }*/

        menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);


        //searchView.

       // final MenuItem finalMenuItem = menuItem;

        //searchView.set


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_select_items:
                if(!this.itemList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enviar elementos seleccionados",
                            Toast.LENGTH_SHORT).show();
                    sendData();
                }
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
        }else{
            itemList.remove(itemSelectable);
        }

        setTitle(getResources().getQuantityString(R.plurals.items_selected,this.itemList.size(), this.itemList.size()));
        //invalidateOptionsMenu();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchItemList.removeAll(searchItemList);

        if(TextUtils.isEmpty(newText)){
            List<ItemSelectable> selectables = ItemSelectable.getItemSelectableList(this.itemList);
            searchItemList.addAll(ItemSelectable.checkItemsInTheList(selectables, this.itemList));
        }else{
            List<ItemSelectable> list = ItemSelectable.getItemSelectableList(getItems(newText));
            searchItemList.addAll(ItemSelectable.checkItemsInTheList(list, this.itemList));
        }

        itemSelectableAdapter.notifyDataSetChanged();

        return true;
    }

    private List<Item> getItems(String str) {
        ItemController controller = new ItemController(
                new MySqliteOpenHelper(this, "PRUEBA.db", null,
                        MySqliteOpenHelper.VERSION).getWritableDatabase());


        return controller.getAllLike(str);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
