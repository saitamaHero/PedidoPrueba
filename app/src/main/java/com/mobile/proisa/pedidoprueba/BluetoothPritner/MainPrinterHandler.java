package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;

public class MainPrinterHandler extends Handler {

    private PrinterCallBack printerCallBack;

    public MainPrinterHandler(PrinterCallBack printerCallBack) {
        if(printerCallBack == null){
            throw new NullPointerException(PrinterCallBack.class.getSimpleName() + " is null");
        }
        this.printerCallBack = printerCallBack;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case PrinterHandler.PRINTER_CONNECTED:
                printerCallBack.onPrinterConnected();
                break;

            case PrinterHandler.PRINTER_DISCCONECTED:
                printerCallBack.onPrinterDisconnected();
                break;

            case PrinterHandler.PRINTER_NOT_FOUND:
                if(msg.obj instanceof BluetoothDevice){
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    printerCallBack.onPrinterNotFound(bluetoothDevice);
                }
                break;
            case PrinterHandler.PRINTER_FINISH_PRINT:
                printerCallBack.onPrintingFinished();

                break;

            case PrinterHandler.PRINTER_PRINTING:
                printerCallBack.onPrinting();

                break;
        }


    }

    public interface PrinterCallBack {
        public void onPrinterConnected();
        public void onPrinterDisconnected();
        public void onPrinting();
        public void onPrintingFinished();
        public void onPrinterNotFound(BluetoothDevice bluetoothDevice);
    }
}
