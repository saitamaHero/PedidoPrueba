package com.mobile.proisa.pedidoprueba.BluetoothPritner;

import com.datecs.api.barcode.Barcode;
import com.datecs.api.printer.Printer;

public abstract class AbstractTicket implements Ticket {
    private StringBuilder mStringBuilder;

    public AbstractTicket() {
        mStringBuilder = new StringBuilder();
    }

    public abstract String getBarcodeInfo();

    /**
     * Devuelve un string con el sticker formado
     * @param buffer
     */
    public abstract String onBuildTicket(StringBuilder buffer);

    @Override
    public String getTicket() {
        return onBuildTicket(mStringBuilder);
    }

    /**
     * Método que utiliza {@link AbstractTicket#getStringWithCharacter}
     * @param nSpaces espacios requeridos
     * @return
     */
    public String getStringWithSpaces(int nSpaces) {
        return getStringWithCharacter(' ', nSpaces);
    }

    public String getStringWithCharacter(char c, int nTimes){
        if(nTimes <=  0){
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < nTimes; i++){
            builder.append(c);
        }


        return builder.toString();
    }

    public String concatWithSpaces(String prefix, String suffix, int maxByLine){
        if(prefix == null || suffix == null || maxByLine <= 0){
            return "";
        }else{
            String formatted = "";

            int spacesRequired = maxByLine - (prefix.length() + suffix.length());

            if(spacesRequired <= 0){
                formatted = prefix + suffix;
            }else{
                formatted = prefix + getStringWithSpaces(spacesRequired) + suffix;
            }

            return formatted;
        }
    }
}
