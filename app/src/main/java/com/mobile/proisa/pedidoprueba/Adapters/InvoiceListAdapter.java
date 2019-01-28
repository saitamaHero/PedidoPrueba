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
import Utils.DateUtils;
import Utils.NumberUtils;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.InvoiceHolder> {
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
    public InvoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return  new InvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceHolder holder, int position) {
        final Invoice invoice = invoices.get(position);

        holder.txtId.setText(invoice.getId());
        holder.txtName.setText(invoice.getClient().getName());
        holder.txtPrice.setText(NumberUtils.formatNumber(invoice.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtStock.setText(NumberUtils.formatNumber(invoice.getItems().size(), NumberUtils.FORMAT_NUMER_INTEGER));
        holder.txtDate.setText(DateUtils.formatDate(invoice.getDate(), DateUtils.EEE_DD_MMM_YYYY_HH_mm));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(onInvoiceClickListener != null) onInvoiceClickListener.onInvoiceClick(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class InvoiceHolder extends RecyclerView.ViewHolder{
        public TextView txtId;
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtStock;
        public TextView txtDate;

        public CardView cardView;

        public InvoiceHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.invoice_id);
            txtName = itemView.findViewById(R.id.client_name);
            txtStock = itemView.findViewById(R.id.items_quantity);
            txtPrice = itemView.findViewById(R.id.total);
            txtDate = itemView.findViewById(R.id.date);

            cardView = itemView.findViewById(R.id.card);
        }
    }


    public interface OnInvoiceClickListener{
        void onInvoiceClick(Invoice item);
    }
}
