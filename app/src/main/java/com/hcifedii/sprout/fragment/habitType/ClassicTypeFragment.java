package com.hcifedii.sprout.fragment.habitType;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcifedii.sprout.R;

public class ClassicTypeFragment extends Fragment implements HabitTypeInterface {

    public ClassicTypeFragment() {
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
        return inflater.inflate(R.layout.fragment_habit_type_classic, container, false);
    }

    @Override
    public int getRepetitions() {
        return 1;
    }

    @Override
    public void setRepetitions(int repetitions) {
        // Do nothing
    }
}