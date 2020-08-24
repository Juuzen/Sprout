package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private static final String IS_SWITCH_ENABLED_KEY = "switchMaterial";
    private static final String SNOOZE_PICKER_VALUE_KEY = "snoozeVal";

    public SnoozeFragment() {
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
            boolean isEnabled = savedInstanceState.getBoolean(IS_SWITCH_ENABLED_KEY);
            int value = savedInstanceState.getInt(SNOOZE_PICKER_VALUE_KEY);

            switchMaterial.setSelected(isEnabled);
            snoozePicker.setValue(value);

            LinearLayout container = view.findViewById(R.id.snoozePickerContainer);

            container.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_SWITCH_ENABLED_KEY, switchMaterial.isSelected());
        outState.putInt(SNOOZE_PICKER_VALUE_KEY, snoozePicker.getValue());
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

    public void setSnooze(int maxSnoozes) {
        if(maxSnoozes > 0){
            switchMaterial.setChecked(true);
            snoozePicker.setValue(maxSnoozes);
        }else{
            switchMaterial.setChecked(false);
        }
    }
}