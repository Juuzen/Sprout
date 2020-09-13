package com.hcifedii.sprout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.hcifedii.sprout.adapter.HabitCardAdapter;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;
import utils.SproutBottomAppBarCutCornersTopEdge;
import utils.SproutNotification;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    HabitCardAdapter adapter;

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
                Bundle bundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();
                startActivity(i, bundle);
                return true;
            }
            return false;
        });

        // FAB button setup
        FloatingActionButton fab = findViewById(R.id.fabAddButton);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), CreateHabitActivity.class);
            Bundle bundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in,
                    android.R.anim.fade_out).toBundle();
            startActivity(intent, bundle);
        });

        RecyclerView rv = findViewById(R.id.habitCardRecyclerView);
        TextView emptyMessage = findViewById(R.id.mainEmptyHabitListMessage);
        realm = Realm.getDefaultInstance();
        RealmResults<Habit> results = realm.where(Habit.class).sort("id").findAll();

        results.addChangeListener(habits -> {
            if (habits.size() > 0) {
                rv.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
            } else {
                emptyMessage.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            }
        });

        //this is necessarily because it is not changed yet
        if (results.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);

        } else {
            emptyMessage.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        adapter = new HabitCardAdapter(results, this, realm);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchMenuItem).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.testNotification:

                new Handler().postDelayed(() -> {
                    SproutNotification notification = SproutNotification.getInstance(this);
                    notification.setTitle("ciao");
                    notification.setContent("boh");
                    notification.showNotification(12);

                }, 2000);


                return true;

            case R.id.searchMenuItem:
                return true;

            case R.id.settingMenuItem:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in,
                        android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
                return true;

            case R.id.aboutMenuItem:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle(getString(R.string.about_us_title));
                builder.setMessage(getString(R.string.about_us_message));
                builder.setIcon(R.drawable.ic_sprout_fg_small);

                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());

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
        realm.removeAllChangeListeners();
        realm.close();
    }
}