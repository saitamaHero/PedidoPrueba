package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Clases.InvoiceType;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Invoice;

public class InvoicePaymentAdapter extends ArrayAdapter<InvoiceType> {

    private Context context;
    private int mLayoutResource;

    public InvoicePaymentAdapter(@NonNull Context context, int resource, @NonNull List<InvoiceType> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mLayoutResource = resource;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        return createView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }


    private View createView(int position, View convertView, ViewGroup parent){
        InvoiceType type = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        convertView = inflater.inflate(mLayoutResource, parent, false);


        Invoice.InvoicePayment payment = type.getInvoicePayment();
        int stringResource;

        if(payment.equals(Invoice.InvoicePayment.CASH)){
            stringResource = R.string.cash_type;
        }else {
            stringResource = R.string.credit_type;
        }


        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(stringResource);


        return convertView;
    }
}
