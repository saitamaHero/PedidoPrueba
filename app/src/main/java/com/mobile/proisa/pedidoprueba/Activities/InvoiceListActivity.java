package com.mobile.proisa.pedidoprueba.Activities;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
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
import com.mobile.proisa.pedidoprueba.BluetoothPritner.Ticket;
import Models.Client;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;

public class InvoiceListActivity extends BaseCompatAcivity implements InvoiceListAdapter.OnInvoiceClickListener, BluetoothListFragment.OnBluetoothSelectedListener, MainPrinterHandler.PrinterCallBack {
    private static final String TAG = "InvoiceListActivity";

    private InvoiceController invoiceController;


    private PrinterHandler printerHandler;
    private MainPrinterHandler mainPrinterHandler;
    private AbstractTicket ticket;

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

        BluetoothListFragment.newInstance(BluetoothUtils.getPrintersBluetooth()).show(getSupportFragmentManager(), "");

        ticket = new TestTicket(invoice);
    }

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        mainPrinterHandler = new MainPrinterHandler(this);

        HandlerThread handlerThread = new HandlerThread("PrinterHandler");
        handlerThread.start();

        printerHandler = new PrinterHandler(handlerThread.getLooper());
        printerHandler.setMainThread(mainPrinterHandler);


        Message msg = new Message();
        msg.what = PrinterHandler.REQUEST_CONNECTION;
        msg.obj  = device;
        printerHandler.sendMessage(msg);
    }

    @Override
    public void onPrinterConnected() {
        Message message = new Message();
        message.what = PrinterHandler.PRINTER_PRINT_TEXT_TAGGED;
        message.obj = ticket.getTicket();

        printerHandler.sendMessage(message);
    }

    @Override
    public void onPrinterDisconnected() {
        Log.d(TAG, "Impresora desconectada");
    }

    @Override
    protected void onPause() {
        super.onPause();

        printerHandler.sendEmptyMessage(PrinterHandler.PRINTER_CLOSE_CONNECTION);
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        Toast.makeText(getApplicationContext(), "Por favor verifica que la impresora esté encendida", Toast.LENGTH_SHORT);
    }
}
