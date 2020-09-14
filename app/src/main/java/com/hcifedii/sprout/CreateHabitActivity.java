package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

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
import com.hcifedii.sprout.fragment.PresetFragment;
import com.hcifedii.sprout.fragment.RemindersFragment;
import com.hcifedii.sprout.fragment.SnoozeFragment;
import com.hcifedii.sprout.fragment.TitleFragment;

import java.util.Calendar;
import java.util.List;

import model.Habit;
import io.realm.RealmList;
import model.Reminder;
import utils.HabitRealmManager;
import utils.NotificationAlarmManager;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    // Fragments of this activity
    PresetFragment presetFragment;

    TitleFragment titleFragment;
    HabitTypeFragment habitTypeFragment;
    FrequencyFragment frequencyFragment;
    RemindersFragment remindersFragment;
    SnoozeFragment snoozeFragment;
    GoalFragment goalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        enableTopBackButton();

        // FAB - Floating Action Button
        ExtendedFloatingActionButton saveFab = findViewById(R.id.fabSaveButton);
        saveFab.setOnClickListener(fabView -> {
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
                    showErrorSnackbar(saveFab, R.string.empty_frequency_warning);
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
                        showErrorSnackbar(saveFab, R.string.error_deadline_is_empty);
                        return;
                    }
                } else {
                    goalIntValue = goalFragment.getInt();
                }

                Habit habit = new Habit();
                // Set the habit fields
                habit.setTitle(title);
                habit.setId(HabitRealmManager.getNextId());
                habit.setHabitType(habitType);
                habit.setMaxRepetitions(maxRepetitions);
                habit.setFrequency(frequency);
                habit.setReminders(reminders);
                habit.setMaxSnoozes(snooze);
                habit.setGoalType(goalType);
                habit.setMaxAction(goalIntValue);
                habit.setMaxStreakValue(goalIntValue);
                habit.setFinalDate(goalLongValue);

                // Print test Message
                //printHabitInfoOnLog(habit);

                // Save habit
                habit = HabitRealmManager.saveOrUpdateHabit(habit);

                // Notifications' alarms setup
                setUpNotification(habit);

                Toast.makeText(this, R.string.new_habit_success_message, Toast.LENGTH_SHORT).show();
                finish();

            } else {
                titleFragment.setErrorMessage(getString(R.string.error_title_is_empty));
                showErrorSnackbar(saveFab, R.string.error_title_is_empty);
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

        presetFragment = (PresetFragment) fragmentManager.findFragmentById(R.id.presetHabitFragment);

        presetFragment.setAdapterListener(habit -> this.runOnUiThread(() -> {

            titleFragment.setTitle(habit.getTitle());

            habitTypeFragment.setHabitType(habit.getHabitType());
            habitTypeFragment.setMaxRepetitions(habit.getRepetitions());

            frequencyFragment.setFrequency(habit.getFrequency());

            remindersFragment.setReminderList(habit.getReminders());

            snoozeFragment.setSnooze(habit.getMaxSnoozes());

            GoalType goalType = habit.getGoalType();
            goalFragment.setGoalType(goalType);
            if (goalType == GoalType.ACTION)
                goalFragment.setInt(habit.getMaxAction());
            else if (goalType == GoalType.STREAK)
                goalFragment.setInt(habit.getMaxStreakValue());
            else if (goalType == GoalType.DEADLINE)
                goalFragment.setLong(habit.getFinalDate());

            Toast.makeText(getBaseContext(), R.string.preset_habit_loading_snackbar, Toast.LENGTH_SHORT).show();
        }));


        // Shrinking / extending behaviour of the fab
        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> this.runOnUiThread(() -> {
            if (scrollY > oldScrollY) {
                // Scroll down
                saveFab.shrink();
            } else {
                // Scroll up
                saveFab.extend();
            }
        }));


    }

    private void setUpNotification(@NonNull Habit habit) {

        List<Reminder> reminders = habit.getReminders();
        NotificationAlarmManager manager;

        if (reminders.size() > 0) {
            manager = new NotificationAlarmManager(this);

            manager.setNotificationData(habit.getTitle(), habit.getId());

            // Now
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            for (Reminder reminder : reminders) {
                if (reminder.isActive() && !reminder.isInThePast(hour, minutes)) {

                    int requestCode = manager.setAlarmAt(reminder.getHours(), reminder.getMinutes());

                    // TODO: controllare se salva il codice
                    // Save request code
                    reminder.setAlarmRequestCode(requestCode);
                    //Log.i("set up notification", reminder.toString());

                    // Reset request code
                    manager.setRequestCode(0);
                }
            }
        }
    }

    /**
     * @param view         The view you want to anchor the Snackbar
     * @param messageResId Resource id of the string you want to use.
     */
    private void showErrorSnackbar(View view, int messageResId) {
        Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.redColor, getTheme()))
                .setTextColor(getResources().getColor(R.color.onRedColor, getTheme()))
                .setAnchorView(view)
                .show();
    }

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
        } else {
            Log.e(logcatTag, "getSupportActionBar() returned null");
        }
    }

    /**
     * Print a test message inside logcat
     *
     * @param habit Habit to be printed
     */
    private void printHabitInfoOnLog(@NonNull Habit habit) {

        // Start Test message
        StringBuilder testData = new StringBuilder();
        testData.append("\nTitle: ").append(habit.getTitle());
        testData.append("\nHabitType: ")
                .append(habit.getHabitType()).append(", ")
                .append(habit.getRepetitions());

        testData.append("\nFrequency: ");
        for (Days d : habit.getFrequency()) {
            testData.append(d.name()).append(' ');
        }

        testData.append("\nReminders: ");
        for (Reminder r : habit.getReminders()) {
            testData.append(r.toString()).append("\t");
        }

        boolean isSnoozeEnabled = habit.getMaxSnoozes() > 0;
        testData.append("\nSnooze: ").append(isSnoozeEnabled).append(", ")
                .append(habit.getMaxSnoozes());

        GoalType goalType = habit.getGoalType();
        testData.append("\nGoal type: ").append(goalType.name()).append(' ');
        if (goalType == GoalType.DEADLINE)
            testData.append(habit.getFinalDate());
        else {
            int intValue = (habit.getMaxAction() > 0) ? habit.getMaxAction() : habit.getMaxStreakValue();
            testData.append(intValue);
        }

        Log.i(logcatTag, testData.toString());
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

}