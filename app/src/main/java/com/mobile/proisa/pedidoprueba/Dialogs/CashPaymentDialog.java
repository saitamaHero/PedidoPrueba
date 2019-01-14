package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import Models.Invoice;

public class CashPaymentDialog extends DialogFragment implements TextWatcher, DialogInterface.OnClickListener {
    private static final String PARAM_INVOICE = "param_invoice";

    private TextView txtDevuelta;
    private TextInputEditText edtMoney;

    private double money;
    private Invoice invoice;


    private OnPaymentComplete onPaymentComplete;

    public static CashPaymentDialog newInstance(Invoice invoice, OnPaymentComplete onPaymentComplete) {
        Bundle args = new Bundle();
        args.putParcelable(PARAM_INVOICE, invoice);
        CashPaymentDialog fragment = new CashPaymentDialog();
        fragment.setOnPaymentComplete(onPaymentComplete);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPaymentComplete(OnPaymentComplete onPaymentComplete) {
        this.onPaymentComplete = onPaymentComplete;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            invoice = getArguments().getParcelable(PARAM_INVOICE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cash_payment_dialog, null);

        builder.setTitle(R.string.msg_cash_pay);
        builder.setView(view);
        edtMoney = view.findViewById(R.id.edt_money);
        edtMoney.addTextChangedListener(this);

        txtDevuelta = view.findViewById(R.id.devuelta);

        builder.setPositiveButton(R.string.ok, this);

        return builder.create();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String m = editable.toString();

        if(!TextUtils.isEmpty(m)) {
            money = Double.parseDouble(m);
        }else{
            money = 0.0;
        }

        showDevuelta(money);
    }

    private void showDevuelta(double money) {
        double total = invoice.getTotal();
        double devuelta = 0.0;

        if(isPaymentComplete(money, total)) {
            devuelta = money - total;
        }

        txtDevuelta.setText(NumberUtils.formatNumber(devuelta, NumberUtils.FORMAT_NUMER_DOUBLE));
    }

    public boolean isPaymentComplete(double money, double total){
        return money > total;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                //Call listener to send the money
                if(onPaymentComplete != null && isPaymentComplete(money, invoice.getTotal())){
                    onPaymentComplete.onPaymentComplete(true, money);
                }
                break;
        }
    }


    public interface OnPaymentComplete{
        void onPaymentComplete(boolean success, double money);
    }
}
