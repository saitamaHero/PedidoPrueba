package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.ItemSelectableAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.MyOnItemSelectedListener;
import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Category;
import Models.Item;
import Models.SimpleElement;
import Sqlite.CategoryController;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;

public class SelectorItemActivity extends AppCompatActivity implements MyOnItemSelectedListener, SearchView.OnQueryTextListener, DialogInterface.OnClickListener, View.OnClickListener {
    public static final String EXTRA_ITEMS = "EXTRA_ITEMS";

    public List<Item> itemList;
    public List<ItemSelectable> searchItemList;

    private RecyclerView recyclerView;
    private TextView txtCategorySelected;
    private ImageView imgDelete;
    private ItemSelectableAdapter itemSelectableAdapter;

    private Category selectedCategory;

    private ListAdapter categories;
    private String mLastTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_item);

        itemList = getExtraItems();
        searchItemList = new ArrayList<>();

        bindUI();


        loadAdapter();

        if (itemList == null || itemList.isEmpty()) {
            setTitle(getResources().getQuantityString(R.plurals.items_selected, this.itemList.size(), this.itemList.size()));
        }
    }

    private void bindUI() {
        recyclerView = findViewById(R.id.recycler_view);
        txtCategorySelected = findViewById(R.id.category_name);
        imgDelete = findViewById(R.id.img_remove);
        imgDelete.setOnClickListener(this);

        txtCategorySelected.setOnClickListener(this);

        showFilterLayout();
    }

    private void showFilterLayout() {
        View view = findViewById(R.id.lyt_filter);
        if (selectedCategory == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private List<Item> getExtraItems() {
        Intent intent = getIntent();

        try {
            Bundle bundle = intent.getExtras();
            return bundle.getParcelableArrayList(EXTRA_ITEMS);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private void loadAdapter() {
        itemSelectableAdapter = new ItemSelectableAdapter(searchItemList, R.layout.item_selectable_card, true);

        recyclerView.setAdapter(itemSelectableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemSelectableAdapter.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_selector_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem;

        menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_select_items:
                if (!this.itemList.isEmpty()) {
                    sendData();
                }
                break;
            case R.id.action_filter:
                showDialogToChoose();
                break;
        }

        return true;
    }

    private void showDialogToChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        categories = getCategories();


        builder.setSingleChoiceItems(categories, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedCategory = (Category) categories.getItem(i);
            }
        });

        builder.setPositiveButton(R.string.search, this);
        builder.setNegativeButton(R.string.clean_filter, this);
        builder.create().show();
    }

    private ListAdapter getCategories() {
        ArrayAdapter listAdapter = new SingleSimpleElementAdapter(this, android.R.layout.select_dialog_singlechoice);
        CategoryController categoryController = new CategoryController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        List<Category> categories = categoryController.getAll();
        listAdapter.addAll(categories);

        return listAdapter;
    }


    private void sendData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ITEMS, new ArrayList<>(this.itemList));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onItemSelected(ItemSelectable itemSelectable) {
        if (!itemList.contains(itemSelectable)) {
            itemList.add(itemSelectable);
        } else {
            itemList.remove(itemSelectable);
        }

        setTitle(getResources().getQuantityString(R.plurals.items_selected, this.itemList.size(), this.itemList.size()));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    private void search(String newText) {
        mLastTextSearch = newText;

        searchItemList.removeAll(searchItemList);

        ItemController itemController = new ItemController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());

        if (TextUtils.isEmpty(newText)) {
            List<ItemSelectable> selectables = ItemSelectable.getItemSelectableList(this.itemList);
            searchItemList.addAll(ItemSelectable.checkItemsInTheList(selectables, this.itemList));
        } else {
            if (selectedCategory != null) {
                List<Item> listItems = itemController.getAll(Item._CAT.concat("=? AND ").concat(Item._NAME).concat(" LIKE ?"), new String[]{selectedCategory.getId(), "%" + newText + "%"});

                List<ItemSelectable> list = ItemSelectable.getItemSelectableList(listItems);
                searchItemList.addAll(ItemSelectable.checkItemsInTheList(list, this.itemList));
            } else {
                List<ItemSelectable> list = ItemSelectable.getItemSelectableList(itemController.getAllLike(newText));
                searchItemList.addAll(ItemSelectable.checkItemsInTheList(list, this.itemList));
            }
        }

        itemSelectableAdapter.notifyDataSetChanged();
    }

    private List<Item> getItems(int count) {
        ItemController controller = new ItemController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        return controller.getAll(count);
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                showFilterLayout();
                showCategoryChoosed();

                search(mLastTextSearch);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                selectedCategory = null;
                showFilterLayout();
                break;
        }
    }

    private void showCategoryChoosed() {
        String str = "";

        if(selectedCategory != null){
            str = getString(R.string.category).concat(": ").concat(selectedCategory.getName());
        }

        txtCategorySelected.setText(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.category_name:
                showDialogToChoose();
                break;

            case R.id.img_remove:
                selectedCategory = null;
                showCategoryChoosed();
                showFilterLayout();
                break;
        }
    }


    public static class SingleSimpleElementAdapter extends ArrayAdapter<SimpleElement> implements ListAdapter {

        public SingleSimpleElementAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            SimpleElement element = getItem(position);

            convertView = super.getView(position, convertView, parent);

            TextView txtView = convertView.findViewById(android.R.id.text1);
            txtView.setText(element.getName());

            return convertView;
        }
    }
}
