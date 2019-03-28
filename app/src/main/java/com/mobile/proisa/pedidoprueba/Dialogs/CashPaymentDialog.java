package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

import Models.Invoice;
import Utils.NumberUtils;

public class CashPaymentDialog extends DialogFragment implements TextWatcher, DialogInterface.OnClickListener {
    private static final String PARAM_TOTAL = "com.mobile.proisa.pedidoprueba.Dialogs.PARAM_TOTAL";

    private TextView txtAmount;
    private EditText txtDevuelta;
    private EditText edtMoney;

    private double mMoney;
    private double mTotal;


    private OnPaymentComplete onPaymentComplete;

    public static CashPaymentDialog newInstance(Invoice invoice, OnPaymentComplete onPaymentComplete) {
        Bundle args = new Bundle();
        args.putDouble(PARAM_TOTAL, invoice.getTotal());
        CashPaymentDialog fragment = new CashPaymentDialog();
        fragment.setOnPaymentComplete(onPaymentComplete);
        fragment.setArguments(args);
        return fragment;
    }

    public static CashPaymentDialog newInstance(double total, OnPaymentComplete onPaymentComplete) {
        Bundle args = new Bundle();
        args.putDouble(PARAM_TOTAL, total);

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
            mTotal = getArguments().getDouble(PARAM_TOTAL);
        }else {
            mTotal = 0.0;
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

        txtAmount = view.findViewById(R.id.amount);
        txtAmount.setText(NumberUtils.formatNumber(mTotal, NumberUtils.FORMAT_NUMER_DOUBLE));

        builder.setPositiveButton(R.string.ok, this);
        builder.setNeutralButton(R.string.payment_exact, this);

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
            mMoney = Double.parseDouble(m);
        }else{
            mMoney = 0.0;
        }

        showDevuelta(mMoney);
    }

    private void showDevuelta(double money) {
        //double mTotal = invoice.getTotal();
        double devuelta = 0.0;

        if(isPaymentComplete(money, mTotal)) {
            devuelta = money - mTotal;
        }

        txtDevuelta.setText(NumberUtils.formatNumber(devuelta, NumberUtils.FORMAT_NUMER_DOUBLE));
    }

    public boolean isPaymentComplete(double money, double total){
        return money >= total;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                //Call listener to send the mMoney
                if(onPaymentComplete != null && isPaymentComplete(mMoney, mTotal)){
                    onPaymentComplete.onPaymentComplete(true, mMoney);
                }
                break;

            case DialogInterface.BUTTON_NEUTRAL:
                if(onPaymentComplete != null){
                    onPaymentComplete.onPaymentComplete(true, mTotal);
                }
                break;
        }
    }


    public interface OnPaymentComplete{
        void onPaymentComplete(boolean success, double money);
    }
}
