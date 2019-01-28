package com.mobile.proisa.pedidoprueba.Activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.Fragments.InvoiceListFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Client;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends AppCompatActivity implements InvoiceListAdapter.OnInvoiceClickListener{
    private static final String TAG = "InvoiceListActivity";

    private InvoiceController invoiceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        Client client = getIntent().getExtras().getParcelable(DetailsClientActivity.EXTRA_CLIENT);

        invoiceController = new InvoiceController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        List<Invoice> invoiceList = invoiceController.getAllById(client.getId());

        setCurrentFragment(InvoiceListFragment.newInstance(invoiceList));

    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onInvoiceClick(Invoice invoice) {
        Toast.makeText(getApplicationContext(), invoice.toString(), Toast.LENGTH_SHORT).show();
    }
}
