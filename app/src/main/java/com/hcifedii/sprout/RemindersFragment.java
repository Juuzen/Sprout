package com.hcifedii.sprout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hcifedii.sprout.adapter.RemindersAdapter;

import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    List<RemindersAdapter.Reminder> reminders;

    public RemindersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // Set up the the arrayList that holds the list of reminders
        reminders = new ArrayList<>();

        // Set up the RecyclerView with the layout manager and the adapter
        RecyclerView remindersRecyclerView = view.findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        RemindersAdapter remindersAdapter = new RemindersAdapter(reminders);
        remindersRecyclerView.setAdapter(remindersAdapter);

        // Set up the + button with a click listener
        ImageButton addButton = view.findViewById(R.id.addReminder);
        addButton.setOnClickListener(addButtonView -> {

            reminders.add(new RemindersAdapter.Reminder(12, 0));
            remindersAdapter.notifyDataSetChanged();

        });

        return view;
    }

    public List<RemindersAdapter.Reminder> getReminders() {
        return reminders;
    }


}