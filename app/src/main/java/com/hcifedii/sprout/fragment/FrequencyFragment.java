package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.hcifedii.sprout.Days;
import com.hcifedii.sprout.R;

import java.util.ArrayList;
import java.util.List;

public class FrequencyFragment extends Fragment {

    private static final String logcatTAG = "Sprout - FrequencyFragment";
    ChipGroup frequencyGroup;

    public FrequencyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            for (Days day : Days.values()) {
                boolean isSelected = savedInstanceState.getBoolean(day.name(), false);

                if (isSelected) {
                    switch (day) {
                        case MONDAY:
                            frequencyGroup.check(R.id.mondayChip);
                            break;
                        case TUESDAY:
                            frequencyGroup.check(R.id.tuesdayChip);
                            break;
                        case WEDNESDAY:
                            frequencyGroup.check(R.id.wednesdayChip);
                            break;
                        case THURSDAY:
                            frequencyGroup.check(R.id.thursdayChip);
                            break;
                        case FRIDAY:
                            frequencyGroup.check(R.id.fridayChip);
                            break;
                        case SATURDAY:
                            frequencyGroup.check(R.id.saturdayChip);
                            break;
                        case SUNDAY:
                            frequencyGroup.check(R.id.sundayChip);
                            break;
                    }
                }
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        List<Days> selectedDays = getSelectedDays();

        for (Days day : Days.values()) {
            if (selectedDays.contains(day)) {
                outState.putBoolean(day.name(), true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frequency, container, false);

        frequencyGroup = view.findViewById(R.id.frequencyGroup);

        return view;
    }

    public List<Days> getSelectedDays() {

        List<Days> selectedDays = new ArrayList<>();

        int childCounter = frequencyGroup.getChildCount();
        for (int i = 0; i < childCounter; i++) {

            Chip chip = (Chip) frequencyGroup.getChildAt(i);

            if (!chip.isChecked()) {
                continue;
            }

            // Add the checked chip to a list
            switch (chip.getId()) {
                case R.id.mondayChip:
                    selectedDays.add(Days.MONDAY);
                    break;
                case R.id.tuesdayChip:
                    selectedDays.add(Days.TUESDAY);
                    break;
                case R.id.wednesdayChip:
                    selectedDays.add(Days.WEDNESDAY);
                    break;
                case R.id.thursdayChip:
                    selectedDays.add(Days.THURSDAY);
                    break;
                case R.id.fridayChip:
                    selectedDays.add(Days.FRIDAY);
                    break;
                case R.id.saturdayChip:
                    selectedDays.add(Days.SATURDAY);
                    break;
                case R.id.sundayChip:
                    selectedDays.add(Days.SUNDAY);
                    break;
                default:
                    Log.e(logcatTAG, "chip.getID() returned default");
                    break;
            }
        }
        return selectedDays;
    }

}