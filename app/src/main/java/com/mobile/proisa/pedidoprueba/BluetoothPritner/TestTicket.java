package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import android.util.Log;

import Models.Invoice;
import Models.Item;
import Utils.DateUtils;
import Utils.NumberUtils;

public class TestTicket extends AbstractTicket {
    private static final String TAG = "TestTicket";
    private Invoice mInvoice;
    private int PRINTER_CHARACTERS_LINES = 32;

    public TestTicket(Invoice invoice) {
        this.mInvoice = invoice;
    }

    @Override
    public String onBuildTicket(StringBuilder buffer) {
        buffer.append("{center}{b}PROISA{br}");
        buffer.append("{center}{b}Av. Estrella Sadhala, mod. 101{br}");
        buffer.append("{center}{b}(809-860-8075){br}");
        buffer.append("--------------------------------");
        buffer.append(String.format("{reset}Cliente:%s (%s){br}", mInvoice.getClient().getName(), mInvoice.getClient().getRemoteId()));
        buffer.append(String.format("{reset}Factura:%s{br}",  mInvoice.getId()));
        buffer.append(String.format("{reset}Fecha:%s{br}", DateUtils.formatDate(mInvoice.getDate(), DateUtils.DD_MM_YYYY_hh_mm_AM_PM)));
        //buffer.append("{br}");
        buffer.append("--------------------------------");
        buffer.append("{reset}{b}CANT | PRECIO              TOTAL");
        buffer.append("--------------------------------");

        for(Item item : mInvoice.getItems() ){
            buffer.append(String.format("{reset}%s{br}",  item.getName()));

            String quantityByPrice = String.format("%s * %s", NumberUtils.formatNumber(item.getQuantity(), NumberUtils.FORMAT_NUMER_INTEGER),
                    NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
            String totalItem = String.format("%s",NumberUtils.formatNumber(item.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));

            buffer.append(concatWithSpaces(quantityByPrice, totalItem, PRINTER_CHARACTERS_LINES));
        }
        buffer.append("-------------------------------{br}{reset}{b}");

        String totalLabel     = "TOTAL:";
        String totalFormatted = NumberUtils.formatNumber(mInvoice.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE);
        buffer.append(concatWithSpaces(totalLabel, totalFormatted, PRINTER_CHARACTERS_LINES));
        buffer.append("-------------------------------{br}");
        buffer.append("{center}{b}¡Gracias por Preferinos!{br}");



        return buffer.toString();
    }


}
