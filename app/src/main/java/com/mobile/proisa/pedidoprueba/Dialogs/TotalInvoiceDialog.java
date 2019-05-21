package com.mobile.proisa.pedidoprueba.Dialogs;



import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Adapters.TotalAdapter;
import com.mobile.proisa.pedidoprueba.Clases.TotalElement;
import com.mobile.proisa.pedidoprueba.R;

import Models.Invoice;
import Utils.NumberUtils;

public class TotalInvoiceDialog extends DialogFragment {
    private static final String PARAM_INVOICE = "com.mobile.proisa.pedidoprueba.Dialogs.PARAM_INVOICE";

    private Invoice invoice;
    private TextView textView;
    private ListView listView;


    public static TotalInvoiceDialog newInstance(Invoice invoice) {
        Bundle args = new Bundle();
        args.putParcelable(PARAM_INVOICE, invoice);
        TotalInvoiceDialog fragment = new TotalInvoiceDialog();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            invoice = getArguments().getParcelable(PARAM_INVOICE);
        }

        setCancelable(true);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_list_view, null, false);
        listView = view.findViewById(R.id.listView);

        builder.setView(view);

        TotalAdapter totalAdapter = new TotalAdapter(getActivity(), R.layout.total_item_layout);
        totalAdapter.add(new TotalElement("TOTAL BRUTO",     NumberUtils.formatToDouble(invoice.getTotalFreeTaxes())));
        totalAdapter.add(new TotalElement("TOTAL ITBIS",     NumberUtils.formatToDouble(invoice.getTotalTaxes())));
        totalAdapter.add(new TotalElement("TOTAL ARTICULOS", NumberUtils.formatToInteger(invoice.getItems().size())));

        listView.setAdapter(totalAdapter);
        totalAdapter.notifyDataSetChanged();


        return builder.create();
    }


}
