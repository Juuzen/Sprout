package com.hcifedii.sprout.fragment.goal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcifedii.sprout.R;
import com.shawnlin.numberpicker.NumberPicker;

public class GoalActionFragment extends Fragment {

    NumberPicker actionPicker;

    public GoalActionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_goal_action, container, false);

        actionPicker = view.findViewById(R.id.goalMaxActionPicker);

        return view;
    }

    public int getMaxAction() {
        return actionPicker.getValue();
    }
}