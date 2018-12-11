package com.mobile.proisa.pedidoprueba.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoicePaymentAdapter;
import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Models.Invoice;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner spPayment;
    private Invoice invoiceToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        spPayment = findViewById(R.id.spPayment);

       InvoicePaymentAdapter adapter =
               new InvoicePaymentAdapter(this, R.layout.invoice_type_layout,getInvoiceTypes());

        spPayment.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spPayment.setOnItemSelectedListener(this);
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
        Toast.makeText(getApplicationContext(), invoiceType.getInvoicePayment().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
