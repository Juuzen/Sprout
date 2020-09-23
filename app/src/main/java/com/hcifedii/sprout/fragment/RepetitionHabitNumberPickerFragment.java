package com.hcifedii.sprout.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hcifedii.sprout.R;
import com.shawnlin.numberpicker.NumberPicker;

public class RepetitionHabitNumberPickerFragment extends AppCompatDialogFragment {
    private HabitNumberPickerListener listener;
    private int maxProgress;
    private int habitId;

    public RepetitionHabitNumberPickerFragment(int max, int id) {
        maxProgress = max;
        habitId = id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_counter_custom_number_picker, null);

        final NumberPicker picker = view.findViewById(R.id.counterPickerNumberPicker);
        picker.setMaxValue(maxProgress);
        picker.setMinValue(1);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.counter_habit_picker_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    int pickerValue = picker.getValue();
                    listener.applyTask(pickerValue, habitId);
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {

                })
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (HabitNumberPickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement HabitNumberPickerListener");
        }
    }

    public interface HabitNumberPickerListener {
        void applyTask(int value, int habitId);
    }
}
