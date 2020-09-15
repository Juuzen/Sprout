package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class DBAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive: Sono qui 1");
        if (intent != null) {
            Log.d("AlarmReceiver", "onReceive: Sono qui 2");
            /*
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<Habit> list = realm.where(Habit.class).findAll();
                for (Habit habit : list) {
                    Log.d("AlarmManager", "onReceive, habit: " + habit.getTitle());
                }
            }


             */
            int secretCode = intent.getIntExtra("Test", 0);
            Log.d("AlarmReceiver", "onReceive: " + secretCode);
            Toast.makeText(context, "Secret code: " + secretCode, Toast.LENGTH_SHORT).show();
        }
    }
}
