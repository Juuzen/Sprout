package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        setTopBarBackButton(true);

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





    }


    private void setTopBarBackButton(boolean flag) {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(flag);
        } else {
            Log.e(logcatTag, "getSupportActionBar() returned null");
        }
    }
}