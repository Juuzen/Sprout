package com.hcifedii.sprout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HabitStatsActivity extends AppCompatActivity {

    public static final String EXTRA_HABIT_ID = "habitID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_stats);
    }
}