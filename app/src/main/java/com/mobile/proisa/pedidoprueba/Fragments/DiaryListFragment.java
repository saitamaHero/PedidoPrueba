package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.proisa.pedidoprueba.Adapters.DiaryListAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.InvoiceListAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Diary;
import Models.Invoice;

public class DiaryListFragment extends Fragment {
    private static final String PARAM_DIARIES = "com.mobile.proisa.pedidoprueba.Fragments.PARAM_DIARIES";
    private List<Diary> diaries;
    private RecyclerView recyclerView;
    private DiaryListAdapter itemListAdapter;

    public DiaryListFragment() {
        // Required empty public constructor
    }

    public static DiaryListFragment newInstance(List<Diary> invoices) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_DIARIES, new ArrayList<>(invoices));
        DiaryListFragment fragment = new DiaryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() !=  null) {
            diaries = getArguments().getParcelableArrayList(PARAM_DIARIES);
        }else{
            diaries = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_diary_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        setAdapter();
    }


    private void setAdapter() {
        itemListAdapter = new DiaryListAdapter(this.diaries, R.layout.diary_item_layout);
        recyclerView.setAdapter(itemListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemListAdapter.setOnDiaryClickListener((DiaryListAdapter.OnDiaryClickListener) getActivity());
    }

}
