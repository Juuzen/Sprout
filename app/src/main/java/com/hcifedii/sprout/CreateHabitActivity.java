package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        setTopBarBackButton(true);

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

        // Habit type selection - Spinner
        Spinner habitTypeSpinner = findViewById(R.id.habitTypeSpinner);
        habitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                TextView informationMessage = findViewById(R.id.informationMessage);
                LinearLayout counterContainer = findViewById(R.id.counterContainer);

                switch (i) {
                    case 0:
                        // Classic Habit
                        informationMessage.setText(R.string.infoClassicHabit);
                        counterContainer.setVisibility(View.GONE);
                        break;
                    case 1:
                        // Counter Habit
                        informationMessage.setText(R.string.infoCounterHabit);
                        counterContainer.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Log.e(logcatTag, "Spinner item select returned \"default\"");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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