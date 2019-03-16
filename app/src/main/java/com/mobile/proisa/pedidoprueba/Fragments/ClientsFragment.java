package com.mobile.proisa.pedidoprueba.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mobile.proisa.pedidoprueba.Activities.DetailsClientActivity;
import com.mobile.proisa.pedidoprueba.Activities.EditClientActivity;
import com.mobile.proisa.pedidoprueba.Adapters.ClientAdapter;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Tasks.DialogInTask;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import BaseDeDatos.ClientUpdater;
import BaseDeDatos.DiaryUpdater;
import BaseDeDatos.InvoiceUpdater;
import BaseDeDatos.SqlConnection;
import BaseDeDatos.SqlUpdater;
import BaseDeDatos.ZoneUpdater;
import Models.Client;
import Models.Invoice;
import Sqlite.ClientController;
import Sqlite.DiaryController;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Sqlite.ZoneController;

import static android.app.Activity.RESULT_OK;


public class ClientsFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener, TareaAsincrona.OnFinishedProcess{
    private static final String PARAM_CLIENT_LIST = "param_client_list";
    public static final int DETAILS_CLIENT_ACTIVITY = 805;
    private static final int CREATE_CLIENT_CODE = 806;
    private static final int DEFAULT_CLIENTS_COUNT = 20;

    private List<Client> clients;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ClientAdapter clientAdapter;
    private boolean isSearching;

    private OnFragmentInteractionListener onFragmentInteractionListener;

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

        onFragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
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
        fab.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        fab.show();
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:
 //                       fab.hide();
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                         fab.hide();
                    break;

                }

            }

        });

        updateList();
    }

    private void setAdapter() {
        Collections.sort(clients, new Client.SortByVisitDate());


        clientAdapter = new ClientAdapter(this.clients, R.layout.cliente_card_layout);
        recyclerView.setAdapter(clientAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        clientAdapter.setClientListener(new ClientAdapter.OnClientListener() {
            @Override
            public void onClientMoreClick(Client client) {
                Intent seeMoreIntent = new Intent(getActivity().getApplicationContext(), DetailsClientActivity.class);
                seeMoreIntent.putExtra(DetailsClientActivity.EXTRA_CLIENT, client);
                startActivityForResult(seeMoreIntent, DETAILS_CLIENT_ACTIVITY);
            }

            @Override
            public void onClientVisitClick(Client client) {
                Intent seeMoreIntent = new Intent(getActivity().getApplicationContext(), DetailsClientActivity.class);
                seeMoreIntent.putExtra(DetailsClientActivity.EXTRA_CLIENT, client);
                seeMoreIntent.putExtra(DetailsClientActivity.EXTRA_INIT_VISIT, true);
                startActivityForResult(seeMoreIntent, DETAILS_CLIENT_ACTIVITY);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sync:
                new SyncClients(0, getActivity(), this, true).execute();
                break;
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        isSearching = newText.length() > 0;

        clients = getClients(newText);
        setAdapter();


        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case DETAILS_CLIENT_ACTIVITY:
                //onFragmentInteractionListener.requestChangePage();
                break;

            case CREATE_CLIENT_CODE:
                if(resultCode == RESULT_OK)
                {
                    Client clientToInsert = data.getExtras().getParcelable(EditClientActivity.EXTRA_DATA);

                    if(clientToInsert != null){
                       saveClient(clientToInsert);
                    }
                }
                break;
        }

    }

    public void saveClient(final Client clientToInsert)
    {
        ClientController controller =
                new ClientController(MySqliteOpenHelper.getInstance(getActivity()).getWritableDatabase());

        if(controller.insert(clientToInsert)){
            String msg = getString(R.string.save_success, clientToInsert.getName());
            Toast.makeText(getActivity().getApplicationContext(),
                    msg, Toast.LENGTH_LONG).show();


            updateList();

            new SyncClients(0, getActivity(), this).execute();
        }else{
            /*Toast.makeText(getActivity().getApplicationContext(),
                    R.string.error_to_save, Toast.LENGTH_LONG).show();*/

            Snackbar.make(recyclerView, R.string.error_to_save, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveClient(clientToInsert);
                        }
                    }).show();
        }
    }

    private void updateList(){
        clients = getClients(DEFAULT_CLIENTS_COUNT);
        setAdapter();
    }

/*    @Override
    public void onResume() {
        super.onResume();

        if(!isSearching)
            updateList();
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent createClientIntent = new Intent(getActivity(), EditClientActivity.class);
                startActivityForResult(createClientIntent, CREATE_CLIENT_CODE);
                break;
        }
    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            Toast.makeText(getActivity(), getString(R.string.updater_success), Toast.LENGTH_SHORT).show();
            updateList();
        }

    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Toast.makeText(getActivity(), exceptions.pop().getMessage(), Toast.LENGTH_SHORT).show();
    }


    public static class SyncClients extends DialogInTask<Void, String, Void> implements SqlUpdater.OnDataUpdateListener<Client>, SqlUpdater.OnErrorListener {

        public SyncClients(int id, Activity context, OnFinishedProcess listener) {
            super(id, context, listener);
        }

        public SyncClients(int id, Activity context, OnFinishedProcess listener, boolean mDialogShow) {
            super(id, context, listener, mDialogShow);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress(getContext().getString(R.string.starting));
            SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());
            MySqliteOpenHelper mySqliteOpenHelper = MySqliteOpenHelper.getInstance(getContext());

            ClientController controller = new ClientController(mySqliteOpenHelper.getWritableDatabase());

            //Si no hay elementos en la base de datos no se analizara practicamente nada.
            ClientUpdater updater = new ClientUpdater(getContext().getApplicationContext(), connection, controller);
            updater.setOnDataUpdateListener(this);
            updater.setOnErrorListener(this);
            updater.addData(controller.getAll());
            updater.apply();

            //Llamar este metodo para que inserte los datos que hacen falta del servidor
            updater.retriveData();

            //Si ocurre un error con los clientes no continuar y acabar el proceso
            if(!isCancelled()) {
                /*Visitas*/
                DiaryController diaryController = new DiaryController(mySqliteOpenHelper.getWritableDatabase());
                //Updater de las visitas
                DiaryUpdater diaryUpdater = new DiaryUpdater(getContext().getApplicationContext(), connection, diaryController);
                diaryUpdater.addData(diaryController.getAll());
                diaryUpdater.apply();

                //Obtener visitas que estan en el servidor
                diaryUpdater.retriveData();
            }

            //Si ocurre un error con los clientes no continuar y acabar el proceso
            if(!isCancelled()) {
                /*Visitas*/
                ZoneController zoneController = new ZoneController(mySqliteOpenHelper.getWritableDatabase());
                //Updater de las visitas
                ZoneUpdater zoneUpdater = new ZoneUpdater(getContext().getApplicationContext(), connection, zoneController);

                zoneUpdater.retriveData();
            }

            //Si ocurre un error con los clientes no continuar y acabar el proceso
            if(!isCancelled()) {
                /*Visitas*/
                InvoiceController invoiceController = new InvoiceController(mySqliteOpenHelper.getWritableDatabase());
                //Updater de las visitas
                InvoiceUpdater invoiceUpdater = new InvoiceUpdater(getContext().getApplicationContext(), connection, invoiceController);
                invoiceUpdater.retriveData();
            }
            return null;
        }

        @Override
        public void onDataUpdate(Client data, int action) {
            String resource;

            switch (action){
                case ACTION_INSERT_REMOTE:
                case ACTION_INSERT_LOCAL:
                    resource = getContext().getString(R.string.insert_msg,data.getName());
                    break;

                case ACTION_UPDATE_REMOTE:
                    resource = getContext().getString(R.string.update_msg, data.getName());
                    break;

                default:
                    return;
            }

            publishProgress(resource);
        }

        @Override
        public void onDataUpdated(Client data) {

        }

        @Override
        public void onError(int error) {
            publishError(new Exception(getContext().getString(R.string.error_to_updater)));
            cancel(true);
        }
    }



    public interface  OnFragmentInteractionListener{
        public void requestChangePage();
    }
}
