package com.hcifedii.sprout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import utils.HabitRealmManager;


public class StatsActivity extends AppCompatActivity {

    private static final String logcatTag = "StatsActivity";

    public static final String SHARED_PREFS_PERMISSION_SHOWED = "UsageDialogShowed";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        enableTopBackButton();

        SharedPreferences pref = getSharedPreferences(SettingsActivity.SHARED_PREFS_FILE, MODE_PRIVATE);

        String usageTimeString = "0";
        // Need android.permission.PACKAGE_USAGE_STATS to get usage time stats
        if (isAccessGranted()) {
            usageTimeString = getUsageTimeString();
        } else if (!pref.getBoolean(SHARED_PREFS_PERMISSION_SHOWED, false)) {
            // First access. Then show this dialog
            showPermissionDialog();
            // Remember that this dialog was already showed once
            SharedPreferences.Editor editor = pref.edit();

            editor.putBoolean(SHARED_PREFS_PERMISSION_SHOWED, true);
            editor.apply();
        }

        MaterialTextView timeTextView = findViewById(R.id.timeTextView);
        timeTextView.setText(usageTimeString);

        long habitCount = HabitRealmManager.getHabitCount();

        MaterialTextView habitCreatedTextView = findViewById(R.id.habitCreatedTextView);
        habitCreatedTextView.setText(Long.toString(habitCount));


    }

    /**
     * Show a dialog requesting the usage stats permission from the user
     */
    private void showPermissionDialog(){
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
     * @return A string with usage time for this app
     */
    private String getUsageTimeString() {
        UsageStatsManager manager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.YEAR, 2019);

        Calendar endTime = Calendar.getInstance();

        List<UsageStats> usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginTime.getTimeInMillis(), endTime.getTimeInMillis());

        long timeInForeground = 0;
        for (UsageStats us : usageStats) {
            // Sum of each usage time, in milliseconds
            timeInForeground += us.getTotalTimeInForeground();
        }

        // Minutes: 1000 * 60
        long minute = (timeInForeground / 60000) % 60;
        // Hours 1000 * 60 * 60
        long hour = (timeInForeground / 3600000) % 24;

        return String.format(Locale.getDefault(), "%2d:%02d", hour, minute);
    }


    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        } else {
            Log.e(logcatTag, "getSupportActionBar() returned null");
        }
    }
}