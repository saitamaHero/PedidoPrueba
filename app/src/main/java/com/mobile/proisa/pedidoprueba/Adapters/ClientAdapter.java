package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import Models.Client;
import Utils.DateUtils;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientHolder> {
    private List<Client> clientList;
    private int layoutResource;
    private OnClientListener clientListener;


    public ClientAdapter(List<Client> clientList, int layoutResource) {
        this.clientList = clientList;
        this.layoutResource = layoutResource;
    }

    public void setClientListener(OnClientListener clientListener) {
        this.clientListener = clientListener;
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
        final Client client = clientList.get(position);

        holder.txtId.setText(client.getId());
        holder.txtName.setText(client.getName());

        Resources res = holder.txtVisitEvent.getContext().getResources();

        DateUtils.DateConverter converter = client.getTimeToVisit();

        if(converter != null){
            int daysCount = (int)converter.getDays();
            int hourCount = (int)converter.getHours();
            int minutesCount = (int)converter.getMinutes();


            if(daysCount > 0){
                holder.txtVisitEvent.setText(
                        res.getQuantityString(R.plurals.visit_formateable,
                                daysCount, daysCount));
            }else if(hourCount > 0){
                holder.txtVisitEvent.setText(
                        res.getString(R.string.time_hours, hourCount));

            }else if(minutesCount > 0){
                holder.txtVisitEvent.setText(
                        res.getString(R.string.time_minutes, minutesCount));
            }else{
                holder.txtVisitEvent.setText(res.getString(R.string.time_unknow));
            }

            Log.d("diaryVisit", client.getVisitDate().getDateEvent().toString());
            Log.d("diaryVisit",client.getName().concat(":") + converter.toString());
        }else{
            holder.txtVisitEvent.setText(res.getString(R.string.time_unknow));
        }


        holder.txtDistance.setText(String.format(Locale.getDefault(),"%.2f Km",client.getDistance()));
        holder.txtAddress.setText(client.getAddress());

        //holder.profilePhoto.setImageURI(client.getProfilePhoto());

        Glide.with(holder.cardView.getContext()).load(client.getProfilePhoto()).thumbnail(0.1f)
        .into(holder.profilePhoto);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clientListener != null) clientListener.onClientMoreClick(client);
            }
        });

        holder.btnVisit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(clientListener != null) clientListener.onClientVisitClick(client);
           }
        });

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clientListener != null) clientListener.onClientMoreClick(client);
            }
        });
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
        public ImageView profilePhoto;
        public CardView cardView;

        public ClientHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.client_id);
            txtName = itemView.findViewById(R.id.client_name);
            txtDistance = itemView.findViewById(R.id.distance);
            txtVisitEvent = itemView.findViewById(R.id.visit_event);
            txtAddress = itemView.findViewById(R.id.address);
            btnMore = itemView.findViewById(R.id.more);
            btnVisit = itemView.findViewById(R.id.visit);
            profilePhoto = itemView.findViewById(R.id.profile_image);
            cardView = itemView.findViewById(R.id.card_client);
        }
    }


    public interface OnClientListener{
        public void onClientMoreClick(Client client);
        public void onClientVisitClick(Client client);
    }
}
