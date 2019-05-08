package com.mobile.proisa.pedidoprueba.Fragments;


import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.proisa.pedidoprueba.Adapters.ActividadAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Diary;
import Models.Invoice;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;


public class ActividadFragment extends Fragment {
    private static final String PARAM_ACTIVIDAD_LIST = "param_actividad_list";
    private RecyclerView recyclerView;
    private List<Actividad> actividadList;

    public ActividadFragment() {
        // Required empty public constructor
    }

    public static ActividadFragment newInstance(List<Actividad> list) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_ACTIVIDAD_LIST, new ArrayList<>(list));
        ActividadFragment fragment = new ActividadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ActividadFragment newInstance() {
        ActividadFragment fragment = new ActividadFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            actividadList = getArguments().getParcelableArrayList(PARAM_ACTIVIDAD_LIST);
        }else{
            actividadList = new ArrayList<>();
        }

        //getActividades();
    }

    @Override
    public void onResume() {
        super.onResume();

        actividadList.clear();

        getActividades();
        setAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_actividad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setAdapter() {
        ActividadAdapter actividadAdapter = new ActividadAdapter(this.actividadList, R.layout.data_detail_layout);
        recyclerView.setAdapter(actividadAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void getActividades()
    {
        Actividad.Builder builder = new Actividad.Builder();
        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getVisitasHechas()))
                .addId(1).addDescription(getString(R.string.msg_visits_today));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getVisitasPendientes()))
                .addId(1).addDescription(getString(R.string.msg_visits_pending));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getFacturasPorVisita()))
                .addId(2).addDescription(getString(R.string.msg_invoices_today));


        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas()))
                .addId(3).addDescription(getString(R.string.msg_invoices_total));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas(Invoice.InvoicePayment.CASH)))
                .addId(4).addDescription(getString(R.string.msg_invoices_total_cash));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas(Invoice.InvoicePayment.CREDIT)))
                .addId(4).addDescription(getString(R.string.msg_invoices_total_credit));

        this.actividadList.add(builder.create());
    }

    private long getVisitasHechas(){
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        String selection = Diary._START_TIME + " IS NOT NULL AND " + Diary._END_TIME + " IS NOT NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_NAME, selection);
    }

    private long getVisitasPendientes() {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        String selection = Diary._START_TIME + " IS NULL AND " + Diary._END_TIME + " IS NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_NAME, selection);
    }

    private long getFacturasPorVisita(){
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        //String selection = Diary._START_TIME + " IS NOT NULL AND " + Diary._END_TIME + " IS NOT NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_DIARY_INV);
    }

    private double getTotalPorFacturas()
    {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        double total = 0.0;

        List<Invoice> invoiceList = new InvoiceController(appDatabase).getAll();

        for(Invoice invoice : invoiceList) {
            total += invoice.getTotal();
        }

        return total;
    }


    private double getTotalPorFacturas(Invoice.InvoicePayment paymentType)
    {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        double total = 0.0;

        List<Invoice> invoiceList = new InvoiceController(appDatabase).getAll();

        for(Invoice invoice : invoiceList) {
            total += invoice.getInvoiceType().equals(paymentType) ? invoice.getTotal() : 0;
        }

        return total;
    }

}
