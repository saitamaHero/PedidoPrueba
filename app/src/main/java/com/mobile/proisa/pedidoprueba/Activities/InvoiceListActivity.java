package com.mobile.proisa.pedidoprueba.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.Fragments.InvoiceListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.TextMessageFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Client;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends BaseCompatAcivity implements InvoiceListAdapter.OnInvoiceClickListener{
    private static final String TAG = "InvoiceListActivity";

    private InvoiceController invoiceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        setTitle(R.string.invoices);

        Client client = getIntent().getExtras().getParcelable(DetailsClientActivity.EXTRA_CLIENT);

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
        startActivity(new Intent(this, VentaActivity.class)
                .putExtra(BaseCompatAcivity.EXTRA_INVOICE, invoice));
    }
}
