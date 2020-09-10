package com.hcifedii.sprout.fragment.goal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.GoalFragment;
import com.shawnlin.numberpicker.NumberPicker;

public class GoalStreakFragment extends Fragment implements GoalInterface {

    private NumberPicker streakPicker;
    private GoalFragment parent;

    private static final String STREAK_PICKER_VALUE_KEY = "streakVal";

    public GoalStreakFragment() {
        // Required empty public constructor
    }

    public GoalStreakFragment(GoalFragment goalFragment) {
        parent = goalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            int value = savedInstanceState.getInt(STREAK_PICKER_VALUE_KEY);
            streakPicker.setValue(value);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STREAK_PICKER_VALUE_KEY, streakPicker.getValue());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goal_streak, container, false);

        streakPicker = view.findViewById(R.id.goalStreakPicker);

        setDefaultValue();

        return view;
    }

    private void setDefaultValue() {
        if (parent != null) {
            int defaultValue = parent.getDefaultInt();
            if (defaultValue > 0) {
                setInt(defaultValue);
            }
        }
    }

    @Override
    public int getInt() {
        return streakPicker.getValue();
    }

    @Override
    public void setInt(int value) {
        streakPicker.setValue(value);
    }
}