package com.mobile.proisa.pedidoprueba.Activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.BluetoothPritner.BluetoothUtils;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.InvoiceTicket;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Dialogs.TotalInvoiceDialog;
import com.mobile.proisa.pedidoprueba.R;

import Models.Client;
import Models.Invoice;
import Sqlite.CompanyController;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;

public class InvoiceDetailsActivity extends PrinterManagmentActivity implements View.OnClickListener, BluetoothListFragment.OnBluetoothSelectedListener{
    private InvoiceTicket currentTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        setTitle(R.string.invoice_details);

    }

    @Override
    protected void onBindUI() {
        super.onBindUI();

        loadInvoice();
    }

    private void loadInvoice() {
        Invoice invoice = getInvoice();

        String code = TextUtils.isEmpty(String.valueOf(invoice.getRemoteId())) ? getString(R.string.not_code) : String.valueOf(invoice.getRemoteId());
        TextView txtCode = findViewById(R.id.code);
        txtCode.setText(code);

        TextView txtTotal = findViewById(R.id.total);
        txtTotal.setText(NumberUtils.formatToDouble(invoice.getTotal()));


        TextView txtTypePayment = findViewById(R.id.payment_type);
        txtTypePayment.setText(invoice.isCash() ? getString(R.string.cash_type).toUpperCase() : getString(R.string.credit_type).toUpperCase());

        Client client  = invoice.getClient();
        TextView txtClientName = findViewById(R.id.client_name);
        txtClientName.setText(client.getName());


        View viewTotal = findViewById(R.id.viewTotal);
        viewTotal.setOnClickListener(this);
    }

    public Invoice getInvoice() {
        Intent intent = getIntent();

        if(intent == null){
            return null;
        }

        Bundle extras = intent.getExtras();

        if(extras.containsKey(EXTRA_INVOICE)){
            return extras.getParcelable(EXTRA_INVOICE);
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_details_invoice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_printer:
                printInvoice(getInvoice());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.viewTotal:
                TotalInvoiceDialog.newInstance(getInvoice()).show(getSupportFragmentManager(), "");
                break;
        }
    }

    private void printInvoice(Invoice invoice){
        currentTicket = new InvoiceTicket(invoice, VentaActivity.VendorUtil.getVendor(this), CompanyController.getCompany(MySqliteOpenHelper.getInstance(this).getReadableDatabase()));

        if(!isPrinterSelected()){
            BluetoothListFragment.newInstance(BluetoothUtils.getPrintersBluetooth()).show(getSupportFragmentManager(), "");
        }else if(isPrinterStillConnected()){
            sendTicketToPrint(currentTicket);
        }
    }


    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        establishConnectionWithPrinter(device);
    }

    @Override
    public void onPrinterConnecting(BluetoothDevice bluetoothDevice) {
        super.onPrinterConnecting(bluetoothDevice);

        Toast.makeText(this,getString( R.string.printer_connecting, bluetoothDevice.getName()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrinterConnected() {
        super.onPrinterConnected();
        sendTicketToPrint(currentTicket);
    }

    @Override
    public void onPrinting() {
        super.onPrinting();
        Toast.makeText(this, R.string.printing_ticket, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrinterDisconnected() {
        super.onPrinterDisconnected();
        Toast.makeText(this, R.string.printer_disconnected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        super.onPrinterNotFound(bluetoothDevice);
        Toast.makeText(getApplicationContext(), R.string.check_printer, Toast.LENGTH_LONG).show();
    }
}
