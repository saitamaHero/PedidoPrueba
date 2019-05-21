package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import Models.Item;
import Utils.DateUtils;
import Utils.NumberUtils;

public class ItemTicket extends AbstractTicket {
    private Item mItem;

    public ItemTicket(Item item) {
        this.mItem = item;
    }

    @Override
    public String getBarcodeInfo() {
        return null;
    }

    @Override
    public String onBuildTicket(StringBuilder buffer) {
        String divisor = getStringWithCharacter('.',32);

        buffer.append(divisor);
        buffer.append(String.format("{reset}{b}Artículo:{reset}%s (%s){br}",mItem.getName(), mItem.getId()));
        buffer.append(String.format("{reset}{b}Precio:{reset}%s{br}", NumberUtils.formatNumber(mItem.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE)));
        buffer.append(String.format("{reset}{b}Unidad:{reset}%s{br}", mItem.getUnit().getId()));
        buffer.append(String.format("{reset}{b}Departamento:{reset}%s{br}", mItem.getCategory().getName()));
        buffer.append(String.format("{reset}{b}Cantidad:{reset}%s{br}", NumberUtils.formatNumber(mItem.getQuantity(), NumberUtils.FORMAT_NUMER_INTEGER)));
        buffer.append(divisor);


        return buffer.toString();
    }
}
