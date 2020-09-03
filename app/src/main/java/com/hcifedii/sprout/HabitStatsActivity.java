package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.CalendarView;

import model.Habit;
import utils.HabitRealmManager;

public class HabitStatsActivity extends AppCompatActivity {

    public static final String EXTRA_HABIT_ID = "habitID";

    private Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_stats);

        enableTopBackButton();

        int habitId = getHabitIdFromBundles(savedInstanceState);

        if (habitId >= 0) {
            HabitRealmManager manager = new HabitRealmManager();
            habit = manager.getHabit(habitId);

            CalendarView calendarView = findViewById(R.id.calendarView);

            setTitle(habit.getTitle());

        }


    }


    private int getHabitIdFromBundles(Bundle savedInstance) {

        if (savedInstance != null) {
            return savedInstance.getInt(EXTRA_HABIT_ID, -1);
        } else {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                return extra.getInt(EXTRA_HABIT_ID, -1);
            }
        }
        return -1;
    }

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }


    public class MyCalendar extends CalendarView {

        public MyCalendar(@NonNull Context context) {
            super(context);
        }


    }


}