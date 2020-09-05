package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hcifedii.sprout.enumerations.Days;
import com.hcifedii.sprout.enumerations.GoalType;
import com.hcifedii.sprout.enumerations.HabitType;
import com.hcifedii.sprout.fragment.FrequencyFragment;
import com.hcifedii.sprout.fragment.GoalFragment;
import com.hcifedii.sprout.fragment.HabitTypeFragment;
import com.hcifedii.sprout.fragment.RemindersFragment;
import com.hcifedii.sprout.fragment.SnoozeFragment;
import com.hcifedii.sprout.fragment.TitleFragment;

import java.util.List;

import model.Habit;
import model.Reminder;
import utils.HabitRealmManager;

public class EditHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - EditHabitActivity";

    // Fragments of this activity
    TitleFragment titleFragment;
    HabitTypeFragment habitTypeFragment;
    FrequencyFragment frequencyFragment;
    RemindersFragment remindersFragment;
    SnoozeFragment snoozeFragment;
    GoalFragment goalFragment;

    int habitId;
    Habit habit;
    Context myContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);
        enableTopBackButton();

        // FAB - Floating Action Button
        ExtendedFloatingActionButton editFab = findViewById(R.id.fabEditButton);
        editFab.setOnClickListener(fabView -> {
            String title = titleFragment.getTitle();
            if (title.length() > 0) {
                // Clear error message
                titleFragment.setErrorMessage(null);
                // Recover the data from the fragments
                // Habit type
                HabitType habitType = habitTypeFragment.getHabitType();
                int repetitions = habitTypeFragment.getRepetitions();

                // Frequency
                List<Days> frequency = frequencyFragment.getSelectedDays();

                if (frequency.size() < 1) {
                    // Warning Snackbar. The user hasn't selected any days of the week.
                    showErrorSnackbar(editFab, R.string.empty_frequency_warning);
                    return;
                }

                // Reminders
                List<Reminder> reminders = remindersFragment.getReminderList();

                // Snooze
                boolean isSnoozeEnabled = snoozeFragment.isSnoozeEnabled();
                int snooze = 0;
                if (isSnoozeEnabled) {
                    snooze = snoozeFragment.getMaxSnoozes();
                }

                // Goal
                GoalType goalType = goalFragment.getGoalType();

                long goalLongValue = 0;
                int goalIntValue = 0;

                if (goalType == GoalType.DEADLINE) {
                    goalLongValue = goalFragment.getLong();

                    if (goalLongValue < 0) {
                        showErrorSnackbar(editFab, R.string.error_deadline_is_empty);
                        return;
                    }
                } else {
                    goalIntValue = goalFragment.getInt();
                }

                // Update the habit


            } else {
                titleFragment.setErrorMessage(getString(R.string.error_title_is_empty));
                showErrorSnackbar(editFab, R.string.error_title_is_empty);
            }
        });

        // Saving a reference to each fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        titleFragment = (TitleFragment) fragmentManager.findFragmentById(R.id.titleFragment);
        habitTypeFragment = (HabitTypeFragment) fragmentManager.findFragmentById(R.id.habitTypeFragment);
        frequencyFragment = (FrequencyFragment) fragmentManager.findFragmentById(R.id.frequencyFragment);
        remindersFragment = (RemindersFragment) fragmentManager.findFragmentById(R.id.reminderFragment);
        snoozeFragment = (SnoozeFragment) fragmentManager.findFragmentById(R.id.snoozeFragment);
        goalFragment = (GoalFragment) fragmentManager.findFragmentById(R.id.goalFragment);


        // Shrinking / extending behaviour of the fab
        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            this.runOnUiThread(() -> {
                if (scrollY > oldScrollY) {
                    // Scroll down
                    editFab.shrink();
                } else {
                    // Scroll up
                    editFab.extend();
                }
            });
        });

        // getting the habits informations from DB

        habitId = getIntent().getIntExtra("HABIT_ID", -1);

        if (habitId >= 0) {
            // Select habit from the database
            habit = HabitRealmManager.getHabit(habitId);



        } else {
            // Go to MainActivity with log error message

        }


    }



    @Override
    protected void onStart() {
        super.onStart();

        if (habit != null) {

            GoalType goalType = habit.getGoalType();

            // TODO: queste due card non visualizzano correttamente i propri dati
            // Set ViewPager2 pages
            goalFragment.setGoalType(goalType);
            habitTypeFragment.setHabitType(habit.getHabitType());

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Set Habit information
                    titleFragment.setTitle(habit.getTitle());
                    habitTypeFragment.setRepetitions(habit.getRepetitions());
                    frequencyFragment.setFrequency(habit.getFrequency());
                    remindersFragment.setReminderList(habit.getReminders());
                    snoozeFragment.setSnooze(habit.getMaxSnoozes());

                    // Goal
                    if (goalType == GoalType.ACTION)
                        goalFragment.setInt(habit.getMaxAction());
                    else if (goalType == GoalType.STREAK)
                        goalFragment.setInt(habit.getMaxStreakValue());
                    else if (goalType == GoalType.DEADLINE)
                        goalFragment.setLong(habit.getFinalDate());



                }
            });

            
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_edit_habit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteHabitButton:
                new AlertDialog.Builder(this)
                        .setTitle("Cancellare l'abitudine?")
                        .setMessage("Non potrai tornare indietro!")
                        .setPositiveButton("Sì, cancella", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HabitRealmManager.deleteHabit(habitId);
                                Toast.makeText(myContext, "Abitudine eliminata!", Toast.LENGTH_SHORT).show();
                                finish(); //FIXME: transizioni
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorSnackbar(View view, int messageResId) {
        Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.redColor, getTheme()))
                .setAnchorView(view)
                .show();
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