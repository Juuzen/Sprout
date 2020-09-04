package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.applandeo.materialcalendarview.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
            habit = HabitRealmManager.getHabit(habitId);

            CalendarView calendarView = findViewById(R.id.calendarView);

            // TODO: spostare il caricamento dei dati su di un thread
            List<Calendar> highlight = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.set(2020, 9,25);
            highlight.add(cal);

            calendarView.setHighlightedDays(highlight);

            setTitle(habit.getTitle());

        }


    }


    private int getHabitIdFromBundles(Bundle savedInstance) {

        int id = -1;
        if (savedInstance != null) {
            id = savedInstance.getInt(EXTRA_HABIT_ID, -1);
        } else {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                id = extra.getInt(EXTRA_HABIT_ID, -1);
            }
        }
        return id;
    }


    private List<Calendar> getPassedDays(@NonNull Habit habit){

        return null;
    }

    private List<Calendar> getSnoozedDays(@NonNull Habit habit){

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_habit_stats, menu);
        return true;
    }

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

}