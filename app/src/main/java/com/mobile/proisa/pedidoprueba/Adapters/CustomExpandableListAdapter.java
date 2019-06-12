package com.mobile.proisa.pedidoprueba.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.Clases.ItemSelectable;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.Category;
import Utils.NumberUtils;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Category> groupListTitle;
    private HashMap<Category,List<ItemSelectable>> details;

    private MyOnItemSelectedListener onItemSelectedListener;

    public CustomExpandableListAdapter(Context context, List<Category> stringList, HashMap<Category, List<ItemSelectable>> details) {
        this.context = context;
        this.groupListTitle = stringList;
        this.details = details;
    }

    public void setOnItemSelectedListener(MyOnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public int getGroupCount() {
        return groupListTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ItemSelectable> items = details.get(groupListTitle.get(groupPosition));
        return items == null? 0 : items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return details.get(groupListTitle.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Category title = (Category) getGroup(groupPosition);

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_group,null);
        }

        TextView txtTitle = convertView.findViewById(R.id.listTitle);

        if(Category.UNKNOWN_CATEGORY.equals(title)){
            txtTitle.setText(convertView.getContext().getString(R.string.no_category));
        }else{
            txtTitle.setText(title.getName());
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemSelectable childData = (ItemSelectable) getChild(groupPosition, childPosition);

        //if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selectable_card,null);
        //}

        final CheckBox checkBox =  convertView.findViewById(R.id.view_check);
        checkBox.setChecked(childData.isSelected());

        TextView txtChildData = convertView.findViewById(R.id.name);
        txtChildData.setText(childData.getName());

        TextView txtItemId = convertView.findViewById(R.id.id);
        txtItemId.setText(childData.getId());

        TextView txtPrice = convertView.findViewById(R.id.price);
        txtPrice.setText(NumberUtils.formatNumber(childData.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));


        TextView txtStock = convertView.findViewById(R.id.stock);
        String text = context.getString(R.string.two_string_format,NumberUtils.formatNumber(childData.getStock(), NumberUtils.FORMAT_NUMER_INTEGER),
                childData.getUnit().getId());
        txtStock.setText(text);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(!checkBox.isChecked());
                //onChildClick(groupPosition, childPosition);
            }
        };

        convertView.setOnClickListener(onClickListener);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onChildClick(groupPosition, childPosition);
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    protected void onChildClick(int group, int child)
    {
        ItemSelectable itemSelectable = (ItemSelectable) getChild(group, child);
        itemSelectable.setSelected(!itemSelectable.isSelected());

        if(onItemSelectedListener != null)
            onItemSelectedListener.onItemSelected(itemSelectable);

    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        groupListTitle.clear();
        groupListTitle.addAll(new ArrayList<Category>(details.keySet()));

    }
}