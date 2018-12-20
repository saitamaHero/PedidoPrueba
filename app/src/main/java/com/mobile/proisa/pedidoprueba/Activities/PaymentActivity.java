package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoicePaymentAdapter;
import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import Models.ColumnsSqlite;
import Models.ITotal;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String EXTRA_INVOICE = "com.mobile.proisa.pedidoprueba.EXTRA_INVOICE";
    private Spinner spPayment;
    private Button btnCompletePayment;
    private Invoice invoiceToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        invoiceToSave = getInvoiceToShow();

        spPayment = findViewById(R.id.spPayment);
        btnCompletePayment = findViewById(R.id.btn_complete_payment);
        btnCompletePayment.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_complete_payment:
                if(invoiceToSave.getInvoiceType().equals(Invoice.InvoicePayment.CASH)){
                    Toast.makeText(getApplicationContext(), "Pagando en Efectivo", Toast.LENGTH_LONG).show();

                    showDialogToReturnMoney(invoiceToSave);
                }
                break;
        }
    }

    private void showDialogToReturnMoney(Invoice invoiceToSave) {

    }


    public void saveInvoice(){
        invoiceToSave.setComment("");
        invoiceToSave.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);
        invoiceToSave.setDate(Calendar.getInstance().getTime());
        invoiceToSave.setId(String.valueOf(invoiceToSave.hashCode()));

        InvoiceController controller = new InvoiceController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());
        if(controller.insert(invoiceToSave)){
            Toast.makeText(getApplicationContext(),
                    "Se guardó la factura "+invoiceToSave.toString(), Toast.LENGTH_LONG)
                    .show();

            setResult(RESULT_OK);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "No guardó la factura "+invoiceToSave.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
