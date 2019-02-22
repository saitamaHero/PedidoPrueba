package com.mobile.proisa.pedidoprueba.BluetoothPritner;

public abstract class AbstractTicket implements Ticket {
    private StringBuilder mStringBuilder;

    public AbstractTicket() {
        mStringBuilder = new StringBuilder();
    }

    /**
     * Devuelve crea un Ticket a partir
     * @param buffer
     */
    public abstract String onBuildTicket(StringBuilder buffer);

    @Override
    public String getTicket() {
        return onBuildTicket(mStringBuilder);
    }


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
