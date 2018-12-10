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
    private OnListChangedListener onListChangedListener;
    private NotificationListener notificationListener;

    public ItemsListSalesAdapter(List<Item> itemList, int layoutResource) {
        super();
        this.itemList = itemList;
        this.layoutResource = layoutResource;
        setHasStableIds(true);
    }

    public void setOnListChangedListener(OnListChangedListener onListChangedListener) {
        this.onListChangedListener = onListChangedListener;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new MyHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        Item item = itemList.get(position);

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
    public long getItemId(int position) {
        return this.itemList.get(position).hashCode();
    }

    @Override
    public void actionOcurred(int action, int position) {
        Item selectedItem = this.itemList.get(position);
        double itemQuantity = selectedItem.getQuantity();

        if(action == ACTION_ADD){
            if(itemQuantity < selectedItem.getStock()){
                selectedItem.setQuantity(itemQuantity + 1);
                notifyItemChanged(position);
            }else{
                if(notificationListener != null)
                    notificationListener.onNotificationRequired(NotificationListener.ITEM_STOCK_EXCEEDED);
            }
        }else if(action == ACTION_LESS){
            if(itemQuantity  > 1){
                selectedItem.setQuantity(itemQuantity - 1);
                notifyItemChanged(position);
            }else{
                if(notificationListener != null)
                    notificationListener.onNotificationRequired(NotificationListener.ITEM_QUANTITY_ZERO);
            }
        }else if(action == ACTION_REMOVED){
            this.itemList.remove(position);
            notifyItemRemoved(position);
        }

        if(onListChangedListener != null)
            onListChangedListener.onListChange(this.itemList);
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

            //Agregar Cantidad +
            btnAdd = itemView.findViewById(R.id.agregar);
            btnAdd.setOnClickListener(this);
            //Quitar cantidad -
            btnLess = itemView.findViewById(R.id.quitar);
            btnLess.setOnClickListener(this);
            //Borrar
            btnDelete = itemView.findViewById(R.id.borrar);
            btnDelete.setOnClickListener(this);

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

    public interface OnListChangedListener{
        void onListChange(List<Item> list );
    }

    public interface NotificationListener{
        int ITEM_STOCK_EXCEEDED = -1;
        int ITEM_QUANTITY_ZERO = 0;

        void onNotificationRequired(int notificationType);

    }

}
