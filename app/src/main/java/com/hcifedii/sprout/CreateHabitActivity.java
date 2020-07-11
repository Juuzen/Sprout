package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class CreateHabitActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - CreateHabitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        setTopBarBackButton(true);
    }


    private void setTopBarBackButton(boolean flag){

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(flag);
        }else{
            Log.e(logcatTag, "getSupportActionBar() returned null");
        }
    }
}