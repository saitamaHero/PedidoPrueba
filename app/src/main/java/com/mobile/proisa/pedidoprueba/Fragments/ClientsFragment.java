package com.mobile.proisa.pedidoprueba.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mobile.proisa.pedidoprueba.Activities.DetailsClientActivity;
import com.mobile.proisa.pedidoprueba.Adapters.ActividadAdapter;
import com.mobile.proisa.pedidoprueba.Adapters.ClientAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Sqlite.ClientController;
import Sqlite.MySqliteOpenHelper;
import Utils.DateUtils;


public class ClientsFragment extends Fragment implements SearchView.OnQueryTextListener{
    private static final String PARAM_CLIENT_LIST = "param_client_list";

    private List<Client> clients;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ClientAdapter clientAdapter;

    public ClientsFragment() {
        // Required empty public constructor
    }


    public static ClientsFragment newInstance() {
        Bundle args = new Bundle();
        ClientsFragment fragment = new ClientsFragment();
        fragment.setArguments(args);
        return fragment;
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
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_clients, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        fab.show();
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                        fab.hide();
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        fab.hide();
                        break;
                }

            }

        });


        clients = getClients(5);

        if(clients == null || clients.isEmpty()){
            ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(getActivity()).getWritableDatabase());
            Client client = new Client();

            client.setId("CL-2100");
            client.setName("Dionicio Acevedo Lebron");
            client.setIdentityCard("40225706668");
            client.setEmail("tec.dionicioacevedo@gmail.com");
            client.setBirthDate(DateUtils.convertToDate("16-02-1997", DateUtils.DD_MM_YYYY));
            client.setCreditLimit(5000.00);
            client.setAddress("Los Buenos #12, Av. Bartolome Colon, Santiago");

            boolean inserted = controller.insert(client);

            if(inserted){
                Toast.makeText(getActivity().getApplicationContext(),
                        client.toString(), Toast.LENGTH_LONG).show();
            }
        }


        setAdapter();
    }

    private void setAdapter() {
        clientAdapter = new ClientAdapter(this.clients, R.layout.cliente_card_layout);
        recyclerView.setAdapter(clientAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        clientAdapter.setClientListener(new ClientAdapter.OnClientListener() {
            @Override
            public void onClientMoreClick(Client client) {
                Intent seeMoreIntent = new Intent(getActivity().getApplicationContext(), DetailsClientActivity.class);
                seeMoreIntent.putExtra("client", client);
                getActivity().startActivity(seeMoreIntent);
            }

            @Override
            public void onClientVisitClick(Client client) {
                if(client.getDistance() < 300.00){
                    Toast.makeText(getActivity(), "Visita: "+client.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<Client> getClients(int count){
        ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(getActivity()).getWritableDatabase());

        return  controller.getAll(count);
    }

    private List<Client> getClients(String str) {
        ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(getActivity()).getWritableDatabase());
        return controller.getAllLike(str);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem  item = menu.findItem(R.id.app_bar_search);

        item.collapseActionView();

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        clients = getClients(newText);
        setAdapter();
        return true;
    }
}
