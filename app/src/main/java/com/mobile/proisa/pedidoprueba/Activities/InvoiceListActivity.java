package com.mobile.proisa.pedidoprueba.Activities;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.AbstractTicket;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.BluetoothUtils;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.InvoiceTicket;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Dialogs.ProgressDialog;
import com.mobile.proisa.pedidoprueba.Fragments.InvoiceListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.TextMessageFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Client;
import Models.Company;
import Models.Invoice;
import Sqlite.CompanyController;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends BaseCompatAcivity implements InvoiceListAdapter.OnInvoiceClickListener {
    private static final String TAG = "InvoiceListActivity";
    private InvoiceController invoiceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        setTitle(R.string.invoices);

        getClientAndSearchInvoices();

    }

    private void getClientAndSearchInvoices(){
        Intent intent = getIntent();

        if(intent != null){
            Bundle extras = intent.getExtras();

            if(extras != null && extras.containsKey(DetailsClientActivity.EXTRA_CLIENT)){
                Client client = extras.getParcelable(DetailsClientActivity.EXTRA_CLIENT);
                showInvoicesForClient(client);
            }
        }
    }

    private void showInvoicesForClient(Client client){
        invoiceController = new InvoiceController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        List<Invoice> invoiceList = invoiceController.getAllById(client.getId());

        if(invoiceList.size() > 0){
            setCurrentFragment(R.id.container, InvoiceListFragment.newInstance(invoiceList));
        }else{
            setCurrentFragment(R.id.container, TextMessageFragment.newInstance(getString(R.string.not_invoices, client.getName())));
        }

    }

    @Override
    public void onInvoiceClick(Invoice invoice) {
       startActivity(new Intent(this, InvoiceDetailsActivity.class).putExtra(EXTRA_INVOICE, invoice));
    }


}