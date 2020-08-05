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
import com.hcifedii.sprout.fragment.goal.*;

public class GoalFragment extends Fragment {

    GoalInterface goalInterface;
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
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        Spinner goalSpinner = view.findViewById(R.id.goalSpinner);
        goalSpinner.setOnItemSelectedListener(new GoalSpinnerListener());

        return view;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    // Return the data from the fragments
    public int getInt(){
        if(goalInterface != null)
            return goalInterface.getInt();

        return -1;
    }

    public String getString(){
        if(goalInterface != null)
            return goalInterface.getString();

        return null;
    }

    
    private class GoalSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            Fragment fragment = null;

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

            switch (i) {
                case 0:
                    // NONE. No layout is shown
                    if(goalInterface != null){
                        // Delete the fragment, if there is one
                        transaction.remove((Fragment) goalInterface);
                    }

                    goalType = GoalType.NONE;
                    break;
                case 1:
                    // ACTION
                    fragment = new GoalActionFragment();
                    transaction.replace(R.id.goalFragmentContainer, fragment);

                    goalType = GoalType.ACTION;

                    break;
                case 2:
                    // DEADLINE
                    fragment = new GoalDeadlineFragment();
                    transaction.replace(R.id.goalFragmentContainer, fragment);

                    //date
                    goalType = GoalType.DEADLINE;

                    break;
                case 3:
                    // STREAK
                    fragment = new GoalStreakFragment();
                    transaction.replace(R.id.goalFragmentContainer, fragment);

                    goalType = GoalType.STREAK;

                    // Nel layout, mettere sotto il picker "giorni"
                    break;
                default:
                    break;
            }
            // Apply changes
            transaction.commit();
            goalInterface = (GoalInterface) fragment;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // Do nothing
        }
    }

}