package com.example.taskhero.ui.base;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public abstract class BaseTaskFormFragment extends Fragment {

    protected final Calendar calendar = Calendar.getInstance();

    protected abstract TextView getDateTextView();
    protected abstract TextView getTimeTextView();

    protected void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        new DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    protected void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        };

        new TimePickerDialog(getContext(), timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    protected void updateDateLabel() {
        String dateString = android.text.format.DateFormat.getMediumDateFormat(getContext()).format(calendar.getTime());
        getDateTextView().setText(dateString);
    }

    protected void updateTimeLabel() {
        String timeString = android.text.format.DateFormat.getTimeFormat(getContext()).format(calendar.getTime());
        getTimeTextView().setText(timeString);
    }
}