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
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends PrinterManagmentActivity implements InvoiceListAdapter.OnInvoiceClickListener,
        BluetoothListFragment.OnBluetoothSelectedListener{
    private static final String TAG = "InvoiceListActivity";
    private InvoiceController invoiceController;

    private AbstractTicket ticket;
    private ProgressDialog progressDialog;

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
        ticket = new InvoiceTicket(invoice, VentaActivity.VendorUtil.getVendor(this));

        if(!isPrinterSelected()){
                BluetoothListFragment.newInstance(BluetoothUtils.getPrintersBluetooth()).show(getSupportFragmentManager(), "");
        }else if(isPrinterStillConnected()){
            //sendMessageToPrint(ticket.getTicket());
            sendTicketToPrint(ticket);
        }
    }

    private void setPrinterStatus(String status, boolean showDialog){

        if(showDialog && progressDialog == null){
            progressDialog = ProgressDialog.newInstance(status);
            progressDialog.show(getFragmentManager(), "");
        }else if(showDialog){
            progressDialog.changeInfo(status);
        }else if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;

            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        establishConnectionWithPrinter(device);
    }


    @Override
    public void onPrinterConnecting(BluetoothDevice bluetoothDevice) {
        setPrinterStatus("Intentando Conectar a "+bluetoothDevice.getName(), true);
    }

    @Override
    public void onPrinterConnected() {
        super.onPrinterConnected();

        setPrinterStatus("Impresora conectada", true);
        sendTicketToPrint(ticket);
    }

    @Override
    public void onPrinting() {
        super.onPrinting();
        setPrinterStatus("Imprimiendo", false);
    }

    @Override
    public void onPrinterDisconnected() {
        super.onPrinterDisconnected();
        setPrinterStatus("Impresora Desconectada", false);
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        super.onPrinterNotFound(bluetoothDevice);
        Toast.makeText(getApplicationContext(), "Por favor verifica que la impresora esté encendida", Toast.LENGTH_SHORT).show();
    }
}