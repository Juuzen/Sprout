package com.hcifedii.sprout;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_FILE = "sharedPrefs";
    public static final String SHARED_PREFS_DARK_MODE = "DarkMode";

    private static final String SETTINGS_FRAGMENT_KEY = "SettingsFragment";

    private SettingsFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        enableTopBackButton();

        FragmentManager manager = getSupportFragmentManager();

        // Get the fragment or make a new one
        if (savedInstanceState != null) {
            fragment = (SettingsFragment) manager.getFragment(savedInstanceState, SETTINGS_FRAGMENT_KEY);
        } else {
            fragment = new SettingsFragment();
        }

        // Add the fragment to the FrameLayout
        manager.beginTransaction().replace(R.id.settings, fragment).commit();


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, SETTINGS_FRAGMENT_KEY, fragment);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        public static final String DARK_UI = "dark";
        public static final String LIGHT_UI = "light";
        public static final String SYSTEM = "system";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference themeSelector = findPreference("theme");

            SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);

            int pref = preferences.getInt(SHARED_PREFS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_NO);

            if (themeSelector != null) {

                if (pref == AppCompatDelegate.MODE_NIGHT_NO)
                    themeSelector.setValue(LIGHT_UI);
                else if (pref == AppCompatDelegate.MODE_NIGHT_YES)
                    themeSelector.setValue(DARK_UI);
                else
                    themeSelector.setValue(SYSTEM);

                themeSelector.setOnPreferenceChangeListener((p, selectedValue) -> {
                    // Save the new value inside the SharedPreferences
                    if (selectedValue instanceof String) {

                        String value = (String) selectedValue;
                        SharedPreferences.Editor editor = preferences.edit();

                        switch (value) {
                            default:    // Light UI as default theme
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                editor.putInt(SHARED_PREFS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case DARK_UI:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                editor.putInt(SHARED_PREFS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                            case SYSTEM:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                editor.putInt(SHARED_PREFS_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                        }
                        editor.apply();
                    }
                    return true;
                });

            }


        }

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