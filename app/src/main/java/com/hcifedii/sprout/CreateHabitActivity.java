package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hcifedii.sprout.fragment.FrequencyFragment;
import com.hcifedii.sprout.fragment.HabitTypeFragment;
import com.hcifedii.sprout.fragment.RemindersFragment;
import com.hcifedii.sprout.fragment.TitleFragment;

import java.util.Iterator;
import java.util.List;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    // Fragments of this activity
    TitleFragment titleFragment;
    HabitTypeFragment habitTypeFragment;
    FrequencyFragment frequencyFragment;
    RemindersFragment remindersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        enableTopBackButton();

        // FAB - Floating Action Button
        ExtendedFloatingActionButton saveFab = findViewById(R.id.fabSaveButton);
        saveFab.setOnClickListener(view -> {

            TextInputLayout titleLayout = findViewById(R.id.titleLayout);
            EditText titleEditText = findViewById(R.id.titleField);

            String title = titleFragment.getTitle();

            if (title.length() > 0) {
                // Clear error message
                titleFragment.setErrorMessage(null);

                // Save habit

            } else {
                titleFragment.setErrorMessage(getString(R.string.titleIsEmptyError));
            }



        });


        // Saving a reference to the fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        titleFragment = (TitleFragment) fragmentManager.findFragmentById(R.id.titleFragment);
        habitTypeFragment = (HabitTypeFragment) fragmentManager.findFragmentById(R.id.habitTypeFragment);
        frequencyFragment = (FrequencyFragment) fragmentManager.findFragmentById(R.id.frequencyFragment);
        remindersFragment = (RemindersFragment) fragmentManager.findFragmentById(R.id.reminderFragment);


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