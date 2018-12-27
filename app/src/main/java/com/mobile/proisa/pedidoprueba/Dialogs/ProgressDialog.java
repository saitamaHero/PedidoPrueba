package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.proisa.pedidoprueba.R;

public class ProgressDialog extends DialogFragment {
    private static final String PARAM_INFO = "param_info";

    private String info;
    private TextView textView;

    public static ProgressDialog newInstance(String info) {
        Bundle args = new Bundle();
        args.putString(PARAM_INFO, info);
        ProgressDialog fragment = new ProgressDialog();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE, getTheme());

        if(getArguments() != null){
            info = getArguments().getString(PARAM_INFO);
        }else{
            info = "...";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog_layout, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textView = view.findViewById(R.id.textView);
        textView.setText(info);
    }


    public void changeInfo(String newInfo){
        if(getDialog().isShowing() && !TextUtils.isEmpty(newInfo)){
            textView.setText(newInfo);
        }
    }
}
