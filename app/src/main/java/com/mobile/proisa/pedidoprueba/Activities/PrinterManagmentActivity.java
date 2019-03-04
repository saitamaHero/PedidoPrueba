package com.mobile.proisa.pedidoprueba.Activities;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.mobile.proisa.pedidoprueba.BluetoothPritner.AbstractTicket;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.MainPrinterHandler;
import com.mobile.proisa.pedidoprueba.BluetoothPritner.PrinterHandler;

public class PrinterManagmentActivity extends BaseCompatAcivity implements MainPrinterHandler.PrinterCallBack {
    private static final String TAG = "PrinterManagmentActivit";

    /**
     * Maneja los eventos que requieren operaciones de larga duración
     */
    private PrinterHandler mPrinterHandler;

    /**
     * Maneja los callback de los estados de la impresora, está siendo ejecutado en el hilo principal
     */
    private MainPrinterHandler mMainPrinterHandler;

    /**
     * Bluetooth seleccionado, sino es encontrado {@link PrinterManagmentActivity#isPrinterSelected()} devolverá false
     */
    private BluetoothDevice mBluetoohSelected;

    /**
     * HandlerThread creado para manejar los sucesos en segundo plano
     */
    private HandlerThread mHandlerThread;

    /**
     * Indica si la impresora está un conectada
     */
    private boolean mPrinterIsStillConnected;


    /**
     * Establece una conexión con el dispositivo bluetooth deseado, en este caso una impresora bluetooth
     * @param printer seleccionado
     */
    protected void establishConnectionWithPrinter(BluetoothDevice printer){
        Message msg = new Message();
        msg.what = PrinterHandler.REQUEST_CONNECTION;
        msg.obj  = printer;
        mPrinterHandler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mMainPrinterHandler == null || mHandlerThread == null){
            mMainPrinterHandler = new MainPrinterHandler(this);

            mHandlerThread = new HandlerThread("PrinterHandlerThread");
            mHandlerThread.start();

            mPrinterHandler = new PrinterHandler(mHandlerThread.getLooper());
            mPrinterHandler.setMainThread(mMainPrinterHandler);
        }
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

        if(mHandlerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mHandlerThread.quitSafely();
            }else{
                mHandlerThread.quit();
            }
        }
    }

    @Override
    public void onPrinterConnecting(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onPrinterConnecting");

        if(!isPrinterSelected()){
            this.mBluetoohSelected = bluetoothDevice;
        }
    }

    @Override
    public void onPrinterConnected() {
        Log.d(TAG, "onPrinterConnected");

        mPrinterIsStillConnected = true;
    }

    @Override
    public void onPrinterDisconnected() {
        Log.d(TAG, "onPrinterDisconnected");

        mPrinterIsStillConnected = false;
        mBluetoohSelected = null;
    }

    @Override
    public void onPrinting() {
        Log.d(TAG, "onPrinting");
    }

    @Override
    public void onPrintingFinished() {
        Log.d(TAG, "onPrintingFinished");
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onPrinterNotFound");

        if(isPrinterSelected()){
            this.mBluetoohSelected = null;
        }
    }

    public boolean isPrinterStillConnected(){
        return mPrinterIsStillConnected;
    }

    /**
     *
     * @return true si una impresora está aún seleccionada
     */
    public boolean isPrinterSelected(){
        return this.mBluetoohSelected != null;
    }

    public void sendMessageToPrint(String toPrint){
        Message message = new Message();
        message.what = PrinterHandler.PRINTER_PRINT_TEXT_TAGGED;
        message.obj = toPrint;

        mPrinterHandler.sendMessage(message);
    }

    public void sendTicketToPrint(AbstractTicket toPrint){
        Message message = new Message();
        message.what = PrinterHandler.PRINTER_PRINT_TEXT_TAGGED;
        message.obj = toPrint;

        mPrinterHandler.sendMessage(message);
    }

    /**
     * Cierra la conexión con el dispositivo bluetooth conectado, si lo está
     */
    public void closeConnection(){
        if(isPrinterStillConnected()){
            mPrinterHandler.sendEmptyMessage(PrinterHandler.PRINTER_CLOSE_CONNECTION);
        }
    }
}
