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


}
