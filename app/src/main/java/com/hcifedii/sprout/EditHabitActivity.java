package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.hcifedii.sprout.enumerations.Days;
import com.hcifedii.sprout.enumerations.GoalType;
import com.hcifedii.sprout.enumerations.HabitType;
import com.hcifedii.sprout.fragment.FrequencyFragment;
import com.hcifedii.sprout.fragment.GoalFragment;
import com.hcifedii.sprout.fragment.HabitTypeFragment;
import com.hcifedii.sprout.fragment.RemindersFragment;
import com.hcifedii.sprout.fragment.SnoozeFragment;
import com.hcifedii.sprout.fragment.TitleFragment;

import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;
import model.Habit;
import model.Reminder;
import utils.HabitRealmManager;
import utils.NotificationAlarmManager;
import utils.NotificationAlarmManager.NotificationAlarm;

public class EditHabitActivity extends SproutApplication {

    private static final String logcatTag = "Sprout - EditHabitActivity";

    private static final String IS_ALREADY_SHOWED = "alreadyShowed";
    public static final String EXTRA_HABIT_ID = "HABIT_ID";

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
            //TODO: Alert for disruptive actions (changing habit type, changing maxRepetitions etc)
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
                RealmList<Reminder> oldReminders = habit.getReminders();

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
                habit.setMaxRepetitions(maxRepetitions);
                if (habitType != habit.getHabitType()) {
                    habit.setRepetitions(0);
                } else {
                    if (maxRepetitions < habit.getRepetitions()) {
                        habit.setRepetitions(maxRepetitions);
                    }
                }
                habit.setHabitType(habitType);
                habit.setFrequencyTest(frequency);
                habit.setFrequency(frequency);
                habit.setReminders(reminders);
                habit.setMaxSnoozes(snooze);
                habit.setGoalType(goalType);
                habit.setMaxAction(goalIntValue);
                habit.setMaxStreakValue(goalIntValue);
                habit.setFinalDate(goalLongValue);

                setUpNotifications(habit, oldReminders);

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
            if (scrollY > oldScrollY)
                editFab.shrink();   // Scroll down
            else
                editFab.extend();   // Scroll up
        }));

        habitId = getHabitIdFromBundles(savedInstanceState);

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
            // Go to MainActivity with log error message
            Log.e(this.getClass().getSimpleName(), "Invalid habit id.");
            finish();
        }

    }

    /**
     * Retrieve the habit id from the intent / saved instance state
     *
     * @param savedInstanceState
     * @return Habit id
     */
    private int getHabitIdFromBundles(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return getIntent().getIntExtra("HABIT_ID", -1);
        else
            return savedInstanceState.getInt(EXTRA_HABIT_ID, -1);
    }

    private void setUpNotifications(@NonNull Habit habit, @NonNull List<Reminder> oldReminders) {

        Calendar calendar = Calendar.getInstance();

        if (!habit.getFrequency().contains(Days.today(calendar))) {
            // If today is not a marked day inside frequency, then skip the creation of the alarms
            return;
        }

        List<Reminder> reminders = habit.getReminders();

        if (reminders.size() > 0) {
            NotificationAlarmManager manager = new NotificationAlarmManager(this);
            manager.setNotificationData(habit.getTitle(), habit.getId());

            // Now
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            for (Reminder reminder : reminders) {

                boolean isActive = reminder.isActive();
                int reminderRequestCode = reminder.getAlarmRequestCode();

                // Two Reminder are equal if they have the same hour, minutes and request code.
                // If a Reminder has a request code != 0, then it has an alarm setted
                if (oldReminders.contains(reminder)) {

                    if (!isActive && reminderRequestCode != 0) {
                        // This alarm needs to be deactivated
                        NotificationAlarm.cancelAlarm(this, reminderRequestCode);
                        reminder.setAlarmRequestCode(0);

                    } else if (isActive && !reminder.isInThePast(hour, minutes)) {
                        // This alarm is active
                        manager.setRequestCode(reminderRequestCode);

                        reminderRequestCode = manager.setAlarmAt(reminder.getHours(), reminder.getMinutes());

                        reminder.setAlarmRequestCode(reminderRequestCode);
                    }

                    // Delete this reminder from the old list. This will be useful later
                    oldReminders.remove(reminder);

                } else {
                    // This Reminder is new or it needs to be updated
                    if (isActive && !reminder.isInThePast(hour, minutes)) {

                        manager.setRequestCode(reminder.getAlarmRequestCode());

                        reminderRequestCode = manager.setAlarmAt(reminder.getHours(), reminder.getMinutes());

                        reminder.setAlarmRequestCode(reminderRequestCode);
                    }
                }
                // Reset the mananager request code
                manager.setRequestCode(0);
            }
        }

        // Delete the remaining old active alarms
        if (oldReminders.size() > 0) {
            for (Reminder r : oldReminders) {
                //Log.e(this.getClass().getSimpleName(), "Old reminder list element: " + reminder.toString());
                if (r.getAlarmRequestCode() != 0)
                    NotificationAlarm.cancelAlarm(this, r.getAlarmRequestCode());
            }
            oldReminders.clear();
        }

        // Print test message
        //printHabitInfoOnLog(habit);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_ALREADY_SHOWED, true);
        outState.putInt(EXTRA_HABIT_ID, habitId);
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

                deleteNotifications(habit);

                HabitRealmManager.deleteHabit(habitId);
                Toast.makeText(getBaseContext(), R.string.delete_habit_dialog_confirmation_toast, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(this,
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                // Removes other Activities from stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent, bundle);

            });
            builder.setNegativeButton(R.string.negative_delete_habit_dialog, (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNotifications(@NonNull Habit habit) {

        Calendar calendar = Calendar.getInstance();

        if (!habit.getFrequency().contains(Days.today(calendar)))
            return;

        for (Reminder reminder : habit.getReminders()) {
            if (reminder.isActive() && reminder.getAlarmRequestCode() != 0)
                // Delete active alarms
                NotificationAlarm.cancelAlarm(this, reminder.getAlarmRequestCode());
        }
    }

}
