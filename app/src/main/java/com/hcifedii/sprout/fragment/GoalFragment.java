package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hcifedii.sprout.GoalType;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.goal.GoalActionFragment;

public class GoalFragment extends Fragment {

    View view;
    GoalType goalType = GoalType.NONE;

    public GoalFragment() {
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
        view = inflater.inflate(R.layout.fragment_goal, container, false);

        Spinner goalSpinner = view.findViewById(R.id.goalSpinner);
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                switch (i) {
                    case 0:
                        // NONE


                        goalType = GoalType.NONE;
                        break;
                    case 1:
                        // ACTION
                        transaction.replace(R.id.goalFragmentContainer, new GoalActionFragment()).commit();

                        goalType = GoalType.ACTION;
                        break;
                    case 2:
                        // DEADLINE
                        //date

                        goalType = GoalType.DEADLINE;
                        break;
                    case 3:
                        // STREAK
                        goalType = GoalType.STREAK;

                        // Nel layout, mettere sotto il picker "giorni"
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        return view;
    }

    public GoalType getGoalType() {
        return goalType;
    }
}