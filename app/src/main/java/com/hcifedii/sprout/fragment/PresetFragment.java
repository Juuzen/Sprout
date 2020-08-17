package com.hcifedii.sprout.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.MaterialContainerTransform;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.adapter.PresetHabitAdapter;

import java.util.LinkedList;
import java.util.List;

import model.Habit;


public class PresetFragment extends Fragment {

    Context context;
    Listener viewListener;

    MaterialButton showMore;

    public PresetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_preset_list, container, false);

        context = view.getContext();

        RecyclerView presetHabitRecyclerView = view.findViewById(R.id.presetHabitRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        presetHabitRecyclerView.setLayoutManager(linearLayoutManager);

        // Set the listener to the recycler view
        viewListener = new Listener();
        PresetHabitAdapter adapter = new PresetHabitAdapter(createPresetHabitList(), viewListener);

        presetHabitRecyclerView.setAdapter(adapter);

        // Set the listener to the button
        showMore = view.findViewById(R.id.showMoreButton);

        showMore.setOnClickListener(new ShowMoreButtonListener(view));

        return view;
    }

    // Implementation of the listeners
    private static class ShowMoreButtonListener implements View.OnClickListener {

        View view;

        ShowMoreButtonListener(View view) {
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            // Material 1.2.0 required
            ViewGroup endView = view.findViewById(R.id.bottomContainer);

            MaterialContainerTransform transform = new MaterialContainerTransform();

            LinearLayout container = view.findViewById(R.id.groupContainer);

            transform.setScrimColor(Color.TRANSPARENT);
            TransitionManager.beginDelayedTransition(container);

            if (endView.getVisibility() == View.GONE) {
                endView.setVisibility(View.VISIBLE);
            } else {
                endView.setVisibility(View.GONE);
            }
        }
    }

    private class Listener implements PresetHabitAdapter.OnClickListener {

        private Habit habit;

        @Override
        public void onClick(Habit habit) {
            this.habit = habit;
            Toast.makeText(context, habit.getTitle(), Toast.LENGTH_SHORT).show();
        }

        public Habit getHabit() {
            return habit;
        }
    }


    private List<Habit> createPresetHabitList() {

        List<Habit> list = new LinkedList<>();

        Habit habit1 = new Habit(), habit2 = new Habit(), habit3 = new Habit();
        habit1.setTitle("Bevi più acqua");
        habit2.setTitle("Fai più sport");
        habit3.setTitle("Fai meditazione");

        list.add(habit1);
        list.add(habit2);
        list.add(habit3);

        return list;
    }
}