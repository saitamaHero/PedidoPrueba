package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.mobile.proisa.pedidoprueba.R;

public class DialogDurationPicker extends DialogFragment implements DialogInterface.OnClickListener, NumberPicker.OnValueChangeListener {
    private static final String PARAM_VALUE = "param_value";
    public static int MAX_VALUE = 9;
    public static int MIN_VALUE = 0;
    private NumberPicker digit1;
    private NumberPicker digit2;
    private OnValueSetListener onValueSetListener;


    private int value;


    public static DialogDurationPicker newInstance() {
        DialogDurationPicker fragment = new DialogDurationPicker();
        return fragment;
    }

    public static DialogDurationPicker newInstance(int value) {
        Bundle args = new Bundle();
        DialogDurationPicker fragment = new DialogDurationPicker();
        args.putInt(PARAM_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            value = getArguments().getInt(PARAM_VALUE);
        }else{
            value = 0;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_calendar_layout, null);
        builder.setView(view);

        builder.setTitle("Cuanto tiempo(minutos) planeas durar?");

        digit1 = view.findViewById(R.id.digit1);
        digit2 = view.findViewById(R.id.digit2);

        digit1.setMaxValue(MAX_VALUE);
        digit1.setMinValue(MIN_VALUE);

        digit2.setMaxValue(MAX_VALUE);
        digit2.setMinValue(MIN_VALUE);


        float slipt = value / 10.0f;
        digit1.setValue((int)slipt);

        slipt = Math.round((slipt - (int)slipt) * 10);
        digit2.setValue((int)slipt);


        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);


        digit1.setOnValueChangedListener(this);
        digit2.setOnValueChangedListener(this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case DialogInterface.BUTTON_POSITIVE:
                if(onValueSetListener != null) onValueSetListener.onValueSet(value);
                break;

            default:
                dialogInterface.dismiss();
        }

    }

    public void setOnValueSetListener(OnValueSetListener onValueSetListener) {
        this.onValueSetListener = onValueSetListener;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        value = digit1.getValue() * 10;
        value += digit2.getValue();
    }


    public interface OnValueSetListener{
        void onValueSet(int value);
    }
}
