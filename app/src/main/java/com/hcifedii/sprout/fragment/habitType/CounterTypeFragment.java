package com.hcifedii.sprout.fragment.habitType;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcifedii.sprout.R;
import com.shawnlin.numberpicker.NumberPicker;

public class CounterTypeFragment extends Fragment implements HabitTypeInterface {

    private NumberPicker picker;

    public CounterTypeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_counter_type, container, false);

        picker = view.findViewById(R.id.repetitionsPicker);

        return view;
    }

    @Override
    public int getInt() {
        return picker.getValue();
    }
}