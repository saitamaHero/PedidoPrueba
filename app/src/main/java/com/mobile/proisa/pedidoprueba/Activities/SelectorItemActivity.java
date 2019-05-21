package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Adapters.CustomExpandableListAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.MyOnItemSelectedListener;
import com.mobile.proisa.pedidoprueba.Adapters.SingleSimpleElementAdapter;
import com.mobile.proisa.pedidoprueba.Clases.CountDrawable;
import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.Category;
import Models.Item;
import Sqlite.CategoryController;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;

public class SelectorItemActivity extends AppCompatActivity implements MyOnItemSelectedListener, DialogInterface.OnClickListener, View.OnClickListener {
    public static final String EXTRA_ITEMS = "EXTRA_ITEMS";

    /**
     * Lista de elementos que han sido seleccionados
     */
    private List<Item> mItemList;

    /**
     * Información de la base de datos que es mostrada en Departamentos
     */
    private HashMap<Category, List<ItemSelectable>> searchItemGroup;


    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mExpandableAdapter;

    private TextView txtCategorySelected;

    private Category selectedCategory;

    private ListAdapter categories;

    private ItemController itemController;
    private Menu defaultMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_item);

        mItemList = getExtraItems();

        bindUI();

        setTitle(R.string.select_items);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAdapter();

        expandCategoriesWithItems();
    }

    private void bindUI() {
        mExpandableListView = findViewById(R.id.expandableListView);
        txtCategorySelected = findViewById(R.id.category_name);
        ImageView imgDelete = findViewById(R.id.img_remove);
        imgDelete.setOnClickListener(this);

        txtCategorySelected.setOnClickListener(this);

        showFilterLayout();
    }

    private void showFilterLayout() {
        boolean hide =  (selectedCategory == null);
        View view = findViewById(R.id.lyt_filter);
        view.setVisibility( hide ? View.GONE : View.VISIBLE);
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
        searchItemGroup = getAll();

        mExpandableAdapter = new CustomExpandableListAdapter(this, new ArrayList<Category>(searchItemGroup.keySet()), searchItemGroup );
        mExpandableListView.setAdapter(mExpandableAdapter);

        mExpandableAdapter.setOnItemSelectedListener(this);
    }

    private void expandCategoriesWithItems(){
        for(Item item : mItemList){
            int groupCount = mExpandableAdapter.getGroupCount();

            for(int idx = 0; idx < groupCount; idx++){
                Category category = (Category) mExpandableAdapter.getGroup(idx);

                if(category.equals(item.getCategory()) && !mExpandableListView.isGroupExpanded(idx)){
                    mExpandableListView.expandGroup(idx);
                }
            }
        }
    }

    private void expandAll(){
        int groupCount = mExpandableAdapter.getGroupCount();

        for(int idx = 0; idx < groupCount; idx++){
            if(!mExpandableListView.isGroupExpanded(idx)){
                mExpandableListView.expandGroup(idx);
            }
        }
    }

    public HashMap<Category, List<ItemSelectable>> getAll(){
        ItemController itemController = new ItemController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        List<ItemSelectable> itemSelectables = ItemSelectable.getItemSelectableList(itemController.getAll());

       return getOrder(ItemSelectable.checkItemsInTheList(itemSelectables, mItemList));
    }

    private HashMap<Category, List<ItemSelectable>> getOrder(List<ItemSelectable> itemSelectables){
        HashMap<Category, List<ItemSelectable>> listHashMap = new HashMap<>();
        List<Category> categories = new CategoryController(MySqliteOpenHelper.getInstance(this).getReadableDatabase()).getAll();

        for(Category currentCategory : categories){
            List<ItemSelectable> listByCategory  = new ArrayList<>();

            for(ItemSelectable itemSelectable : itemSelectables){
                if(itemSelectable.getCategory().equals(currentCategory)){
                    listByCategory.add(itemSelectable);
                }
            }

            if(listByCategory.size() > 0){
                listHashMap.put(currentCategory, listByCategory);
            }
        }

        return listHashMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_selector_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        defaultMenu = menu;
        showCountItems();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_select_items:
                if (!this.mItemList.isEmpty()) {
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

        int checkedItem = ((ArrayAdapter<Category>)categories).getPosition(selectedCategory);
        
        builder.setSingleChoiceItems(categories, checkedItem, new DialogInterface.OnClickListener() {
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
        resultIntent.putExtra(EXTRA_ITEMS, new ArrayList<>(this.mItemList));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showCountItems(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            setTitle(getResources().getQuantityString(R.plurals.items_selected, this.mItemList.size(), this.mItemList.size()));
        }else{
            setCount(this, String.valueOf(this.mItemList.size()));
        }
    }

    @Override
    public void onItemSelected(ItemSelectable itemSelectable) {
        if (!mItemList.contains(itemSelectable)) {
            mItemList.add(itemSelectable);
        } else {
            mItemList.remove(itemSelectable);
        }

        showCountItems();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(EXTRA_ITEMS, new ArrayList<>(mItemList));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mItemList = savedInstanceState.getParcelableArrayList(EXTRA_ITEMS);
    }



    private void search() {
        searchItemGroup.clear();
        mExpandableAdapter.notifyDataSetChanged();

        if(itemController == null)
            itemController = new ItemController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());

        if (selectedCategory == null) {
            List<Item> all = itemController.getAll();
            List<ItemSelectable> selectables = ItemSelectable.getItemSelectableList(all);

            searchItemGroup.putAll(getOrder(ItemSelectable.checkItemsInTheList(selectables, this.mItemList)));
        } else {
            if(selectedCategory != null){
                String selection =  Item._CAT.concat("=?");
                String[] selectionArgs = new String[]{selectedCategory.getId()};

                List<Item> listItems = itemController.getAll(selection,  selectionArgs);
                List<ItemSelectable> selectables = ItemSelectable.getItemSelectableList(listItems);

                searchItemGroup.putAll( getOrder(ItemSelectable.checkItemsInTheList(selectables, this.mItemList)));
            }
        }

        mExpandableAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                showFilterLayout();
                showCategoryChoosed();
                search();
                expandAll();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                selectedCategory = null;
                showFilterLayout();
                search();
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
                search();
                break;
        }
    }

    public void setCount(Context context, String count) {

        MenuItem menuItem = defaultMenu.findItem(R.id.action_select_items);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_count);

        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_count, badge);
    }
}