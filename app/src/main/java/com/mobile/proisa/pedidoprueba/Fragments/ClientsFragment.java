package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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
    private ClientAdapter clientAdapter;

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
        /*Toolbar toolbar = view.findViewById(R.id.toolbar);


        toolbar.inflateMenu(R.menu.menu_search);



        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                SearchView searchView = (SearchView) item;

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        setAdapter(clients.size() - newText.length());

                        return true;
                    }
                });

                return true;
            }
        });*/

        setAdapter();
    }



    private void setAdapter() {
        clientAdapter = new ClientAdapter(this.clients, R.layout.cliente_card_layout);
        recyclerView.setAdapter(clientAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        clientAdapter.setClientListener(new ClientAdapter.OnClientListener() {
            @Override
            public void onClientMoreClick(Client client) {
                Toast.makeText(getActivity(), "Ver mas: "+client.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClientVisitClick(Client client) {
                Toast.makeText(getActivity(), "Visita: "+client.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(int max) {
        this.clients = this.clients.subList(0, max);
        clientAdapter.notifyDataSetChanged();/*
        ClientAdapter clientAdapter = new ClientAdapter(this.clients.subList(0, max), R.layout.cliente_card_layout);
        recyclerView.setAdapter(clientAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        clientAdapter.setClientListener(new ClientAdapter.OnClientListener() {
            @Override
            public void onClientMoreClick(Client client) {
                Toast.makeText(getActivity(), "Ver mas: "+client.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClientVisitClick(Client client) {
                Toast.makeText(getActivity(), "Visita: "+client.toString(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
