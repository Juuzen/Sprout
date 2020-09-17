package com.hcifedii.sprout;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.hcifedii.sprout.enumerations.Days;
import com.hcifedii.sprout.enumerations.GoalType;

import model.Habit;
import model.Reminder;

/**
 * Root of the application.
 * Use this class if you want to add a method that has to be shared between all the activities.
 */
public abstract class SproutApplication extends AppCompatActivity {

    protected static final String LOGCAT_TAG = "Sprout_Application";

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

    protected void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

    /**
     * @param anchorView         The view you want to anchor the Snackbar
     * @param messageResId Resource id of the string you want to use.
     */
    protected void showErrorSnackbar(View anchorView, int messageResId) {
        Snackbar.make(anchorView, messageResId, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.redColor, getTheme()))
                .setTextColor(getResources().getColor(R.color.onRedColor, getTheme()))
                .setAnchorView(anchorView)
                .show();
    }

    /**
     * Print a test message inside logcat
     *
     * @param habit Habit to be printed
     */
    protected void printHabitInfoOnLog(@NonNull Habit habit) {

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

        Log.i(LOGCAT_TAG, testData.toString());
    }

}
