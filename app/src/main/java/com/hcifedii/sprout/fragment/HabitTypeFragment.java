package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hcifedii.sprout.HabitType;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.fragment.habitType.ClassicTypeFragment;
import com.hcifedii.sprout.fragment.habitType.CounterTypeFragment;
import com.hcifedii.sprout.fragment.habitType.HabitTypeInterface;

import java.util.HashMap;
import java.util.Map;

import model.Habit;

public class HabitTypeFragment extends Fragment {

    private ViewPager2 habitTypeViewPager;

    Map<HabitType, HabitTypeInterface> habitTypeFragmentMap = new HashMap<>();

    public HabitTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            FragmentManager manager = getChildFragmentManager();

            for (HabitType key : HabitType.values()) {
                Fragment fragment = manager.getFragment(savedInstanceState, key.name());

                // If there is an actual fragment saved with that name, then put it inside the map.
                if (fragment != null)
                    habitTypeFragmentMap.put(key, (HabitTypeInterface) fragment);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager manager = getChildFragmentManager();

        // For each fragment inside the map, save it inside the outState
        for (HabitType key : habitTypeFragmentMap.keySet()) {
            Fragment fragment = (Fragment) habitTypeFragmentMap.get(key);

            if (fragment != null && fragment.isAdded())
                manager.putFragment(outState, key.name(), fragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_habit_type, container, false);

        habitTypeViewPager = view.findViewById(R.id.habitTypeViewPager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getChildFragmentManager(), getLifecycle());

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

        HabitType habitType = getHabitTypeByPosition(habitTypeViewPager.getCurrentItem());
        HabitTypeInterface fragment = habitTypeFragmentMap.get(habitType);

        if (fragment != null) {
            return fragment.getRepetitions();
        }
        return 1;
    }

    public HabitType getHabitType() {
        return getHabitTypeByPosition(habitTypeViewPager.getCurrentItem());
    }

    private HabitType getHabitTypeByPosition(int position) {

        if (position == 1) {
            return HabitType.COUNTER;
        }
        return HabitType.CLASSIC;
    }

    private  int getPositionByHabitType(HabitType habitType){
        if (habitType == HabitType.COUNTER)
            return 1;
        return 0;
    }

    public void setHabitType(String habitType) {

        int position = 0;
        if(habitType != null){
            HabitType type = HabitType.valueOf(habitType);
            position = getPositionByHabitType(type);
        }

        habitTypeViewPager.setCurrentItem(position);
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

            if (position == 1) {
                habitTypeFragment = new CounterTypeFragment();
                habitTypeFragmentMap.put(HabitType.COUNTER, (HabitTypeInterface) habitTypeFragment);
            } else {
                habitTypeFragment = new ClassicTypeFragment();
                habitTypeFragmentMap.put(HabitType.CLASSIC, (HabitTypeInterface) habitTypeFragment);
            }
            return habitTypeFragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}