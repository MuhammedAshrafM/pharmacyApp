package com.example.muhammed.pharmacyapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bahaa on 30/11/2017.
 */

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText txtdate;
    boolean aBoolean;
    Context contextClass;
    public DateDialog(View view, boolean bool) {
        txtdate = (EditText) view;
        aBoolean = bool;
    }
    public DateDialog(Context context, View view, boolean bool) {
        txtdate = (EditText) view;
        aBoolean = bool;
        contextClass = context;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);


    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        Calendar c = Calendar.getInstance();

// set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

// and get that as a Date
        Date today = c.getTime();

// or as a timestamp in milliseconds
        long todayInMillis = c.getTimeInMillis();

// UserClass-specified date which you are testing
// let's say the components come from a form or something

// reuse the calendar to set UserClass specified date
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

// and get that as a Date
        Date dateSpecified = c.getTime();


        if (aBoolean) {
            if (dateSpecified.before(today)) {

                String date = day + "-" + (month + 1) + "-" + year;
                txtdate.setText("");
                Toast.makeText(contextClass, "Please select early date", Toast.LENGTH_SHORT).show();
            } else {

                String date = year + "-" + (month + 1) + "-" + day;
                txtdate.setText(date);
            }
        }
        else {
            String date = year + "-" + (month + 1) + "-" + day;
            txtdate.setText(date);
        }
    }


}