package com.hcifedii.sprout.fragment.goal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.hcifedii.sprout.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class GoalDeadlineFragment extends Fragment implements GoalInterface {

    private long timeInMills;
    private String timeString;

    private static final String TIME_IN_MILLS_KEY = "mills";
    private static final String TIME_STRING_KEY = "millsString";

    private MaterialButton dateButton;

    public GoalDeadlineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            timeInMills = savedInstanceState.getLong(TIME_IN_MILLS_KEY);
            timeString = savedInstanceState.getString(TIME_STRING_KEY);

            if(timeString != null)
                dateButton.setText(timeString);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(TIME_IN_MILLS_KEY, timeInMills);
        outState.putString(TIME_STRING_KEY, timeString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goal_deadline, container, false);

        dateButton = view.findViewById(R.id.dateButton);

        dateButton.setOnClickListener(view1 -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

            // Set minimum valid date
            CalendarConstraints.Builder calendarConstraint = new CalendarConstraints.Builder();
            calendarConstraint.setValidator(DateValidatorPointForward.now());

            // Set date picker's title and default date
            builder.setTitleText(R.string.datePickerString);

            long timeInMills = Calendar.getInstance().getTimeInMillis();
            this.timeInMills = timeInMills;

            builder.setSelection(timeInMills);

            // Build the picker
            builder.setCalendarConstraints(calendarConstraint.build());
            MaterialDatePicker<Long> datePicker = builder.build();

            datePicker.show(getChildFragmentManager(), datePicker.toString());

            // Add listener
            datePicker.addOnPositiveButtonClickListener(selectedDate -> {

                DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(getContext());

                this.timeInMills = selectedDate;
                this.timeString = dateFormatter.format(new Date(selectedDate));

                dateButton.setText(this.timeString);
            });
        });

        return view;
    }

    @Override
    public @Nullable
    String getString() {
        return timeString;
    }
}