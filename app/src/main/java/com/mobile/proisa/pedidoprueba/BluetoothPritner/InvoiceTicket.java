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
                        NumberUtils.formatNumber(item.getQuantity(), NumberUtils.FORMAT_NUMER_INTEGER),
                        NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE),
                        NumberUtils.formatNumber(item.getTaxes(), NumberUtils.FORMAT_NUMER_DOUBLE));



                String totalItem = String.format("%s", NumberUtils.formatNumber(item.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));

                buffer.append("{reset}" + concatWithSpaces(quantityByPrice, totalItem, PRINTER_CHARACTERS_LINES));
            }
        }
        buffer.append(divisorString + "{reset}{b}");


        String totalLabel = "ITBIS GRAVADO:";
        String totalFormatted =  NumberUtils.formatNumber(mInvoice.getTotalTaxes(), NumberUtils.FORMAT_NUMER_DOUBLE);
        buffer.append(concatWithSpaces(totalLabel, totalFormatted, PRINTER_CHARACTERS_LINES));

        totalLabel = "TOTAL ARTICULOS:";
        totalFormatted = NumberUtils.formatNumber(mInvoice.getItems().size(), NumberUtils.FORMAT_NUMER_INTEGER);
        buffer.append(concatWithSpaces(totalLabel, totalFormatted, PRINTER_CHARACTERS_LINES));

        totalLabel = "NETO A PAGAR:";
        totalFormatted = NumberUtils.formatNumber(mInvoice.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE);
        buffer.append(concatWithSpaces(totalLabel, totalFormatted, PRINTER_CHARACTERS_LINES));

        buffer.append(divisorString);
        buffer.append("{center}{b}¡Gracias por Preferinos!{br}");


        return buffer.toString();
    }

    @Override
    public String getBarcodeInfo() {
        return null;
        //return mInvoice.getId();
    }
}
