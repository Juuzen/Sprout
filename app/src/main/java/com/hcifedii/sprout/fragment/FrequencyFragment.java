package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.hcifedii.sprout.Days;
import com.hcifedii.sprout.R;

import java.time.DayOfWeek;
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