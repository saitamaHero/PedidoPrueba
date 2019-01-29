package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextMessageFragment extends Fragment {
    private static final String PARAM_TEXT = "com.mobile.proisa.pedidoprueba.Fragments.PARAM_TEXT";

    private String mText;

    public TextMessageFragment() {
        // Required empty public constructor
    }


    public static TextMessageFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(PARAM_TEXT, text);
        TextMessageFragment fragment = new TextMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mText = getArguments().getString(PARAM_TEXT);
        }else{
            mText = getString(R.string.nothing_to_show);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtText = view.findViewById(R.id.text);
        txtText.setText(mText);
    }
}
