package com.mobile.proisa.pedidoprueba.Fragments;


import android.app.Activity;
import android.content.Intent;
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
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Tasks.DialogInTask;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import BaseDeDatos.CategoryUpdater;
import BaseDeDatos.CompanyUpdater;
import BaseDeDatos.ItemUpdater;
import BaseDeDatos.SqlConnection;
import BaseDeDatos.SqlUpdater;
import BaseDeDatos.UnitUpdater;
import Models.Category;
import Models.Company;
import Models.Item;
import Models.Unit;
import Sqlite.CategoryController;
import Sqlite.CompanyController;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;
import Sqlite.UnitController;
import Utils.DateUtils;

public class ItemListFragment extends Fragment implements ItemListAdapter.OnItemClickListener, TareaAsincrona.OnFinishedProcess {
    private static final String PARAM_ITEMS = "param_items";
    private static final int ITEMS_COUNT_DEFAULT = 30;
    private List<Item> items;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;

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
        updateList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateList(){
        this.items = getItems(ITEMS_COUNT_DEFAULT);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sync:
                new SyncItems(0, getActivity(), this, true).execute();
                break;
        }

        return true;
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

    @Override
    public void onItemClick(Item item) {
        Intent seeMoreIntent = new Intent(getActivity().getApplicationContext(), DetailsItemActivity.class);
        seeMoreIntent.putExtra(DetailsItemActivity.EXTRA_ITEM_DATA, item);
        getActivity().startActivity(seeMoreIntent);
    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            Toast.makeText(getActivity(), getString(R.string.updater_success), Toast.LENGTH_SHORT).show();
            updateList();
        }

    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Toast.makeText(getActivity(), exceptions.pop().getMessage(), Toast.LENGTH_SHORT).show();
    }


    public static class SyncItems extends DialogInTask<Void, String, Void> implements SqlUpdater.OnDataUpdateListener<Item> {

        public SyncItems(int id, Activity context, OnFinishedProcess listener) {
            super(id, context, listener);
        }

        public SyncItems(int id, Activity context, OnFinishedProcess listener, boolean mDialogShow) {
            super(id, context, listener, mDialogShow);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress(getContext().getString(R.string.starting));

            SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());

            /**
             * Articulos
             */
            ItemController itemController = new ItemController(MySqliteOpenHelper.getInstance(getContext()).getWritableDatabase());
            //Si no hay elementos en la base de datos no se analizara practicamente nada.
            ItemUpdater itemUpdater = new ItemUpdater(getContext().getApplicationContext(), connection, itemController);
            itemUpdater.setOnDataUpdateListener(this);
            //Llamar este metodo para que inserte los datos que hacen falta del servidor
            itemUpdater.retriveData();

            /**
             * Categoría
             */
            CategoryController categoryController = new CategoryController(MySqliteOpenHelper.getInstance(getContext()).getWritableDatabase());
            //Si no hay elementos en la base de datos no se analizara practicamente nada.
            CategoryUpdater categoryUpdater = new CategoryUpdater(getContext().getApplicationContext(), connection, categoryController);
            //Llamar este metodo para que inserte los datos que hacen falta del servidor
            categoryUpdater.retriveData();

            /**
             * Unidad
             */
            UnitController unitController = new UnitController(MySqliteOpenHelper.getInstance(getContext()).getWritableDatabase());
            //Si no hay elementos en la base de datos no se analizara practicamente nada.
            UnitUpdater unitUpdater = new UnitUpdater(getContext().getApplicationContext(), connection, unitController);
            //Llamar este metodo para que inserte los datos que hacen falta del servidor
            unitUpdater.retriveData();


            CompanyUpdater updater = new CompanyUpdater(getContext().getApplicationContext(), connection, new CompanyController(MySqliteOpenHelper.getInstance(getContext()).getWritableDatabase()));
            updater.retriveData();

            return null;
        }

        @Override
        public void onDataUpdate(Item data, int action) {
            String resource;

            switch (action){
                case ACTION_INSERT_REMOTE:
                case ACTION_INSERT_LOCAL:
                    resource = getContext().getString(R.string.insert_msg,data.getName());
                    break;

                case ACTION_UPDATE_REMOTE:
                    resource = getContext().getString(R.string.update_msg, data.getName());
                    break;

                default:
                    return;
            }

            publishProgress(resource);
        }

        @Override
        public void onDataUpdated(Item data) {

        }
    }
}