package com.mobile.proisa.pedidoprueba.Activities;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.mobile.proisa.pedidoprueba.BluetoothPritner.AbstractTicket;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.BluetoothUtils;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.InvoiceTicket;
import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Dialogs.CashPaymentDialog;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Services.VisitaActivaService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.ColumnsSqlite;
import Models.Diary;
import Models.Invoice;
import Sqlite.DiaryController;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Utils.DateUtils;
import Utils.NumberUtils;

public class PaymentActivity extends PrinterManagmentActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, BluetoothListFragment.OnBluetoothSelectedListener {
    private Spinner spPayment;
    private Button btnCompletePayment;
    private Invoice mInvoice;
    private BroadcastReceiver broadcastReceiver;
    private boolean mVisitActive;

    private Diary mCurrentVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setTitle(R.string.payment);
        mInvoice = getInvoiceToShow();
    }

    @Override
    protected void onBindUI() {
        mVisitActive = false;

        spPayment = findViewById(R.id.spPayment);
        btnCompletePayment = findViewById(R.id.btn_complete_payment);
        btnCompletePayment.setOnClickListener(this);

        loadInvoiceTypes();
        loadData();

        createBroadcastReceiver();
        sendBroadcast(new Intent().setAction(VisitaActivaService.ACTION_IS_VISIT_RUNNING));
    }

    /**
     * Crea un broadcast para recivir datos de la visita activida si la hay.
     */
    private void createBroadcastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent == null? "" : intent.getAction();

                if(VisitaActivaService.ACTION_VISIT_RUNNING.equals(action)){
                    mCurrentVisit = intent.getExtras().getParcelable(VisitaActivaService.EXTRA_VISIT);
                    Toast.makeText(getApplicationContext(), "La visita está corriendo", Toast.LENGTH_SHORT).show();
                }else if(VisitaActivaService.ACTION_VISIT_FINISH.equals(action)){
                    mCurrentVisit = intent.getExtras().getParcelable(VisitaActivaService.EXTRA_VISIT);


                    DiaryController  diaryController = new DiaryController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());

                    if(diaryController.update(mCurrentVisit)){
                        //Posiblemente abrir otra actividad para seguir rellenando datos de la visita
                        Toast.makeText(getApplicationContext(), R.string.visit_finished , Toast.LENGTH_LONG).show();
                    }
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_RUNNING);
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_FINISH);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //cerrar broadcastReceiver
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Carga los tipos de facturas en la base de datos
     */
    private void loadInvoiceTypes() {
        InvoiceTypeAdapter adapter = new InvoiceTypeAdapter(getBaseContext(), R.layout.spinner_item_custom);
        adapter.addAll(getInvoiceTypes());
        spPayment.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spPayment.setOnItemSelectedListener(this);

    }

    private void loadData() {
        TextView txtClient = findViewById(R.id.client_name);
        txtClient.setText(mInvoice.getClient().getName());
        TextView txtTotal = findViewById(R.id.total);

        if (mInvoice.containsItems()) {
            txtTotal.setText(NumberUtils.formatNumber(mInvoice.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));
        } else {
            txtTotal.setText(NumberUtils.formatNumber(0.00, NumberUtils.FORMAT_NUMER_DOUBLE));
        }
    }

    /**
     * Obtiene la factura a mostrar en esta actividad
     * @return
     */
    private Invoice getInvoiceToShow() {
        Intent intent = getIntent();

        try {
            Bundle extras = intent.getExtras();
            return extras.getParcelable(EXTRA_INVOICE);
        } catch (Exception e) {
            finish();
        }

        return null;
    }

    /**
     * Devuelve todos los tipos de facturas
     * @return
     */
    private List<InvoiceType> getInvoiceTypes() {
        List<InvoiceType> invoiceTypes = new ArrayList<>();

        for (Invoice.InvoicePayment invoicePayment : Invoice.InvoicePayment.values()) {
            invoiceTypes.add(new InvoiceType(invoicePayment));
        }

        return invoiceTypes;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        InvoiceType invoiceType = (InvoiceType) adapterView.getItemAtPosition(i);
        mInvoice.setInvoiceType(invoiceType.getInvoicePayment());
        Log.d("InvoiceToSave", mInvoice.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete_payment:
                if (mInvoice.getInvoiceType().equals(Invoice.InvoicePayment.CASH)) {
                    showDialogToReturnMoney(mInvoice);
                } else {
                    saveInvoice();
                }
                break;

        }
    }

    /**
     * Detiene el servicio de la visita si hay una
     */
    private void stopVisitService(){
        if(mVisitActive){
            Intent intent = new Intent(this, VisitaActivaService.class);
            stopService(intent);
        }
    }

    /**
     * Muestra un dialog para introducir el dinero que se recibe
     * @param invoiceToSave
     */
    private void showDialogToReturnMoney(Invoice invoiceToSave) {
        CashPaymentDialog.newInstance(invoiceToSave, new CashPaymentDialog.OnPaymentComplete() {
            @Override
            public void onPaymentComplete(boolean success, double money) {
                if (success) {
                    saveInvoice();
                }
            }
        }).show(getSupportFragmentManager(), null);
    }

    /**
     * Devuelve una factura lista para guardar
     * @return
     */
    private Invoice getReadyInvoice() {
        TextInputEditText edtComment = findViewById(R.id.comment);
        mInvoice.setComment(edtComment.getText().toString());
        mInvoice.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);
        mInvoice.setDate(Calendar.getInstance().getTime());
        mInvoice.setId(String.valueOf(mInvoice.hashCode()));

        return mInvoice;
    }

    /**
     * Guarda la factura actual de la actividad
     */
    public void saveInvoice() {
        mInvoice = getReadyInvoice();
        InvoiceController controller = new InvoiceController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

        if (controller.insert(mInvoice)) {
            /**
             * LLegado a este punto intentar guardar remotamente la factura y luego volver a esta actividad y salir
             */
            stopVisitService();
            setResult(RESULT_OK);
            finish();
        } else {
            Snackbar.make(btnCompletePayment, "No se guardó la factura", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.badStatus)).setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveInvoice();
                }
            }).show();
        }
    }

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {

        Toast.makeText(this, "Seleccion: "+device.getName(), Toast.LENGTH_SHORT).show();
        establishConnectionWithPrinter(device);
    }

    @Override
    public void onPrinterConnected() {
        super.onPrinterConnected();

        AbstractTicket ticket = new InvoiceTicket(mInvoice, VentaActivity.VendorUtil.getVendor(this));
        sendTicketToPrint(ticket);
    }

    @Override
    public void onPrintingFinished() {
        super.onPrintingFinished();
        closeConnection();
        setResult(RESULT_OK);
        finish();
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


        private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            InvoiceType payment = getItem(position);

            convertView = super.getDropDownView(position, convertView, parent);

            TextView txtView = convertView.findViewById(android.R.id.text1);
            String stringResource;


            switch (payment.getInvoicePayment()) {
                case CASH:
                    stringResource = getContext().getString(R.string.cash_type);
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
