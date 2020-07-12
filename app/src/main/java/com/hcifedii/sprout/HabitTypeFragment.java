package com.hcifedii.sprout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

public class HabitTypeFragment extends Fragment {

    private static final String logcatTag = "Sprout - HabitTypeFragment";
    private int repetitions = 1;
    View root;

    public HabitTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_habit_type, container, false);

        setUpListener();

        return root;
    }

    public int getRepetitions() {
        return repetitions;
    }

    private void setUpListener() {
        // Habit type selection - Spinner
        Spinner habitTypeSpinner = root.findViewById(R.id.habitTypeSpinner);

        NumberPicker numberPicker = root.findViewById(R.id.repetitionsPicker);

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> repetitions = newVal);

        habitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                TextView informationMessage = root.findViewById(R.id.informationMessage);
                LinearLayout counterContainer = root.findViewById(R.id.counterContainer);

                switch (i) {
                    case 0:
                        // Classic Habit
                        informationMessage.setText(R.string.infoClassicHabit);
                        counterContainer.setVisibility(View.GONE);

                        numberPicker.setValue(1);
                        repetitions = 1;

                        break;
                    case 1:
                        // Counter Habit
                        informationMessage.setText(R.string.infoCounterHabit);
                        counterContainer.setVisibility(View.VISIBLE);

                        numberPicker.setValue(3);
                        repetitions = 3;

                        break;
                    default:
                        Log.e(logcatTag, "Spinner item select returned \"default\"");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


}