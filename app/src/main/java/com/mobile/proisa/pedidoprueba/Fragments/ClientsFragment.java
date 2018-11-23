package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.proisa.pedidoprueba.Adapters.ActividadAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ClientAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Client;


public class ClientsFragment extends Fragment {
    private static final String PARAM_CLIENT_LIST = "param_client_list";

    private List<Client> clients;
    private RecyclerView recyclerView;

    public ClientsFragment() {
        // Required empty public constructor
    }


    public static ClientsFragment newInstance(List<Client> clients) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_CLIENT_LIST, new ArrayList<>(clients));
        ClientsFragment fragment = new ClientsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            clients = getArguments().getParcelableArrayList(PARAM_CLIENT_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clients, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);

        setAdapter();
    }

    private void setAdapter() {
        ClientAdapter clientAdapter = new ClientAdapter(this.clients, R.layout.cliente_card_layout);
        recyclerView.setAdapter(clientAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
