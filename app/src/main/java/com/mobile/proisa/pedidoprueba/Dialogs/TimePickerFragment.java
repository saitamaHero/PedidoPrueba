package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;
    private Date date;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setListener(listener);

        return fragment;
    }

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener, Date date) {
        Bundle args = new Bundle();
        args.putSerializable("date",date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            date = (Date) getArguments().get("date");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default time in the picker

        final Calendar c;

        if(date != null){
            c = Calendar.getInstance();
            c.setTime(date);
        }else{
            c = Calendar.getInstance();
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);



        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minute, false);
    }

}