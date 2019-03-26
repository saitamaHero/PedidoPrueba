package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import android.util.Log;

import com.datecs.api.barcode.Barcode;

import Models.Invoice;
import Models.Item;
import Models.Vendor;
import Utils.DateUtils;
import Utils.NumberUtils;

/**
 * Clase que exitiende de {@link AbstractTicket}
 */
public class InvoiceTicket extends AbstractTicket {
    private static final String TAG = "InvoiceTicket";
    private Invoice mInvoice;
    private Vendor mVendor;
    private int PRINTER_CHARACTERS_LINES = 32;

    public InvoiceTicket(Invoice invoice, Vendor vendor) {
        this.mInvoice = invoice;
        this.mVendor = vendor;
    }

    @Override
    public String onBuildTicket(StringBuilder buffer) {
        String divisorString = getStringWithCharacter('-', PRINTER_CHARACTERS_LINES);
        buffer.append("{center}{b}PROISA{br}");
        buffer.append("{center}{b}Av. Estrella Sadhala, mod. 101{br}");
        buffer.append("{center}{b}(809-860-8075){br}");
        buffer.append(divisorString);
        buffer.append(String.format("{reset}{center}{b}%s{br}", mInvoice.isCash() ? "VENTA DE CONTADO" : "VENTA A CREDITO"));
        buffer.append(divisorString);
        buffer.append(String.format("{reset}{b}CLIENTE:{reset}%s (%s){br}", mInvoice.getClient().getName(), mInvoice.getClient().getRemoteId()));
        buffer.append(String.format("{reset}{b}NCF:{reset}%s{br}",          mInvoice.getNcfSequence()));
        buffer.append(String.format("{reset}{b}FACTURA:{reset}%s{br}",      mInvoice.getRemoteId()));
        buffer.append(String.format("{reset}{b}FECHA:{reset}%s{br}",        DateUtils.formatDate(mInvoice.getDate(), DateUtils.DD_MM_YYYY_hh_mm_AM_PM)));
        buffer.append(String.format("{reset}{b}OPERADOR:{reset}%s{br}",     mVendor.getName()));
        buffer.append(divisorString);

        buffer.append("{reset}{b}DESCRIPCION{br}");
        buffer.append("{reset}{b}CANT.   PRECIO    ITBIS    TOTAL");
        buffer.append(divisorString);

        for (Item item : mInvoice.getItems()) {
            if(item != null) {
                String tag = item.isFreeTaxes() ? Item.FREE_TAXES : Item.INCLUDE_TAXES;

                String description  = concatWithSpaces(item.getName(),tag, PRINTER_CHARACTERS_LINES);

                buffer.append(String.format("{reset}{b}%s{br}", description));

                String quantityByPrice = String.format("%-8s%6s    %s",
                        NumberUtils.formatToInteger(item.getQuantity()),
                        NumberUtils.formatToDouble(item.getPrice()),
                        NumberUtils.formatToDouble(item.getTaxes()));



                String totalItem = String.format("%s", NumberUtils.formatToDouble(item.getTotal()));

                buffer.append("{reset}" + concatWithSpaces(quantityByPrice, totalItem, PRINTER_CHARACTERS_LINES));
            }
        }
        buffer.append(divisorString + "{reset}{b}");


        buffer.append(createTotal("TOTAL ARTICULOS:",   NumberUtils.formatToInteger(mInvoice.getItems().size()) ));

        if(mInvoice.hasDiscount() ){
            buffer.append(createTotal("DESCUENTO",
                    NumberUtils.formatToInteger(mInvoice.getDiscount() * 100.0) + "%"));
        }


        buffer.append(createTotal("MONTO BRUTO:",       NumberUtils.formatToDouble(mInvoice.getTotalFreeTaxes())));
        buffer.append(createTotal("ITBIS GRAVADO:",     NumberUtils.formatToDouble(mInvoice.getTotalTaxes())));
        buffer.append(createTotal("NETO A PAGAR:",      NumberUtils.formatToDouble(mInvoice.getTotal())));

        buffer.append(getStringWithCharacter('_',PRINTER_CHARACTERS_LINES));

        if(mInvoice.isCash()){
            buffer.append("{reset}{b}EFECTIVO:{br}");
            buffer.append("{reset}"+ concatWithSpaces("RECIBIDO:", NumberUtils.formatToDouble(mInvoice.getMoneyReceived()), PRINTER_CHARACTERS_LINES));
            buffer.append("{reset}"+ concatWithSpaces("DEVUELTA:", NumberUtils.formatToDouble(mInvoice.getMoneyReceived() - mInvoice.getTotal()), PRINTER_CHARACTERS_LINES));
        }

        buffer.append(getStringWithCharacter('_',PRINTER_CHARACTERS_LINES));

        buffer.append("{reset}{center}{b}¡Gracias por Preferinos!{br}");

        return buffer.toString();
    }

    private String createTotal(String label, String totalFormatted){
        return concatWithSpaces(label, totalFormatted, PRINTER_CHARACTERS_LINES);
    }

    @Override
    public String getBarcodeInfo() {
        return null;
        //return mInvoice.getId();
    }
}
