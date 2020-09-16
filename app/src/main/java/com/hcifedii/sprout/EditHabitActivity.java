package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

import io.realm.RealmList;
import model.Habit;
import model.Reminder;
import utils.HabitRealmManager;

public class EditHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - EditHabitActivity";
    private static final String IS_ALREADY_SHOWED = "alreadyShowed";

    // Fragments of this activity
    TitleFragment titleFragment;
    HabitTypeFragment habitTypeFragment;
    FrequencyFragment frequencyFragment;
    RemindersFragment remindersFragment;
    SnoozeFragment snoozeFragment;
    GoalFragment goalFragment;

    int habitId;
    Habit habit;

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
                int maxRepetitions = habitTypeFragment.getMaxRepetitions();

                // Frequency
                List<Days> frequency = frequencyFragment.getSelectedDays();
                if (frequency.size() < 1) {
                    // Warning Snackbar. The user hasn't selected any days of the week.
                    showErrorSnackbar(editFab, R.string.empty_frequency_warning);
                    return;
                }

                // Reminders
                RealmList<Reminder> reminders = remindersFragment.getReminderList();

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
                        //showErrorSnackbar(saveFab, R.string.error_deadline_is_empty);
                        return;
                    }
                } else {
                    goalIntValue = goalFragment.getInt();
                }

                // Set the habit fields. There is no need to create a new object. Just use the old
                // one and set each fields.
                habit.setTitle(title);
                habit.setHabitType(habitType);
                habit.setMaxRepetitions(maxRepetitions);
                if (maxRepetitions < habit.getRepetitions()) {
                    habit.setRepetitions(maxRepetitions);
                }
                habit.setFrequencyTest(frequency);
                habit.setFrequency(frequency);
                habit.setReminders(reminders);
                habit.setMaxSnoozes(snooze);
                habit.setGoalType(goalType);
                habit.setMaxAction(goalIntValue);
                habit.setMaxStreakValue(goalIntValue);
                habit.setFinalDate(goalLongValue);

                // Update the habit
                HabitRealmManager.saveOrUpdateHabit(habit);
                Toast.makeText(this, "Abitudine aggiornata!", Toast.LENGTH_SHORT).show();
                finish();

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
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> this.runOnUiThread(() -> {
            if (scrollY > oldScrollY) {
                // Scroll down
                editFab.shrink();
            } else {
                // Scroll up
                editFab.extend();
            }
        }));

        // If savedInstance is null then get the data from the database. Else (ex. on screen
        // rotation) each fragments will recover the data they saved.
        if (savedInstanceState == null) {
            habitId = getIntent().getIntExtra("HABIT_ID", -1);

            if (habitId >= 0) {
                // Select habit from the database
                habit = HabitRealmManager.getHabit(habitId);

                if (habit != null) {

                    this.runOnUiThread(() -> {

                        // Set Habit information
                        titleFragment.setTitle(habit.getTitle());
                        habitTypeFragment.setHabitType(habit.getHabitType());
                        habitTypeFragment.setMaxRepetitions(habit.getMaxRepetitions());
                        frequencyFragment.setFrequency(habit.getFrequency());
                        remindersFragment.setReminderList(habit.getReminders());
                        snoozeFragment.setSnooze(habit.getMaxSnoozes());

                        GoalType goalType = habit.getGoalType();
                        goalFragment.setGoalType(goalType);
                        // Goal
                        if (goalType == GoalType.ACTION)
                            goalFragment.setInt(habit.getMaxAction());
                        else if (goalType == GoalType.STREAK)
                            goalFragment.setInt(habit.getMaxStreakValue());
                        else if (goalType == GoalType.DEADLINE)
                            goalFragment.setLong(habit.getFinalDate());


                    });


                }


            } else {
                // Go to MainActivity with log error message or throw an exception

            }

        }


    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_ALREADY_SHOWED, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_edit_habit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteHabitButton) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

            builder.setTitle("Sprout");
            builder.setMessage(R.string.delete_habit_dialog_message);
            builder.setPositiveButton(R.string.positive_delete_habit_dialog, (dialogInterface, i) -> {
                HabitRealmManager.deleteHabit(habitId);
                Toast.makeText(getBaseContext(), "Abitudine eliminata!", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorSnackbar(View view, int messageResId) {
        Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.redColor, getTheme()))
                .setAnchorView(view)
                .show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void enableTopBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
