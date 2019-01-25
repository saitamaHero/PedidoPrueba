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

import java.util.List;

import Models.Invoice;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ClientHolder> {
    private List<Invoice> invoices;
    private int layoutResource;
    private OnInvoiceClickListener onInvoiceClickListener;


    public InvoiceListAdapter(List<Invoice> itemList, int layoutResource) {
        this.invoices = itemList;
        this.layoutResource = layoutResource;
    }

    public void setOnInvoiceClickListener(OnInvoiceClickListener itemClickListener) {
        this.onInvoiceClickListener = itemClickListener;
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
        final Invoice item = invoices.get(position);

        holder.txtId.setText(item.getId());
        holder.txtName.setText(item.getName());
        //holder.txtPrice.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
        //holder.txtStock.setText(NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(onInvoiceClickListener != null) onInvoiceClickListener.onInvoiceClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
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


    public interface OnInvoiceClickListener{
        void onInvoiceClick(Invoice item);
    }
}
