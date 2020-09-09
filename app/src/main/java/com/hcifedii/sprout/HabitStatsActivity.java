package com.hcifedii.sprout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.hcifedii.sprout.enumerations.GoalType;
import com.hcifedii.sprout.enumerations.HabitType;

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
            // Using a different thread for ui operations
            this.runOnUiThread(() -> {

                // Get habit from database
                habit = HabitRealmManager.getHabit(habitId);

                if(habit == null)
                    return;         // TODO: forse ci andrebbe meglio un'eccezione

                // Set Activity's title
                setTitle(habit.getTitle());

                // Get the views
                CalendarView calendarView = findViewById(R.id.calendarView);

                // List of icons that will be showed inside the calendar
                List<EventDay> events = new ArrayList<>();

                // List of the days that should be highlighted
                List<Calendar> highlight = new ArrayList<>();

                // Mark creation date inside the calendar
                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(habit.getHabitCreationDate());
                calendarView.setMinimumDate(startDate);

                Drawable startIcon = getIcon(R.drawable.ic_add_24, R.color.primaryColor);
                events.add(new EventDay(startDate, startIcon));

                // Mark the end date if available
                if(habit.getGoalType() == GoalType.DEADLINE){
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTimeInMillis(habit.getFinalDate());
                    calendarView.setMaximumDate(endDate);

                    Drawable endIcon = getIcon(R.drawable.ic_archive_24, R.color.primaryColor);
                    events.add(new EventDay(endDate, endIcon));
                }

                // Get the task
                ///
//                Calendar cal = Calendar.getInstance();
//                cal.set(2020, 9,25);
//                highlight.add(cal);










                calendarView.setEvents(events);
                calendarView.setHighlightedDays(highlight);





            });



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

    private Drawable getIcon(int drawableResId, int colorResId){

        Drawable icon = ContextCompat.getDrawable(this, drawableResId);
        if(icon != null)
            icon.setTint(getResources().getColor(colorResId, getTheme()));

        return icon;
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