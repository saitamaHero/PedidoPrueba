package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.mobile.proisa.pedidoprueba.Models.Item;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyHolder> {
    private List<Item> itemList;
    private int layoutResource;


    public ItemsAdapter(List<Item> itemList, int layoutResource) {
        this.itemList = itemList;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);

        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final Item item = itemList.get(position);

        holder.txtNombre.setText(item.getName());
        holder.txtId.setText(item.getId());
        holder.txtCantidad.setText(NumberUtils.formatNumber(item.getQuantity(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtCantidadInv.setText(NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtPrecio.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtSubtotal.setText(NumberUtils.formatNumber(item.getTotal(), NumberUtils.FORMAT_NUMER_DOUBLE));



        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_add:
                        onItemQuantityChange(item, item.getQuantity() + 1);
                        notifyItemChanged(position);
                        break;

                    case R.id.action_remove:
                        onItemQuantityChange(item,item.getQuantity() - 1);
                        notifyItemChanged(position);

                        break;

                    case R.id.action_delete:
                        itemList.remove(item);
                        notifyItemRemoved(position);
                        break;
                }

                //notifyDataSetChanged();

                Log.d("menuItemClicked", "position: "+ position);
                return true;
            }
        });


    }

    public void onItemQuantityChange(Item item, double newQuantity){
        item.setQuantity(newQuantity);
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView txtNombre;
        public TextView txtId;
        public TextView txtCantidad;
        public TextView txtCantidadInv;
        public TextView txtPrecio;
        public TextView txtSubtotal;
        public Toolbar toolbar;

        public MyHolder(View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.nombre);
            txtId = itemView.findViewById(R.id.id);
            txtCantidad = itemView.findViewById(R.id.cantidad);
            txtCantidadInv = itemView.findViewById(R.id.cantidad_inventario);
            txtPrecio = itemView.findViewById(R.id.precio);
            txtSubtotal = itemView.findViewById(R.id.subtotal);
            toolbar = itemView.findViewById(R.id.toolbarCard);
            toolbar.inflateMenu(R.menu.menu_per_item);
        }
    }

    public class MyMenuClickListener implements  Toolbar.OnMenuItemClickListener{
        private int position;

        public MyMenuClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }
    }



}
