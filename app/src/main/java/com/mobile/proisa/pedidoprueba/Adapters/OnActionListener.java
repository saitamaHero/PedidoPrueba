package com.mobile.proisa.pedidoprueba.Adapters;

public interface OnActionListener{
    int ACTION_LESS = -1;
    int ACTION_ADD = 0;
    int ACTION_REMOVED = 1;

    void actionOcurred(int action, int position);
}
