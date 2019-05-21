package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Clases.TotalElement;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.Collection;

public class TotalAdapter extends BaseAdapter{
    private Context context;
    private int resoucerId;
    private ArrayList<TotalElement> elements;

    public TotalAdapter(Context context, int resoucerId) {
        this.context = context;
        this.resoucerId = resoucerId;
        this.elements = new ArrayList<>();
    }

    public void add(TotalElement e){
        this.elements.add(e);
    }


    public void addAll(Collection<TotalElement> elements){
        this.elements.addAll(elements);
    }

    @Override
    public int getCount() {
        return this.elements.size();
    }

    @Override
    public Object getItem(int position) {
        return this.elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return context;
    }

    public int getResoucerId() {
        return resoucerId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TotalElement totalElement = (TotalElement) getItem(position);


        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(getResoucerId(), null);
        }

        TextView txtTitle = convertView.findViewById(R.id.title);
        txtTitle.setText(totalElement.getTitle());


        TextView txtSubTitle = convertView.findViewById(R.id.subtitle);
        txtSubTitle.setText(totalElement.getSubtitle());


        return convertView;
    }
}
