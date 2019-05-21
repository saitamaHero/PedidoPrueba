package com.mobile.proisa.pedidoprueba.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.mobile.proisa.pedidoprueba.Receivers.DiaryBroadcastReceiver;
import com.mobile.proisa.pedidoprueba.Services.VisitaActivaService;
import com.mobile.proisa.pedidoprueba.Tasks.DialogInTask;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import BaseDeDatos.InvoiceUpdater;
import BaseDeDatos.SqlConnection;
import BaseDeDatos.SqlUpdater;
import Models.ColumnsSqlite;
import Models.Diary;
import Models.Invoice;
import Sqlite.DiaryController;
import Sqlite.InvoiceController;
import Sqlite.InvoiceDiaryController;
import Sqlite.MySqliteOpenHelper;
import Utils.DateUtils;
import Utils.NumberUtils;

public class PaymentActivity extends BaseCompatAcivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,  TareaAsincrona.OnFinishedProcess, DiaryBroadcastReceiver.OnDiaryStateListener {
    private static final String TAG = "PaymentActivity";

    private Spinner spPayment;
    private Button btnCompletePayment;
    private Invoice mInvoice;
    private BroadcastReceiver broadcastReceiver;
    private Diary mVisitActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setTitle(R.string.payment);

        mInvoice = getInvoiceToShow();
    }

    @Override
    protected void onBindUI() {

        spPayment = findViewById(R.id.spPayment);
        btnCompletePayment = findViewById(R.id.btn_complete_payment);
        btnCompletePayment.setOnClickListener(this);

        loadInvoiceTypes();
        loadData();

        createBroadcastReceiver();
        sendBroadcast(new Intent().setAction(VisitaActivaService.ACTION_IS_VISIT_RUNNING));
    }

    /**
     * Crea un broadcast para recivir datos de la visita activa si la hay.
     */
    private void createBroadcastReceiver(){
        broadcastReceiver = new DiaryBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_RUNNING);
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_FINISH);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onVisitStatusChanged(int status, Diary diary) {
        switch (status){
            case DiaryBroadcastReceiver.OnDiaryStateListener.VISIT_RUNNING:
                Toast.makeText(getApplicationContext(), R.string.visit_running, Toast.LENGTH_SHORT).show();
                this.mVisitActive = diary;
                break;

            case DiaryBroadcastReceiver.OnDiaryStateListener.VISIT_FINISH:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            txtTotal.setText(NumberUtils.formatToDouble(mInvoice.getTotal()));
        } else {
            txtTotal.setText(NumberUtils.formatToDouble(0.00));
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
        //if(mVisitActive){
            Intent intent = new Intent(this, VisitaActivaService.class);
            stopService(intent);
        //}
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
                    mInvoice.setMoneyReceived(money);
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
        SQLiteDatabase database = MySqliteOpenHelper.getInstance(this).getWritableDatabase();
        InvoiceController controller = new InvoiceController(database);

        if(TextUtils.isEmpty(mInvoice.getId())){
            mInvoice = getReadyInvoice();
        }


        if (!controller.exists(Invoice._ID, mInvoice.getId())) {
            /**
             * LLegado a este punto intentar guardar remotamente la factura y luego volver a esta actividad y salir
             */

            if(controller.insert(mInvoice)) {
                if(this.mVisitActive != null){
                    List<Invoice> invoices = new ArrayList<>();
                    invoices.add(mInvoice);

                    InvoiceDiaryController invoiceDiaryController = new InvoiceDiaryController(database);
                    if(invoiceDiaryController.insertAllWithId(invoices, this.mVisitActive.getId())){
                        //Toast.makeText(this, "Se guardo la factura en VistasFacturas", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "saveInvoice: Se guardo la factura en VistasFacturas");
                    }
                }


                new SaveInvoiceTask(0, this, this, true).execute(mInvoice);
            }else {
                Snackbar.make(btnCompletePayment, "No se guardó la factura", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.badStatus)).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveInvoice();
                    }
                }).show();
            }
        }

    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            switch (task.getId()){
                case 0:
                    Invoice invoice = task.getData().getParcelable(EXTRA_INVOICE);
                    startActivityForResult(new Intent(this, InvoiceDetailsActivity.class).putExtra(EXTRA_INVOICE, invoice), REQUEST_CODE_INVOICE_DETAILS);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_INVOICE_DETAILS:
                finish();
                break;
        }
    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Toast.makeText(this, R.string.invoice_not_save, Toast.LENGTH_LONG).show();


        //startActivityForResult(new Intent(this, InvoiceDetailsActivity.class).putExtra(EXTRA_INVOICE, mInvoice), REQUEST_CODE_INVOICE_DETAILS);


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


    private class SaveInvoiceTask extends DialogInTask<Invoice, String, Void > implements SqlUpdater.OnDataUpdateListener<Invoice>, SqlUpdater.OnErrorListener {

        public SaveInvoiceTask(int id, Activity context, OnFinishedProcess listener, boolean mDialogShow) {
            super(id, context, listener, mDialogShow);
        }

        @Override
        protected Void doInBackground(Invoice... invoices) {
            if(invoices == null || invoices.length  == 0){
                return null;
            }

            Invoice invoiceToSave =  invoices[0];

            publishProgress(getContext().getString(R.string.starting));

            SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());
            connection.connect();

            InvoiceUpdater invoiceUpdater = new InvoiceUpdater(getContext(), connection,
                                       new InvoiceController(MySqliteOpenHelper.getInstance(getContext()).getWritableDatabase()));


            invoiceUpdater.addData(invoiceToSave);

            invoiceUpdater.setOnDataUpdateListener(this);
            invoiceUpdater.setOnErrorListener(this);

            invoiceUpdater.apply();

            try {
                connection.getSqlConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        public void onDataUpdate(Invoice data, int action) {
            publishProgress("Guardando Factura");
        }

        @Override
        public void onDataUpdated(Invoice data) {
            getData().putParcelable(EXTRA_INVOICE, data);
        }

        @Override
        public void onError(int error) {
            publishError(new Exception("Error on SaveInvoiceTask #" +error));
        }
    }
}
