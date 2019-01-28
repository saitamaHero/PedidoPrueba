package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ItemListAdapter;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import Models.Invoice;
import Models.Item;
import Sqlite.ItemController;
import Sqlite.MySqliteOpenHelper;
import Utils.DateUtils;

public class InvoiceListFragment extends Fragment {
    private static final String PARAM_INVOICES = "com.mobile.proisa.pedidoprueba.Fragments.PARAM_INVOICES";
    private List<Invoice> invoices;
    private RecyclerView recyclerView;
    private InvoiceListAdapter itemListAdapter;

    public InvoiceListFragment() {
    }

    public static InvoiceListFragment newInstance(List<Invoice> invoices) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_INVOICES, new ArrayList<>(invoices));
        InvoiceListFragment fragment = new InvoiceListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() !=  null)
        {
            invoices = getArguments().getParcelableArrayList(PARAM_INVOICES);
        }else{
            invoices = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        setAdapter();
    }


    private void setAdapter() {
        itemListAdapter = new InvoiceListAdapter(this.invoices, R.layout.invoice_basic_card);
        recyclerView.setAdapter(itemListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemListAdapter.setOnInvoiceClickListener((InvoiceListAdapter.OnInvoiceClickListener) getActivity());
    }


}