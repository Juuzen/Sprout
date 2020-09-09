package utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class HabitRealmManager {

    private static final String LOG_TAG = "DBManager";

    public static long getHabitCount() {
        Realm realm = null;
        long count = 0;

        try {
            realm = Realm.getDefaultInstance();
            count = realm.where(Habit.class).count();

        } finally {
            if (realm != null)
                realm.close();
        }
        return count;
    }

    public static Habit copyHabit(int habitId) {
        if (habitId < 0) {
            return null;
        }
        Realm realm = null;
        Habit habitCopy = null;
        try {
            realm = Realm.getDefaultInstance();

            Habit check = realm.where(Habit.class).equalTo("id", habitId).findFirst();
            if (check != null) {
                habitCopy = realm.copyFromRealm(check);
            }

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return habitCopy;
    }

    public static Habit getHabit(int id) {
        if (id < 0)
            return null;
        Realm realm = null;
        Habit habit;
        try {
            realm = Realm.getDefaultInstance();
            habit = realm.where(Habit.class).equalTo("id", id).findFirst();
            if (habit != null) {
                // This line is necessary because, by default, if something changes inside the database
                // the object will automatically know. This thing will cause some crash / bad behaviour
                // when the activity / fragment saves its state.
                return realm.copyFromRealm(habit);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
        return habit;
    }

    public static void deleteHabit(int id) {
        if (id >= 0) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(realmInstance -> {
                    Habit habit = realmInstance.where(Habit.class).equalTo("id", id).findFirst();
                    if (habit != null) {
                        habit.deleteFromRealm();
                        Log.i(LOG_TAG, "Habit deleted: " + id);
                    }
                });
            } finally {
                if (realm != null)
                    realm.close();
            }
        }
    }

    public static List<Habit> getAllHabits() {
        Realm realm = null;
        List<Habit> habitList;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Habit> realmResults = realm.where(Habit.class).findAll();
            habitList = realm.copyFromRealm(realmResults);
        } finally {
            if (realm != null)
                realm.close();
        }
        return habitList;
    }

    public static void saveOrUpdateHabit(@NonNull Habit habit) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(
                    realmInstance -> {
                        realmInstance.insertOrUpdate(habit);
                        },
                    () -> Log.i(LOG_TAG, "Transaction success! - ID: " + habit.getId()),
                    error -> Log.i(LOG_TAG, "Transaction error! - ID: " + habit.getId() + "\n" + error.getMessage()));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private static int getNextId(Realm realm) {
        Log.d("getNextId", "1");
        Number newId = realm.where(Habit.class).max("id");
        if (newId != null) {
            Log.d("getNextId", "ID MAX: " + newId.toString());
            return newId.intValue() + 1;
        }
        Log.d("getNextId", "2");
        return 0;
    }
}
