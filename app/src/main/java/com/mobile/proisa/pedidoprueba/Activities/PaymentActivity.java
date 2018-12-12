package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoicePaymentAdapter;
import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Models.ITotal;
import Models.Invoice;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String EXTRA_INVOICE = "com.mobile.proisa.pedidoprueba.EXTRA_INVOICE";
    private Spinner spPayment;
    private Invoice invoiceToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        invoiceToSave = getInvoiceToShow();

        spPayment = findViewById(R.id.spPayment);

       InvoicePaymentAdapter adapter =
               new InvoicePaymentAdapter(this, R.layout.invoice_type_layout, getInvoiceTypes());

        spPayment.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spPayment.setOnItemSelectedListener(this);


        loadData();

        Log.d("InvoiceToSave", invoiceToSave.toString());
    }

    private void loadData(){
        TextView txtClient = findViewById(R.id.client_name);
        txtClient.setText(invoiceToSave.getClient().getName());
        TextView txtTotal = findViewById(R.id.total);

        if(invoiceToSave.getItems() != null){
            txtTotal.setText(NumberUtils.formatNumber(NumberUtils.getTotal(new ArrayList<ITotal>(invoiceToSave.getItems())),NumberUtils.FORMAT_NUMER_DOUBLE));
        }else{
            txtTotal.setText(NumberUtils.formatNumber(0.00,NumberUtils.FORMAT_NUMER_DOUBLE));
        }

    }

    private Invoice getInvoiceToShow() {
        Intent intent = getIntent();

        try{
            Bundle extras = intent.getExtras();
            return extras.getParcelable(EXTRA_INVOICE);
        }catch (Exception e){
            finish();
        }

        return null;
    }

    private List<InvoiceType> getInvoiceTypes(){
        List<InvoiceType> invoiceTypes = new ArrayList<>();

        for(Invoice.InvoicePayment invoicePayment : Invoice.InvoicePayment.values()){
            invoiceTypes.add(new InvoiceType(invoicePayment));
        }

        return invoiceTypes;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        InvoiceType invoiceType = (InvoiceType) adapterView.getItemAtPosition(i);
        invoiceToSave.setInvoiceType(invoiceType.getInvoicePayment());
        Log.d("InvoiceToSave", invoiceToSave.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
