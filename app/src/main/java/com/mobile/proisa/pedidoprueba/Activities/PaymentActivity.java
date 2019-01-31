package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.Dialogs.CashPaymentDialog;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.ColumnsSqlite;
import Models.ITotal;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String EXTRA_INVOICE = "com.mobile.proisa.pedidoprueba.EXTRA_INVOICE";
    private Spinner spPayment;
    private Button btnCompletePayment;
    private Invoice invoiceToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setTitle(R.string.payment);

        invoiceToSave = getInvoiceToShow();
        bindUI();
        loadInvoiceTypes();

        loadData();
    }

    private void bindUI() {
        spPayment = findViewById(R.id.spPayment);
        btnCompletePayment = findViewById(R.id.btn_complete_payment);
        btnCompletePayment.setOnClickListener(this);
    }


    private void loadInvoiceTypes() {
        InvoiceTypeAdapter adapter = new InvoiceTypeAdapter(getBaseContext(), R.layout.spinner_item_custom);
        adapter.addAll(getInvoiceTypes());
        spPayment.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spPayment.setOnItemSelectedListener(this);
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
                    showDialogToReturnMoney(invoiceToSave);
                }
                break;
        }
    }

    private void showDialogToReturnMoney(Invoice invoiceToSave) {
        CashPaymentDialog.newInstance(invoiceToSave, new CashPaymentDialog.OnPaymentComplete() {
            @Override
            public void onPaymentComplete(boolean success, double money) {
                if(success){
                    saveInvoice();
                    /*setResult(RESULT_OK);
                    finish();*/
                }

                //Mantenerse en la actividad

            }
        }).show(getSupportFragmentManager(), null);
    }

    private Invoice getReadyInvoice() {
        TextInputEditText edtComment = findViewById(R.id.comment);
        invoiceToSave.setComment(edtComment.getText().toString());
        invoiceToSave.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);
        invoiceToSave.setDate(Calendar.getInstance().getTime());
        invoiceToSave.setId(String.valueOf(invoiceToSave.hashCode()));

        return invoiceToSave;
    }

    public void saveInvoice(){
        invoiceToSave = getReadyInvoice();

        InvoiceController controller = new InvoiceController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

        if(controller.insert(invoiceToSave)){
            Toast.makeText(getApplicationContext(), "Se guardó la factura "+invoiceToSave.toString(), Toast.LENGTH_LONG)
                    .show();

            //Posiblemente intentar guardar remotamente y luego  imprimir la factura
            //setResult(RESULT_OK);
            //finish();
        }else{
            Snackbar.make(btnCompletePayment, "No se guardó la factura", Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveInvoice();
                        }
                    }).show();
        }
    }


    public static class InvoiceTypeAdapter extends ArrayAdapter<InvoiceType> implements ListAdapter {

        public InvoiceTypeAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }


        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }


        private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            InvoiceType payment = getItem(position);

            convertView = super.getDropDownView(position, convertView, parent);

            TextView txtView = convertView.findViewById(android.R.id.text1);
            String stringResource;


            switch (payment.getInvoicePayment()){
                case CASH:
                    stringResource =getContext().getString( R.string.cash_type);
                    break;

                case CREDIT:
                    stringResource = getContext().getString(R.string.credit_type);
                    break;

                default:
                    stringResource = getContext().getString(R.string.empty_string);
                    break;
            }


            txtView.setText(stringResource);

            return convertView;
        }
    }


}
