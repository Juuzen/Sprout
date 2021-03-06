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

import io.realm.RealmList;
import model.Reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    private RealmList<Reminder> reminderList = new RealmList<>();
    private RemindersAdapter remindersAdapter;

    private static final String REMINDERS_LIST_KEY = "reminders_list";

    public RemindersFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            Serializable serializable = savedInstanceState.getSerializable(REMINDERS_LIST_KEY);

            if (serializable instanceof List) {
                List<Reminder> savedReminders = (List<Reminder>) serializable;
                reminderList.clear();
                reminderList.addAll(savedReminders);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (reminderList.size() > 0) {
            // I have to make a new list because RealmList cannot be cast to Serializable.
            List<Reminder> list = new ArrayList<>(reminderList);

            outState.putSerializable(REMINDERS_LIST_KEY, (Serializable) list);
        }
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

        remindersAdapter = new RemindersAdapter(reminderList);
        remindersRecyclerView.setAdapter(remindersAdapter);

        // Set up the + button with a click listener
        ImageButton addButton = view.findViewById(R.id.addReminder);
        addButton.setOnClickListener(addButtonView -> {

            reminderList.add(new Reminder(12, 0));
            remindersAdapter.notifyDataSetChanged();

        });

        return view;
    }

    public RealmList<Reminder> getReminderList() {
        return reminderList;
    }

    public void setReminderList(RealmList<Reminder> reminders) {

        if (reminders != null && reminders.size() > 0) {
            // Delete the existent reminders and add the new ones
            reminderList.clear();
            reminderList.addAll(reminders);
            remindersAdapter.notifyDataSetChanged();
        }
    }
}