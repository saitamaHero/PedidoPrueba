package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Client;
import Models.Diary;
import Utils.DateUtils;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryHolder> {
    private static final String TAG = "DiaryListAdapter";
    private List<Diary> diaries;
    private int layoutResource;
    private OnDiaryClickListener onDiaryClickListener;


    public DiaryListAdapter(List<Diary> itemList, int layoutResource) {
        this.diaries = itemList;
        this.layoutResource = layoutResource;
    }

    public void setOnDiaryClickListener(OnDiaryClickListener itemClickListener) {
        this.onDiaryClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public DiaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return  new DiaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryHolder holder, int position) {
        final Diary diary = diaries.get(position);

        Client client = diary.getClientToVisit();

        if(client != null){
            holder.txtId.setText(client.getId());
            holder.txtName.setText(client.getName());

            Glide.with(holder.profilePhoto.getContext()).load(client.getProfilePhoto()).thumbnail(0.1f)
                    .into(holder.profilePhoto);
        }

        holder.txtDate.setText(DateUtils.formatDate(diary.getDateEvent(), DateUtils.YYYY_MM_DD_HH_mm_ss));
        holder.txtDuration.setText(String.valueOf(diary.getDuration()));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(onDiaryClickListener != null) onDiaryClickListener.onInvoiceClick(diary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    public class DiaryHolder extends RecyclerView.ViewHolder{
        public TextView txtId;
        public TextView txtName;
        public TextView txtDate;
        public TextView txtDuration;
        public ImageView profilePhoto;


        public CardView cardView;

        public DiaryHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.client_id);
            txtName = itemView.findViewById(R.id.client_name);
            txtDate = itemView.findViewById(R.id.diary_datetime);
            txtDuration = itemView.findViewById(R.id.duration);
            profilePhoto = itemView.findViewById(R.id.profile_image);
            cardView = itemView.findViewById(R.id.card);
        }
    }


    public interface OnDiaryClickListener{
        void onInvoiceClick(Diary item);
    }
}
