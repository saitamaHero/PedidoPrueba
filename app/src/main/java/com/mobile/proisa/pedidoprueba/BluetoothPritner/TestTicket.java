package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import Models.Invoice;
import Models.Item;
import Utils.DateUtils;

public class TestTicket extends AbstractTicket {

    private Invoice mInvoice;

    public TestTicket(Invoice invoice) {
        this.mInvoice = invoice;
    }

    @Override
    public String onBuildTicket(StringBuilder buffer) {
        buffer.append("{center}{b}PROISA SOFT{br}");
        buffer.append("{center}{b}Av. Estrella Sadhala, mod. 101{br}");
        buffer.append("{center}{b}(809-860-8075){br}");
        buffer.append("-------------------------------{br}");
        buffer.append(String.format("{reset}Cliente:%s (%s){br}", mInvoice.getClient().getName(), mInvoice.getClient().getRemoteId()));
        buffer.append(String.format("{reset}Factura:%s{br}",  mInvoice.getId()));
        buffer.append(String.format("{reset}F & H:%s{br}", DateUtils.formatDate(mInvoice.getDate(), DateUtils.EEE_DD_MMM_YYYY_HH_mm)));
        buffer.append("-------------------------------{br}");


        for(Item item : mInvoice.getItems() ){
            buffer.append(String.format("{reset}%s{br}",  item.getName()));
            buffer.append(String.format("{reset}{right}%.2f   %.2f   ",item.getQuantity(), item.getPrice()));
            buffer.append(String.format("%.2f{br}", item.getTotal()));
        }

        buffer.append("-------------------------------{br}");
        buffer.append(String.format("{reset}{right}Total de compra: %.2f{br}", mInvoice.getTotal()));
        buffer.append("-------------------------------{br}");
        buffer.append("{center}{b}¡Gracias por su Compra!{br}");

        return buffer.toString();
    }
}
