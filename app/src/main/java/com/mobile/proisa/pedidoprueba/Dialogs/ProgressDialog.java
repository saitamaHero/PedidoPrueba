package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Paint;
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
    private String mTitle;

    public static ProgressDialog newInstance(String info) {
        Bundle args = new Bundle();
        args.putString(PARAM_INFO, info);
        ProgressDialog fragment = new ProgressDialog();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    public static ProgressDialog newInstance(String title, String info) {
        Bundle args = new Bundle();
        ProgressDialog fragment = new ProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(getArguments() != null){
            info = getArguments().getString(PARAM_INFO);
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


        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Dialog_MinWidth);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setTitle(getString(R.string.processing));

        return dialog;
    }

    public void changeInfo(String newInfo){
        if(getDialog().isShowing() && !TextUtils.isEmpty(newInfo)){
            textView.setText(newInfo);
        }
    }
}
