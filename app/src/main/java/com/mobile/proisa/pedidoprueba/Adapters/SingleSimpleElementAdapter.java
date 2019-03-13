package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import Models.SimpleElement;

/**
 * Implementación de {@link ListAdapter} para
 */
public class SingleSimpleElementAdapter extends ArrayAdapter<SimpleElement> implements ListAdapter {

    public SingleSimpleElementAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SimpleElement element = getItem(position);

        convertView = super.getView(position, convertView, parent);

        TextView txtView = convertView.findViewById(android.R.id.text1);
        txtView.setText(element.getName());



        return convertView;
    }


    @Override
    public int getPosition(@Nullable SimpleElement item) {
        return super.getPosition(item);
    }
}
