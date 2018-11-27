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
import java.util.Stack;

import Models.Diary;
import Utils.DateUtils;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ActividadHolder> {
    private List<Diary> diaryList;
    private int layoutResource;

    public DiaryAdapter(List<Diary> diaries, int layoutResource) {
        this.diaryList = diaries;
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
        Diary diary = diaryList.get(position);

        holder.txtComment.setText(diary.getComment());
        holder.txtDate.setText(DateUtils.formatDate(diary.getDateEvent(), DateUtils.DD_MM_YYYY_hh_mm_ss));
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }


    public class ActividadHolder extends RecyclerView.ViewHolder{
        public TextView txtComment;
        public TextView txtDate;


        public ActividadHolder(View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.comment);
            txtDate = itemView.findViewById(R.id.date);
        }
    }
}
