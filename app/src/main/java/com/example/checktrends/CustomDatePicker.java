package com.example.checktrends;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CustomDatePicker extends DialogFragment{
    int year,month,day;
    GregorianCalendar maxDate,minDate;

    public CustomDatePicker(Calendar calendar, GregorianCalendar maxDate, GregorianCalendar minDate){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    public interface CustomDatePickerListener{
        void onDateSet(int year, int month, int day);
    }

    private CustomDatePickerListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CustomDatePicker.CustomDatePickerListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString() + "はインターフェースを実装していません");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                listener.onDateSet(year, month + 1, day);
            }
        },year,month,day);

        android.widget.DatePicker datePicker = datePickerDialog.getDatePicker();

        if(maxDate != null){
            datePicker.setMaxDate(maxDate.getTimeInMillis());
        }

        if(minDate != null){
            datePicker.setMinDate(minDate.getTimeInMillis());
        }

        return datePickerDialog;
    }

}
