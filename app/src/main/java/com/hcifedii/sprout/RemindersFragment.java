package com.hcifedii.sprout;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hcifedii.sprout.adapter.RemindersAdapter;

import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    private List<RemindersAdapter.Reminder> reminderList;

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
        reminderList = new ArrayList<>();

        // Set up the RecyclerView with the layout manager and the adapter
        RecyclerView remindersRecyclerView = view.findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        RemindersAdapter adp = new RemindersAdapter(reminderList, getFragmentManager());
        remindersRecyclerView.setAdapter(adp);



        // Set up the + button with a click listener
        ImageButton addButton = view.findViewById(R.id.addReminder);
        addButton.setOnClickListener(view1 -> {


            TextView content = new TextView(view1.getContext());

            reminderList.add(new RemindersAdapter.Reminder(content, new ImageButton(view1.getContext()), new ImageButton(view1.getContext())));
            adp.notifyDataSetChanged();


        });

        return view;
    }


}