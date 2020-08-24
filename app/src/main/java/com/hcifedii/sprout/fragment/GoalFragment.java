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
import com.hcifedii.sprout.enumerations.GoalType;

import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.goal.*;

import java.util.HashMap;
import java.util.Map;

public class GoalFragment extends Fragment {

    ViewPager2 cardViewPager;
    ViewPagerFragmentAdapter adapter;

    // Container for the fragment's references
    Map<GoalType, GoalInterface> goalFragmentMap = new HashMap<>();

    public GoalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method retrieves the data of this fragment, if there is some.
     *
     * @param savedInstanceState Bundle with the data.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            FragmentManager manager = getChildFragmentManager();

            for (GoalType key : GoalType.values()) {
                Fragment fragment = manager.getFragment(savedInstanceState, key.name());

                // If there is an actual fragment saved with that name, then put it inside the map.
                if (fragment != null)
                    goalFragmentMap.put(key, (GoalInterface) fragment);
            }
        }
    }

    /**
     * This method saves the data of this fragment.
     *
     * @param outState Bundle used to save the data.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager manager = getChildFragmentManager();

        // For each fragment inside the map, save it inside the outState
        for (GoalType key : goalFragmentMap.keySet()) {
            Fragment fragment = (Fragment) goalFragmentMap.get(key);

            if (fragment != null && fragment.isAdded())
                manager.putFragment(outState, key.name(), fragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        // ViewPager2 set up
        cardViewPager = view.findViewById(R.id.cardViewPager);
        adapter = new ViewPagerFragmentAdapter(getChildFragmentManager(), getLifecycle());

        cardViewPager.setAdapter(adapter);
        cardViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        cardViewPager.setOffscreenPageLimit(adapter.NUM_PAGES);

        TabLayout dots = view.findViewById(R.id.viewPagerDots);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(dots, cardViewPager, true,
                (tab, position) -> {
                    // position of the current tab and that tab
                });
        tabLayoutMediator.attach();

        return view;
    }

    public GoalType getGoalType() {
        return getGoalTypeByPosition(cardViewPager.getCurrentItem());
    }

    private GoalType getGoalTypeByPosition(int position) {
        switch (position) {
            default:
                return GoalType.NONE;
            case 1:
                return GoalType.ACTION;
            case 2:
                return GoalType.DEADLINE;
            case 3:
                return GoalType.STREAK;
        }
    }

    private int getPositionByGoalType(GoalType goalType){
        switch (goalType){
            default:
                return 0;
            case ACTION:
                return 1;
            case DEADLINE:
                return 2;
            case STREAK:
                return 3;
        }
    }

    // Return the data from the fragments
    public int getInt() {

        GoalType goalType = getGoalTypeByPosition(cardViewPager.getCurrentItem());
        GoalInterface fragment = goalFragmentMap.get(goalType);

        if (fragment != null)
            return fragment.getInt();

        return -1;
    }

    public long getLong() {

        GoalType goalType = getGoalTypeByPosition(cardViewPager.getCurrentItem());
        GoalInterface fragment = goalFragmentMap.get(goalType);

        if (fragment != null)
            return fragment.getLong();

        return -1;
    }

    public void setGoalType(GoalType goalType) {
        cardViewPager.setCurrentItem(getPositionByGoalType(goalType));
    }

    public void setInt(int value) {
        GoalType goalType = getGoalTypeByPosition(cardViewPager.getCurrentItem());
        GoalInterface fragment = goalFragmentMap.get(goalType);

        if (fragment != null)
            fragment.setInt(value);
    }

    public void setLong(long value) {
        GoalType goalType = getGoalTypeByPosition(cardViewPager.getCurrentItem());
        GoalInterface fragment = goalFragmentMap.get(goalType);

        if (fragment != null)
            fragment.setLong(value);
    }


    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public final int NUM_PAGES = 4;

        public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager,
                                        @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        /**
         * This method create a new Fragment or, if it already exists, recycle the old one.
         *
         * @param position Position of the fragment inside the ViewPager2.
         * @return Fragment The fragment that the ViewPager2 has to show.
         */
        @NonNull
        @Override
        public Fragment createFragment(int position) {

            Fragment goalFragment;

            switch (position) {
                default:
                    goalFragment = new NoGoalFragment();
                    goalFragmentMap.put(GoalType.NONE, (GoalInterface) goalFragment);
                    break;
                case 1:
                    goalFragment = new GoalActionFragment();
                    goalFragmentMap.put(GoalType.ACTION, (GoalInterface) goalFragment);
                    break;
                case 2:
                    goalFragment = new GoalDeadlineFragment();
                    goalFragmentMap.put(GoalType.DEADLINE, (GoalInterface) goalFragment);
                    break;
                case 3:
                    goalFragment = new GoalStreakFragment();
                    goalFragmentMap.put(GoalType.STREAK, (GoalInterface) goalFragment);
                    break;
            }
            return goalFragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}