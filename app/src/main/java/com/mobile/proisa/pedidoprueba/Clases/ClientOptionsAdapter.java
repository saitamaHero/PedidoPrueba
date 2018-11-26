package com.mobile.proisa.pedidoprueba.Clases;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

/**
 * Created by dionicio on 25/11/18.
 */

public class ClientOptionsAdapter extends BaseAdapter{
    private Context context;
    private Menu myMenu;
    private int layoutResourceFile;

    public ClientOptionsAdapter(Context context, Menu myMenu, int layoutResourceFile) {
        this.context = context;
        this.myMenu = myMenu;
        this.layoutResourceFile = layoutResourceFile;
    }

    @Override
    public int getCount() {
        return myMenu.size();
    }

    @Override
    public Object getItem(int i) {
        return myMenu.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return myMenu.getItem(i).getItemId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MenuItem menuItem = (MenuItem) getItem(i);

        view = LayoutInflater.from(context).inflate(layoutResourceFile, null);

        ImageView imageView = view.findViewById(R.id.icon);
        imageView.setImageDrawable(menuItem.getIcon());


        TextView textView = view.findViewById(R.id.title);
        textView.setText(menuItem.getTitle());

        return view;
    }
}
