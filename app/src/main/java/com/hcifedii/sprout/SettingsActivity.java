package com.hcifedii.sprout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        enableTopBackButton();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference uiTheme = findPreference("theme");

            // TODO: salvare la preferenza nelle SharedPreferences
            if (uiTheme != null) {
                int uiMode = AppCompatDelegate.getDefaultNightMode();

                if (uiMode == AppCompatDelegate.MODE_NIGHT_NO)
                    uiTheme.setValue("light");
                else if (uiMode == AppCompatDelegate.MODE_NIGHT_YES)
                    uiTheme.setValue("dark");
                else
                    uiTheme.setValue("system");

                uiTheme.setOnPreferenceChangeListener((preference, newValue) -> {

                    if (newValue instanceof String) {

                        String value = (String) newValue;

                        switch (value) {
                            case "light":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                restartApp();
                                return true;
                            case "dark":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                restartApp();
                                return true;
                            case "system":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                restartApp();
                                return true;
                            default:
                                return false;
                        }

                    }

                    return true;
                });

            }


        }

        private void restartApp() {
            Intent intent = new Intent(getContext(), SettingsActivity.class);

            startActivity(intent);
            getActivity().finish();
        }
    }

    private void enableTopBackButton() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }


}