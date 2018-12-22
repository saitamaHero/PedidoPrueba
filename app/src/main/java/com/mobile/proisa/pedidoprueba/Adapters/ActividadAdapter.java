package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadHolder> {
    private List<Actividad> actividadList;
    private int layoutResource;

    public ActividadAdapter(List<Actividad> actividadList, int layoutResource) {
        this.actividadList = actividadList;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public ActividadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return  new ActividadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadHolder holder, int position) {
        Actividad actividad = actividadList.get(position);

        holder.txtNumeric.setText(actividad.getNumeric());

        if(!actividad.isGood()){
            Context context = holder.txtNumeric.getContext();

            holder.txtNumeric.setTextColor(context.getResources().getColor(R.color.badStatus));
        }

        holder.txtDescription.setText(actividad.getDescription());
        holder.txtInfo.setText(actividad.getInfo());
    }

    @Override
    public int getItemCount() {
        return actividadList.size();
    }


    public class ActividadHolder extends RecyclerView.ViewHolder{
        public TextView txtNumeric;
        public TextView txtDescription;
        public TextView txtInfo;

        public ActividadHolder(View itemView) {
            super(itemView);
            txtNumeric = itemView.findViewById(R.id.numeric);
            txtDescription = itemView.findViewById(R.id.description);
            txtInfo = itemView.findViewById(R.id.info);
        }
    }
}
