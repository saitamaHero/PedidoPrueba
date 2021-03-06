package com.mobile.proisa.pedidoprueba.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Item;
import Utils.NumberUtils;

public class ItemSelectableAdapter extends RecyclerView.Adapter<ItemSelectableAdapter.SelectableViewHolder>
        implements MyOnItemSelectedListener {
    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;
    private List<ItemSelectable> selectableList;
    private int layoutResource;
    private boolean multiSelectionEnabled;
    private MyOnItemSelectedListener onItemSelectedListener;

    public ItemSelectableAdapter(List<ItemSelectable> selectableList, int layoutResource, boolean multiSelectionEnabled) {
        this.selectableList = selectableList;
        this.layoutResource = layoutResource;
        this.multiSelectionEnabled = multiSelectionEnabled;
    }

    public void setOnItemSelectedListener(MyOnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @NonNull
    @Override
    public SelectableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);

        return new SelectableViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableViewHolder holder, int position) {
        holder.itemSelectable = selectableList.get(position);
        holder.renderSelection(holder.itemSelectable.isSelected());

        Item item = selectableList.get(position);

        holder.txtId.setText(item.getId());
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));
        holder.txtCategory.setText(item.getCategory().getName());

        String stockQuantity = holder.txtStock.getContext().getString(R.string.two_string_format,
                NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE),item.getUnit().getId());

        holder.txtStock.setText(stockQuantity);
    }

    @Override
    public int getItemCount() {
        return selectableList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(multiSelectionEnabled){
            return MULTI_SELECTION;
        }else{
            return SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(ItemSelectable itemSelectable) {
            onItemSelectedListener.onItemSelected(itemSelectable);
    }

    public static class SelectableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyOnItemSelectedListener onItemSelectedListener;
        public ItemSelectable itemSelectable;
        public CardView cardView;
        public CheckBox check;
        public TextView txtId;
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtStock;
        public TextView txtCategory;


        public SelectableViewHolder(View itemView, MyOnItemSelectedListener onItemSelectedListener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);

            check = itemView.findViewById(R.id.view_check);
            check.setOnClickListener(this);
            cardView.setOnClickListener(this);

            txtId       = itemView.findViewById(R.id.id);
            txtName     = itemView.findViewById(R.id.name);
            txtStock    = itemView.findViewById(R.id.stock);
            txtPrice    = itemView.findViewById(R.id.price);
            txtCategory = itemView.findViewById(R.id.category);

            this.onItemSelectedListener = onItemSelectedListener;
        }

        private void renderSelection(boolean selected) {
            check.setChecked(selected);
        }


        @Override
        public void onClick(View view) {
            run();
        }

        private void run()
        {
            //Deshacer seleccion
            if(itemSelectable.isSelected() && getItemViewType() == MULTI_SELECTION){
                renderSelection(false);
                itemSelectable.setSelected(false);
            }else{
                renderSelection(true);
                itemSelectable.setSelected(true);
            }

            SelectableViewHolder.this.onItemSelectedListener.onItemSelected(itemSelectable);
        }
    }



}
