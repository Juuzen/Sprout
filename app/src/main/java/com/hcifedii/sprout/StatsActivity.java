package com.hcifedii.sprout;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.hcifedii.sprout.adapter.HabitListAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Habit;
import utils.HabitRealmManager;


public class StatsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_PERMISSION_SHOWED = "UsageDialogShowed";

    HabitListAdapter adapter;
    MaterialTextView noItemMessage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        enableTopBackButton();

        // Info inside the card
        MaterialTextView timeTextView = findViewById(R.id.timeTextView);
        timeTextView.setText(getUsageTimeString());

        long habitCount = HabitRealmManager.getHabitCount();
        long completedHabitCount = HabitRealmManager.getCompletedHabitCount();

        MaterialTextView habitCreatedTextView = findViewById(R.id.habitCreatedTextView);
        habitCreatedTextView.setText(Long.toString(habitCount));

        MaterialTextView habitCompletedTextView = findViewById(R.id.habitCompletedTextView);
        habitCompletedTextView.setText(Long.toString(completedHabitCount));

        // Recycler View

        noItemMessage = findViewById(R.id.noItemMessage);

        RecyclerView recyclerView = findViewById(R.id.habitRecyclerView);

        List<Habit> habitList = HabitRealmManager.getAllHabits();

        if (habitList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noItemMessage.setVisibility(View.GONE);

            // Adapter setup
            adapter = new HabitListAdapter(habitList);
            adapter.setListener(habitId -> {
                Intent i = new Intent(this, HabitStatsActivity.class);
                i.putExtra(HabitStatsActivity.EXTRA_HABIT_ID, habitId);

                Bundle bundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(i, bundle);
            });

            recyclerView.setAdapter(adapter);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        } else {
            // Show a message if there are no habits
            recyclerView.setVisibility(View.GONE);
            noItemMessage.setVisibility(View.VISIBLE);
        }

    }

    /**
     * @return A string representing the time spent by the user on this application
     */
    private String getUsageTimeString() {
        SharedPreferences pref = getSharedPreferences(SettingsActivity.SHARED_PREFS_FILE, MODE_PRIVATE);

        String usageTimeString = "0";
        // Need android.permission.PACKAGE_USAGE_STATS to get usage time stats
        if (isAccessGranted()) {
            usageTimeString = getUsageTimeFromUsageStatsManager();
        } else if (!pref.getBoolean(SHARED_PREFS_PERMISSION_SHOWED, false)) {
            // First access. Then show this dialog
            showPermissionDialog();
            // Remember that this dialog was already showed once
            SharedPreferences.Editor editor = pref.edit();

            editor.putBoolean(SHARED_PREFS_PERMISSION_SHOWED, true);
            editor.apply();
        }
        return usageTimeString;
    }

    /**
     * Show a dialog requesting the usage stats permission from the user
     */
    private void showPermissionDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Sprout");
        builder.setMessage(R.string.permission_dialog_message);

        builder.setPositiveButton(R.string.positive_permission_dialog_button, (dialogInterface, i) -> {
            // These are system permissions, so i have to use an intent to open an external activity.
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            // Restart the app
            finish();
        });
        builder.setNeutralButton(R.string.neutral_permission_dialog_button, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    /**
     * Check if the usage stats permission is granted.
     *
     * @return true if the permission is granted. False otherwise.
     */
    private boolean isAccessGranted() {

        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            return (mode == AppOpsManager.MODE_ALLOWED);
        }
    }

    /**
     * Return the usage time in HH:MM format
     *
     * @return A string with usage time for this app
     */
    private String getUsageTimeFromUsageStatsManager() {
        UsageStatsManager manager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.YEAR, 2019);

        // This method return a map with ALL the packages on the device
        Map<String, UsageStats> usageStatsMap = manager.queryAndAggregateUsageStats(beginTime.getTimeInMillis(), System.currentTimeMillis());

        // Select this package: com.hcifedii.sprout
        UsageStats usageStats = usageStatsMap.get(getPackageName());

        long timeInForeground = 0;
        if (usageStats != null) {
            timeInForeground = usageStats.getTotalTimeInForeground();
        }

        // Minutes: 1000 * 60
        long minute = (timeInForeground / 60000) % 60;
        // Hours 1000 * 60 * 60
        long hour = (timeInForeground / 3600000) % 24;

        return String.format(Locale.getDefault(), "%2d:%02d", hour, minute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar_stats, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchMenuItem).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.searchMenuItem) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }
}