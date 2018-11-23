package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

import java.util.List;
import java.util.Locale;

import Models.Client;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientHolder> {
    private List<Client> clientList;
    private int layoutResource;

    public ClientAdapter(List<Client> clientList, int layoutResource) {
        this.clientList = clientList;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public ClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return  new ClientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientHolder holder, int position) {
        Client client = clientList.get(position);

        holder.txtId.setText(client.getId());
        holder.txtName.setText(client.getName());
        holder.txtVisitEvent.setText(client.isDayOfTheVisit() ? "Programado" : "No Programado");
        holder.txtDistance.setText(String.format(Locale.getDefault(),"%.2f Km",client.getDistance()));
        holder.txtAddress.setText(client.getAddress());

    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }


    public class ClientHolder extends RecyclerView.ViewHolder{
        public TextView txtId;
        public TextView txtName;
        public TextView txtVisitEvent;
        public TextView txtDistance;
        public TextView txtAddress;
        public Button btnVisit;
        public Button btnMore;

        public ClientHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.client_id);
            txtName = itemView.findViewById(R.id.client_name);
            txtDistance = itemView.findViewById(R.id.distance);
            txtVisitEvent = itemView.findViewById(R.id.visit_event);
            txtAddress = itemView.findViewById(R.id.address);
            btnMore = itemView.findViewById(R.id.more);
            btnVisit = itemView.findViewById(R.id.visit);
        }
    }
}
