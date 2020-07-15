package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hcifedii.sprout.R;
import com.shawnlin.numberpicker.NumberPicker;

public class SnoozeFragment extends Fragment {

    View view;
    SwitchMaterial switchMaterial;
    NumberPicker snoozePicker;

    public SnoozeFragment() {
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
        view = inflater.inflate(R.layout.fragment_snooze, container, false);

        snoozePicker = view.findViewById(R.id.snoozePicker);

        // Set listener on the switch
        switchMaterial = view.findViewById(R.id.snoozeSwitch);

        switchMaterial.setOnCheckedChangeListener((switchButton, isSwitchChecked) -> {

            LinearLayout snoozePickerContainer = view.findViewById(R.id.snoozePickerContainer);

            snoozePickerContainer.setVisibility(isSwitchChecked ? View.VISIBLE : View.GONE);
        });

        return view;
    }

    public boolean isSnoozeEnabled() {
        return switchMaterial.isChecked();
    }

    public int getMaxSnoozes() {

        if (switchMaterial.isChecked()) {
            return snoozePicker.getValue();
        }
        return 0;
    }

}