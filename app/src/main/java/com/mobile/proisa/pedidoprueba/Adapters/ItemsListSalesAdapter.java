package com.mobile.proisa.pedidoprueba.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Item;
import Utils.NumberUtils;

public class ItemsListSalesAdapter extends RecyclerView.Adapter<ItemsListSalesAdapter.MyHolder> implements OnActionListener {
    private List<Item> itemList;
    private int layoutResource;

    public ItemsListSalesAdapter(List<Item> itemList, int layoutResource) {
        super();
        this.itemList = itemList;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new MyHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final Item item = itemList.get(position);

        holder.txtNombre.setText(item.getName());
        holder.txtId.setText(item.getId());

        holder.txtCantidad.setText(NumberUtils.formatNumber(item.getQuantity(),
                NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtCantidadInv.setText(NumberUtils.formatNumber(item.getStock(),
                NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtPrecio.setText(NumberUtils.formatNumber(item.getPrice(),
                NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtSubtotal.setText(NumberUtils.formatNumber(item.getTotal(),
                NumberUtils.FORMAT_NUMER_DOUBLE));





    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public void actionOcurred(int action, int position) {
        if(action == ACTION_ADD){
            Log.d("ActionOcurred", "Agregar cantidad "+this.itemList.get(position).toString());
        }else if(action == ACTION_LESS){
            Log.d("ActionOcurred", "Quitar cantidad "+this.itemList.get(position).toString());
        }else if(action == ACTION_REMOVED){
            Log.d("ActionOcurred", "Remover "+this.itemList.get(position).toString());
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView txtNombre;
        public TextView txtId;
        public TextView txtCantidad;
        public TextView txtCantidadInv;
        public TextView txtPrecio;
        public TextView txtSubtotal;
        public CardView cardView;
        public Button btnAdd;
        public Button btnLess;
        public Button btnDelete;
        private OnActionListener actionListener;

        public MyHolder(View itemView, OnActionListener actionListener) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.name);
            txtId = itemView.findViewById(R.id.id);
            txtCantidad = itemView.findViewById(R.id.quantity);
            txtCantidadInv = itemView.findViewById(R.id.stock);
            txtPrecio = itemView.findViewById(R.id.price);
            txtSubtotal = itemView.findViewById(R.id.total);
            cardView = itemView.findViewById(R.id.card);

            btnAdd = itemView.findViewById(R.id.agregar);
            btnLess = itemView.findViewById(R.id.quitar);
            btnDelete = itemView.findViewById(R.id.borrar);
            this.actionListener = actionListener;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.borrar:
                    actionListener.actionOcurred(OnActionListener.ACTION_REMOVED,
                            getAdapterPosition());
                    break;

                case R.id.agregar:
                    actionListener.actionOcurred(OnActionListener.ACTION_ADD,
                            getAdapterPosition());
                    break;

                case R.id.quitar:
                    actionListener.actionOcurred(OnActionListener.ACTION_LESS,
                            getAdapterPosition());
                    break;
            }

        }


    }



}
