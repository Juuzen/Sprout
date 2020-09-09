package com.hcifedii.sprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.hcifedii.sprout.adapter.HabitCardAdapter;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import model.Habit;
import utils.HabitRealmManager;
import utils.SproutBottomAppBarCutCornersTopEdge;

public class MainActivity extends AppCompatActivity {

    private static final String logcatTag = "Sprout - MainActivity";
    private int lastPosition;
    private RecyclerView rv;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUIMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the title inside the top bar for this activity.
        // I'm not doing it inside the Manifest because it changes the app's name
        setTitle(R.string.MainActivityTitle);

        // Bottom App Bar setup
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        cutBottomAppEdge(bottomAppBar);     // Diamond shape

        // Add listener to Stats button inside the bottom app bar
        MenuItem statsMenuItem = bottomAppBar.getMenu().findItem(R.id.statsMenuItem);
        statsMenuItem.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.statsMenuItem){
                Intent i = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(i);
                return true;
            }
            return false;
        });

        // FAB button setup
        FloatingActionButton fab = findViewById(R.id.fabAddButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), CreateHabitActivity.class);
            startActivity(intent);
        });

        realm = Realm.getDefaultInstance();
        RealmResults<Habit> results = realm.where(Habit.class).sort("id").findAll();
        rv = findViewById(R.id.habitCardRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        HabitCardAdapter adapter = new HabitCardAdapter(results, true, this);
        rv.setAdapter(adapter);
        //TODO: quando il converter è vuoto, mostra una textview invece della recyclerview
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
            case R.id.searchMenuItem:


                return true;
            case R.id.settingMenuItem:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.aboutMenuItem:

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

                builder.setTitle(getString(R.string.about_us_title));
                builder.setMessage(getString(R.string.about_us_message));
                builder.setIcon(R.drawable.ic_sprout_fg_small);

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                builder.show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}