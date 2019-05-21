package com.mobile.proisa.pedidoprueba.Fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;

public class FragmentBaseWithSearch extends Fragment implements SearchView.OnQueryTextListener {

    private boolean isSearching = false;
    private String  mTextSearch = "";

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.isSearching = newText.length() > 0;
        this.mTextSearch = newText;


        return true;
    }

    public String getTextSearch() {
        return mTextSearch;
    }


    public boolean isSearching() {
        return isSearching;
    }

    public SearchView.OnQueryTextListener getOnQueryTextListener(){
        return this;
    }
}
