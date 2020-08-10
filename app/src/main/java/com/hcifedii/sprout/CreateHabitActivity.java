package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hcifedii.sprout.fragment.FrequencyFragment;
import com.hcifedii.sprout.fragment.GoalFragment;
import com.hcifedii.sprout.fragment.HabitTypeFragment;
import com.hcifedii.sprout.fragment.RemindersFragment;
import com.hcifedii.sprout.fragment.SnoozeFragment;
import com.hcifedii.sprout.fragment.TitleFragment;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    // Fragments of this activity
    TitleFragment titleFragment;
    HabitTypeFragment habitTypeFragment;
    FrequencyFragment frequencyFragment;
    RemindersFragment remindersFragment;
    SnoozeFragment snoozeFragment;
    GoalFragment goalFragment;

    // TODO: salvare lo stato di ogni fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        enableTopBackButton();

        // TODO: il titolo non può contenere una stringa vuota

        // FAB - Floating Action Button
        ExtendedFloatingActionButton saveFab = findViewById(R.id.fabSaveButton);
        saveFab.setOnClickListener(view -> {

            String title = titleFragment.getTitle();

            if (title.length() > 0) {
                // Clear error message
                titleFragment.setErrorMessage(null);

                // Save habit

            } else {
                titleFragment.setErrorMessage(getString(R.string.titleIsEmptyErrorString));
            }

            String message = goalFragment.getGoalType().name();
            int data = goalFragment.getInt();
            message = message + " " + data;

            //goalFragment.getString();

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        });


        // Saving a reference to the fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        titleFragment = (TitleFragment) fragmentManager.findFragmentById(R.id.titleFragment);
        //habitTypeFragment = (HabitTypeFragment) fragmentManager.findFragmentById(R.id.habitTypeFragment);
        frequencyFragment = (FrequencyFragment) fragmentManager.findFragmentById(R.id.frequencyFragment);
        //remindersFragment = (RemindersFragment) fragmentManager.findFragmentById(R.id.reminderFragment);
        snoozeFragment = (SnoozeFragment) fragmentManager.findFragmentById(R.id.snoozeFragment);
        goalFragment = (GoalFragment) fragmentManager.findFragmentById(R.id.goalFragment);

    }


    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        } else {
            Log.e(logcatTag, "getSupportActionBar() returned null");
        }
    }
}