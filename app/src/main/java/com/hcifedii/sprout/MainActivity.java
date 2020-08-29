package com.hcifedii.sprout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;

import utils.SproutBottomAppBarCutCornersTopEdge;

public class MainActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setUIMode();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set the title inside the top bar for this activity.
        // I'm not doing it inside the Manifest because it changes the app's name
        setTitle(R.string.MainActivityTitle);

        FloatingActionButton fab = findViewById(R.id.fabAddButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), CreateHabitActivity.class);

            startActivity(intent);
        });


        BottomAppBar bar = findViewById(R.id.bottomAppBar);
        cutBottomAppEdge(bar);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settingMenuItem:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.aboutMenuItem:
                Toast.makeText(this, "Sono il secondo", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Set the Night/Light UI. On the first run of the app, the user get the Light UI.
     */
    private void setUIMode() {

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.SHARED_PREFS_FILE, MODE_PRIVATE);

        int pref = preferences.getInt(SettingsActivity.SHARED_PREFS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_NO);

        AppCompatDelegate.setDefaultNightMode(pref);
    }

    private void cutBottomAppEdge(BottomAppBar bar) {
        BottomAppBarTopEdgeTreatment topEdge = new SproutBottomAppBarCutCornersTopEdge(
                bar.getFabCradleMargin(),
                bar.getFabCradleRoundedCornerRadius(),
                bar.getCradleVerticalOffset());
        MaterialShapeDrawable babBackground = (MaterialShapeDrawable) bar.getBackground();
        //It requires 1.1.0-alpha10
        babBackground.setShapeAppearanceModel(
                babBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopEdge(topEdge)
                        .build());
    }


}