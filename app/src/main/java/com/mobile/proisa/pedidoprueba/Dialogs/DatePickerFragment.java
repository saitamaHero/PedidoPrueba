package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;
    private Date date;
    private Date minDate;


    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();

        fragment.setListener(listener);

        return fragment;
    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, Date startDate) {
        Bundle args = new Bundle();
        args.putSerializable("date",startDate);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        fragment.setListener(listener);

        return fragment;
    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, Date startDate, Date minDate) {
        Bundle args = new Bundle();
        args.putSerializable("date",startDate);
        args.putSerializable("min_date", minDate);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            date = (Date) getArguments().get("date");

            if(getArguments().containsKey("min_date")){
                minDate = (Date) getArguments().getSerializable("min_date");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c;
        if(date != null){
            c = Calendar.getInstance();
            c.setTime(date);
        }else{
            c = Calendar.getInstance();
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it

        DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(), listener, year, month, day);

        if(minDate != null)
        {
           DatePicker datePicker = datePickerDialog.getDatePicker();
           datePicker.setMinDate(minDate.getTime());
        }

        return datePickerDialog;
    }


}