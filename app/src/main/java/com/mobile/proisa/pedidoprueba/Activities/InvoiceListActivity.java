package com.mobile.proisa.pedidoprueba.Activities;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.AbstractTicket;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.InvoiceListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.TextMessageFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import com.mobile.proisa.pedidoprueba.BluetoothPritner.BluetoothUtils;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.MainPrinterHandler;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.PrinterHandler;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.TestTicket;

import Models.Client;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends BaseCompatAcivity implements InvoiceListAdapter.OnInvoiceClickListener, BluetoothListFragment.OnBluetoothSelectedListener, MainPrinterHandler.PrinterCallBack {
    private static final String TAG = "InvoiceListActivity";

    private InvoiceController invoiceController;


    private PrinterHandler mPrinterHandler;
    private MainPrinterHandler mMainPrinterHandler;
    private AbstractTicket ticket;
    private BluetoothDevice mBluetoohSelected;
    private HandlerThread mHandlerThread;
    private boolean mPrinterIsStillConnected;


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
        /*startActivity(new Intent(this, VentaActivity.class)
                .putExtra(BaseCompatAcivity.EXTRA_INVOICE, invoice)
                .putExtra(BaseCompatAcivity.EXTRA_IS_NEW_INVOICE, false)
        );*/

        ticket = new TestTicket(invoice, VentaActivity.VendorUtil.getVendor(this));

        if(!isPrinterSelected()){
                BluetoothListFragment.newInstance(BluetoothUtils.getPrintersBluetooth()).show(getSupportFragmentManager(), "");
        }else if(mPrinterIsStillConnected){
            sendMessageToPrint(ticket.getTicket());
        }else{
            connectToPrinter(mBluetoohSelected);
        }

    }

    private void setPrinterStatus(String status){
        TextView txtStatus = findViewById(R.id.txt_status);
        txtStatus.setText(status);
    }

    private boolean isPrinterSelected(){
        return this.mBluetoohSelected != null;
    }

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        mBluetoohSelected = device;

        mMainPrinterHandler = new MainPrinterHandler(this);

        mHandlerThread = new HandlerThread("PrinterHandler");
        mHandlerThread.start();

        mPrinterHandler = new PrinterHandler(mHandlerThread.getLooper());
        mPrinterHandler.setMainThread(mMainPrinterHandler);

        connectToPrinter(mBluetoohSelected);
    }

    private void connectToPrinter(BluetoothDevice device){
        Message msg = new Message();
        msg.what = PrinterHandler.REQUEST_CONNECTION;
        msg.obj  = device;
        mPrinterHandler.sendMessage(msg);
    }

    private void sendMessageToPrint(String toPrint){
        Message message = new Message();
        message.what = PrinterHandler.PRINTER_PRINT_TEXT_TAGGED;
        message.obj = toPrint;

        mPrinterHandler.sendMessage(message);
    }

    @Override
    public void onPrinterConnecting(BluetoothDevice bluetoothDevice) {
        setPrinterStatus("Intentando conectar a "+bluetoothDevice.getName());
    }

    @Override
    public void onPrinterConnected() {
        mPrinterIsStillConnected = true;
        sendMessageToPrint(ticket.getTicket());
        setPrinterStatus("Impresora conectada");
    }

    @Override
    public void onPrinting() {
        setPrinterStatus("Imprimiendo");
    }

    @Override
    public void onPrintingFinished() {
        setPrinterStatus("Impresora conectada");
        Toast.makeText(getApplicationContext(), "Impresion Terminada!", Toast.LENGTH_SHORT).show();
        //mPrinterHandler.sendEmptyMessage(PrinterHandler.PRINTER_CLOSE_CONNECTION);
    }

    @Override
    public void onPrinterDisconnected() {
        mBluetoohSelected = null;
        mPrinterIsStillConnected = false;
        setPrinterStatus("Impresora Desconectada");
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        if(isPrinterSelected()){
            this.mBluetoohSelected = null;
        }
        setPrinterStatus(String.format("La impresora %s no fue encontrada", bluetoothDevice.getName()));
        Toast.makeText(getApplicationContext(), "Por favor verifica que la impresora esté encendida", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mPrinterHandler != null)
            mPrinterHandler.sendEmptyMessage(PrinterHandler.PRINTER_CLOSE_CONNECTION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mHandlerThread != null){
            mHandlerThread.quit();
        }
    }


}
