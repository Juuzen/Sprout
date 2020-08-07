package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

        ViewPager2 cardViewPager = view.findViewById(R.id.cardViewPager);
        cardViewPager.setAdapter(new ViewPagerFragmentAdapter(getChildFragmentManager(), getLifecycle()));
        cardViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        TabLayout dots = view.findViewById(R.id.viewPagerDots);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(dots, cardViewPager, true,
                (tab, position) -> {
                    // position of the current tab and that tab
                });
        tabLayoutMediator.attach();


//        Spinner goalSpinner = view.findViewById(R.id.goalSpinner);
//        goalSpinner.setOnItemSelectedListener(new GoalSpinnerListener());

        return view;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    // Return the data from the fragments
    public int getInt() {
        if (goalInterface != null)
            return goalInterface.getInt();

        return -1;
    }

    public String getString() {
        if (goalInterface != null)
            return goalInterface.getString();

        return null;
    }
//
//
//    private class GoalSpinnerListener implements AdapterView.OnItemSelectedListener {
//
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//            Fragment fragment = null;
//
//            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//
//            switch (i) {
//                case 0:
//                    // NONE. No layout is shown
//                    if(goalInterface != null){
//                        // Delete the fragment, if there is one
//                        transaction.remove((Fragment) goalInterface);
//                    }
//
//                    goalType = GoalType.NONE;
//                    break;
//                case 1:
//                    // ACTION
//                    fragment = new GoalActionFragment();
//                    transaction.replace(R.id.goalFragmentContainer, fragment);
//
//                    goalType = GoalType.ACTION;
//
//                    break;
//                case 2:
//                    // DEADLINE
//                    fragment = new GoalDeadlineFragment();
//                    transaction.replace(R.id.goalFragmentContainer, fragment);
//
//                    //date
//                    goalType = GoalType.DEADLINE;
//
//                    break;
//                case 3:
//                    // STREAK
//                    fragment = new GoalStreakFragment();
//                    transaction.replace(R.id.goalFragmentContainer, fragment);
//
//                    goalType = GoalType.STREAK;
//
//                    // Nel layout, mettere sotto il picker "giorni"
//                    break;
//                default:
//                    break;
//            }
//            // Apply changes
//            transaction.commit();
//            goalInterface = (GoalInterface) fragment;
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//            // Do nothing
//        }
//    }


    private static class ViewPagerFragmentAdapter extends FragmentStateAdapter {


        public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new NoGoalFragment();
                case 1:
                    return new GoalActionFragment();
                case 2:
                    return new GoalDeadlineFragment();
                case 3:
                    return new GoalStreakFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }


    }

}