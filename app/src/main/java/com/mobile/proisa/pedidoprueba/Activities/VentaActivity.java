package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.util.List;

import Models.Constantes;
import Models.Invoice;
import Models.Item;
import Models.Vendor;
import Utils.NumberUtils;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

public class VentaActivity extends BaseCompatAcivity implements ItemsListSalesAdapter.OnListChangedListener, ItemsListSalesAdapter.NotificationListener {
    private static final int MY_REQUEST_CODE_ITEMS = 1000;
    private static final int PAYMENT_REQUEST_CODE = 1001;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private Invoice mInvoice;
    private boolean isNewInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        if(savedInstanceState == null){
            this.mInvoice = getInvoiceFromIntent();
        }/*else{
            this.mInvoice = savedInstanceState.getParcelable(EXTRA_INVOICE);
        }*/

        Vendor vendor = VendorUtil.getVendor(this);
        setTitle(vendor.getName());
        getSupportActionBar().setSubtitle(R.string.vendor);


        isNewInvoice = getIntent().getBooleanExtra(EXTRA_IS_NEW_INVOICE, true);
    }

    @Override
    protected void onBindUI() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        loadAdapter();
        loadData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_INVOICE, mInvoice);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mInvoice = savedInstanceState.getParcelable(EXTRA_INVOICE);
    }

    private void loadAdapter(){
        adapter = new ItemsListSalesAdapter(mInvoice.getItems(), R.layout.item_card_view);
        ((ItemsListSalesAdapter)adapter).setOnListChangedListener(this);
        ((ItemsListSalesAdapter)adapter).setNotificationListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FadeInRightAnimator());

        SimpleItemAnimator itemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        itemAnimator.setChangeDuration(0L);
    }

    private Invoice getInvoiceFromIntent(){
        Intent intent = getIntent();

        try{
            Bundle bundle = intent.getExtras();
            return bundle.getParcelable(EXTRA_INVOICE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    private void loadData(){
        TextView txtClient = findViewById(R.id.client_name);
        txtClient.setText(mInvoice.getClient().getName());
        TextView txtTotal = findViewById(R.id.total);

        if(mInvoice.containsItems()){
            //NumberUtils.getTotal(new ArrayList<ITotal>(mInvoice.getItems()))
            txtTotal.setText(NumberUtils.formatNumber(mInvoice.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));
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

        if(isNewInvoice){
            if(mInvoice.containsItems()){
                menuItem.setVisible(true);
            }else{
                menuItem.setVisible(false);
            }
        }else{
            MenuItem addIItem = menu.findItem(R.id.add_items);

            menuItem.setVisible(false);
            addIItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if(mInvoice.containsItems()){
                    Intent paymentActivity = new Intent(this, PaymentActivity.class);
                    paymentActivity.putExtra(EXTRA_INVOICE, mInvoice);
                    paymentActivity.putExtra(EXTRA_IS_NEW_INVOICE, isNewInvoice);
                    startActivityForResult(paymentActivity, PAYMENT_REQUEST_CODE);
                }
                return true;

            case R.id.add_items:
                startActivityForResult(new Intent(this, SelectorItemActivity.class)
                        ,MY_REQUEST_CODE_ITEMS);
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

                    int count = mInvoice.getItems().size();

                    List<Item> items = data.getExtras().getParcelableArrayList(SelectorItemActivity.EXTRA_ITEMS);
                    mInvoice.setItems(items);

                    //notify Adapter

                    if(adapter != null){
                        adapter.notifyItemRangeInserted(count , adapter.getItemCount());
                    }

                    //Invalidar el menu de opciones para que se re-dibuje
                    invalidateOptionsMenu();
                    loadData();
                }
                break;
            case PAYMENT_REQUEST_CODE:
                //if(resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                //}
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
        if(isNewInvoice && mInvoice.containsItems()){
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
        }else{
            super.onBackPressed();
        }

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
