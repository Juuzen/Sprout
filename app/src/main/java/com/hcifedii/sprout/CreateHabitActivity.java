package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hcifedii.sprout.adapter.RemindersAdapter;

import java.util.Iterator;
import java.util.List;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    HabitTypeFragment habitTypeFragment;
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

            String title = titleEditText.getText().toString();

            if (title.length() > 0) {

                // Clear error message
                titleLayout.setError(null);

                // Save habit

            } else {
                titleLayout.setError(getString(R.string.titleIsEmptyError));
            }




        });


        // Saving a reference to the fragments
        habitTypeFragment = (HabitTypeFragment) getSupportFragmentManager().findFragmentById(R.id.habitTypeFragment);
        remindersFragment = (RemindersFragment) getSupportFragmentManager().findFragmentById(R.id.reminderFragment);





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