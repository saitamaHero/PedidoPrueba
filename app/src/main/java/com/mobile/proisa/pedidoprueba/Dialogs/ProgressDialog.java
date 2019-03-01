package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.AlertDialog;
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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate( R.layout.progress_dialog_layout, null);
        textView  = view.findViewById(R.id.textView);
        textView.setText(info);


        builder.setTitle(getString(R.string.processing)).setView(view);

        return builder.create();
    }

    public void changeInfo(String newInfo){
        if(getDialog().isShowing() && !TextUtils.isEmpty(newInfo)){
            textView.setText(newInfo);
        }
    }
}
