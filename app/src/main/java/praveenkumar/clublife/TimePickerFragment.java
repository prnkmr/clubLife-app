package praveenkumar.clublife;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Praveen kumar on 27/07/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    TimePickListener listener;
    void setTimePickListener(TimePickListener listener){
        this.listener=listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time=hourOfDay+":"+minute+":00";
        listener.onTimeSet(time);
    }
}
