package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.util.List;
import java.util.Locale;

import Models.Client;
import Models.Item;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ClientHolder> {
    private List<Item> items;
    private int layoutResource;
    private OnItemClickListener onItemClickListener;


    public ItemListAdapter(List<Item> itemList, int layoutResource) {
        this.items = itemList;
        this.layoutResource = layoutResource;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
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
        final Item item = items.get(position);

        holder.txtId.setText(item.getId());
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtStock.setText(NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(onItemClickListener != null) onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ClientHolder extends RecyclerView.ViewHolder{
        public TextView txtId;
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtStock;


        public CardView cardView;

        public ClientHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.id);
            txtName = itemView.findViewById(R.id.name);
            txtStock = itemView.findViewById(R.id.stock);
            txtPrice = itemView.findViewById(R.id.price);

            cardView = itemView.findViewById(R.id.card);
        }
    }


    public interface OnItemClickListener{
        void onItemClick(Item item);
    }
}
