package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hcifedii.sprout.R;
import com.hcifedii.sprout.adapter.RemindersAdapter;
import com.hcifedii.sprout.adapter.RemindersAdapter.Reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    private List<Reminder> reminderList = new ArrayList<>();

    private static final String REMINDERS_LIST_KEY = "reminders_list";


    public RemindersFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            Serializable serializable = savedInstanceState.getSerializable(REMINDERS_LIST_KEY);

            if (serializable instanceof List) {

                List<? extends Reminder> savedReminders = (List<? extends Reminder>) serializable;
                reminderList.addAll(savedReminders);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (reminderList.size() > 0)
            outState.putSerializable(REMINDERS_LIST_KEY, (Serializable) reminderList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // Set up the RecyclerView with the layout manager and the adapter
        RecyclerView remindersRecyclerView = view.findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        RemindersAdapter remindersAdapter = new RemindersAdapter(reminderList);
        remindersRecyclerView.setAdapter(remindersAdapter);

        // Set up the + button with a click listener
        ImageButton addButton = view.findViewById(R.id.addReminder);
        addButton.setOnClickListener(addButtonView -> {

            reminderList.add(new Reminder(12, 0));
            remindersAdapter.notifyDataSetChanged();

        });

        return view;
    }

    public List<Reminder> getReminderList() {
        return reminderList;
    }

}