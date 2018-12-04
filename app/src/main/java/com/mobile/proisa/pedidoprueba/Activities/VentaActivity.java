package com.mobile.proisa.pedidoprueba.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mobile.proisa.pedidoprueba.Adapters.ItemListAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ItemsListSalesAdapter;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.Item;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

public class VentaActivity extends AppCompatActivity implements ItemsListSalesAdapter.OnListChangedListener {
    public static final String EXTRA_CLIENT = "extra_client";
    private static final int MY_REQUEST_CODE_ITEMS = 1000;
    //public static final String EXTRA_XXX = "";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Client client;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        client = getClient();

        this.itemList = new ArrayList<>();

        setTitle(client.getName());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        adapter = new ItemsListSalesAdapter(this.itemList, R.layout.item_card_view);
        ((ItemsListSalesAdapter)adapter).setOnListChangedListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FadeInRightAnimator());


        SimpleItemAnimator itemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        itemAnimator.setChangeDuration(0L);
    }

    private Client getClient(){
        Intent intent = getIntent();

        try{
            Bundle bundle = intent.getExtras();
            return bundle.getParcelable(EXTRA_CLIENT);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_venta, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_save);

        if(this.itemList == null || this.itemList.isEmpty()){
            menuItem.setVisible(false);
        }else{
            menuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                Log.d("VentaActivity",
                        String.format("guardar %s articulos a nombre de %s",
                        NumberUtils.formatNumber(itemList.size(), NumberUtils.FORMAT_NUMER_INTEGER),
                        this.client.toString()));
                return true;

            case R.id.add_items:
                startActivityForResult(new Intent(this, SelectorItemActivity.class),
                        MY_REQUEST_CODE_ITEMS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case MY_REQUEST_CODE_ITEMS:
                if(resultCode == RESULT_OK
                        && data.getExtras().containsKey(SelectorItemActivity.EXTRA_ITEMS)){

                    int count = this.itemList.size();

                    List<Item> items = data.getExtras().getParcelableArrayList(SelectorItemActivity.EXTRA_ITEMS);
                    this.itemList.addAll(items);

                    //notify Adapter
                    adapter.notifyItemRangeInserted(count , adapter.getItemCount());

                    //Invalidar el menu de opciones para que se redibuje
                    invalidateOptionsMenu();
                }
                break;
        }
    }

    @Override
    public void onListChange(List<Item> list) {
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        builder.setTitle(R.string.msg_exit_sale);
        builder.setTitle(R.string.msg_exit_no_save);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.create().show();

    }
}
