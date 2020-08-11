package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hcifedii.sprout.GoalType;
import com.hcifedii.sprout.HabitType;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.goal.GoalActionFragment;
import com.hcifedii.sprout.fragment.goal.GoalDeadlineFragment;
import com.hcifedii.sprout.fragment.goal.GoalInterface;
import com.hcifedii.sprout.fragment.goal.GoalStreakFragment;
import com.hcifedii.sprout.fragment.goal.NoGoalFragment;
import com.hcifedii.sprout.fragment.habitType.ClassicTypeFragment;
import com.hcifedii.sprout.fragment.habitType.CounterTypeFragment;
import com.hcifedii.sprout.fragment.habitType.HabitTypeInterface;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.HashMap;
import java.util.Map;

public class HabitTypeFragment extends Fragment {

    private ViewPager2 habitTypeViewPager;
    private ViewPagerFragmentAdapter adapter;

    Map<HabitType, HabitTypeInterface> habitTypeFragmentMap = new HashMap<>();

    private static final String logcatTag = "Sprout - HabitTypeFragment";
    private HabitType habitType = HabitType.Classic;
    private int repetitions = 1;

    public HabitTypeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_habit_type, container, false);

        habitTypeViewPager = view.findViewById(R.id.habitTypeViewPager);
        adapter = new ViewPagerFragmentAdapter(getChildFragmentManager(), getLifecycle());

        habitTypeViewPager.setAdapter(adapter);
        habitTypeViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Dots under the ViewPager
        TabLayout dots = view.findViewById(R.id.habitTypeViewPagerDots);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(dots, habitTypeViewPager, true,
                (tab, position) -> {
                    // position of the current tab and that tab
                });
        tabLayoutMediator.attach();



        return view;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public HabitType getHabitType(){
        return habitType;
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

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

            Fragment habitTypeFragment = null;

            switch (position) {
                case 0:
                    habitTypeFragment = new ClassicTypeFragment();
                    habitTypeFragmentMap.put(HabitType.Classic, (HabitTypeInterface) habitTypeFragment);
                    break;
                case 1:
                    habitTypeFragment = new CounterTypeFragment();
                    habitTypeFragmentMap.put(HabitType.Counter, (HabitTypeInterface) habitTypeFragment);
                    break;
            }
            return habitTypeFragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }


}