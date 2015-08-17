package praveenkumar.clublife;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


import java.util.Calendar;

/**
 * Created by Praveen kumar on 27/07/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
DatePickListener listener;
    void setDatePickListener(DatePickListener listener){
        this.listener=listener;

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String date=year+"-"+(month+1)+"-"+day;
        listener.onDateSet(date);
    }
}