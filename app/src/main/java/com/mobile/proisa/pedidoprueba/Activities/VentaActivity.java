package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.ItemsListSalesAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.Constantes;
import Models.ITotal;
import Models.Invoice;
import Models.Item;
import Models.Vendor;
import Utils.NumberUtils;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

public class VentaActivity extends AppCompatActivity implements ItemsListSalesAdapter.OnListChangedListener, ItemsListSalesAdapter.NotificationListener {
    public static final String EXTRA_CLIENT = "extra_client";
    private static final int MY_REQUEST_CODE_ITEMS = 1000;
    private static final int PAYMENT_REQUEST_CODE = 1001;
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

        if(savedInstanceState == null){
            this.itemList = new ArrayList<>();
        }else{
            itemList =savedInstanceState.getParcelableArrayList("list");
        }

        Vendor vendor =VendorUtil.getVendor(this);
        setTitle(vendor.getName());
        getSupportActionBar().setSubtitle(R.string.vendor);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        loadAdapter();
        loadData();
    }

    private void loadAdapter(){
        adapter = new ItemsListSalesAdapter(this.itemList, R.layout.item_card_view);
        ((ItemsListSalesAdapter)adapter).setOnListChangedListener(this);
        ((ItemsListSalesAdapter)adapter).setNotificationListener(this);
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

    private void loadData(){
        TextView txtClient = findViewById(R.id.client_name);
        txtClient.setText(client.getName());
        TextView txtTotal = findViewById(R.id.total);

        if(itemList != null){
            txtTotal.setText(NumberUtils.formatNumber(NumberUtils.getTotal(new ArrayList<ITotal>(itemList)), NumberUtils.FORMAT_NUMER_DOUBLE));
        }else{
            txtTotal.setText(NumberUtils.formatNumber(0.00,NumberUtils.FORMAT_NUMER_DOUBLE));
        }

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
                if(!this.itemList.isEmpty()){
                    Invoice mInvoice = getInvoice();

                    Intent paymentActivity = new Intent(this, PaymentActivity.class);
                    paymentActivity.putExtra(PaymentActivity.EXTRA_INVOICE, mInvoice);
                    startActivityForResult(paymentActivity, PAYMENT_REQUEST_CODE);
                }
                return true;

            case R.id.add_items:
                startActivityForResult(new Intent(this, SelectorItemActivity.class)
                        //.putParcelableArrayListExtra(SelectorItemActivity.EXTRA_ITEMS,new ArrayList<>(this.itemList)),
                        ,MY_REQUEST_CODE_ITEMS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Invoice getInvoice() {
        Invoice invoice = new Invoice();

        invoice.setItems(this.itemList);
        invoice.setClient(this.client);

        return invoice;
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

                    //Invalidar el menu de opciones para que se re-dibuje
                    invalidateOptionsMenu();

                    loadData();
                }
                break;
            case PAYMENT_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    finish();
                }
                break;
        }
    }

    @Override
    public void onListChange(List<Item> list) {
        invalidateOptionsMenu();
        loadData();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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

    @Override
    public void onNotificationRequired(int notificationType) {
        switch (notificationType){
            case ItemsListSalesAdapter.NotificationListener.ITEM_STOCK_EXCEEDED:
                Toast.makeText(getApplicationContext(), R.string.stock_exceeded, Toast.LENGTH_LONG).show();
                break;

            case ItemsListSalesAdapter.NotificationListener.ITEM_QUANTITY_ZERO:
                Toast.makeText(getApplicationContext(), R.string.quantity_no_zero, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", new ArrayList<Item>(this.itemList));
    }



    public static class VendorUtil{
        public static Vendor getVendor(Context context){
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
            String codigo = sharedPreferences.getString(Constantes.VENDOR_CODE, "");
            String nombre = sharedPreferences.getString(Constantes.VENDOR_NAME, "");

            Vendor vendor = new Vendor();
            vendor.setId(codigo);
            vendor.setName(nombre);

            return vendor;
        }
    }


}
