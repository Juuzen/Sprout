package utils;

import android.util.Log;
import androidx.annotation.NonNull;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class HabitRealmManager {

    private static final String LOG_TAG = "DBManager";

    public static long getHabitCount() {
        Realm realm = null;
        long count;
        try {
            realm = Realm.getDefaultInstance();
            count = realm.where(Habit.class).count();

        } finally {
            if (realm != null)
                realm.close();
        }
        return count;
    }

    public static Habit getHabit(int habitId) {
        Habit result = null;
        if (habitId >= 0) {
            try (Realm realm = Realm.getDefaultInstance()) {
                Habit check = realm.where(Habit.class).equalTo("id", habitId).findFirst();
                if (check != null) {
                    result = realm.copyFromRealm(check);
                }
            }
        }
        return result;
    }

    public static void deleteHabit(int id) {
        if (id >= 0) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(realmInstance -> {
                    Habit habit = realmInstance.where(Habit.class).equalTo("id", id).findFirst();
                    if (habit != null) {
                        habit.deleteFromRealm();
                    }
                });
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
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(
                    realmInstance -> realmInstance.insertOrUpdate(habit),
                    () -> Log.i(LOG_TAG, "Transaction success! - ID: " + habit.getId()),
                    error -> Log.i(LOG_TAG, "Transaction error! - ID: " + habit.getId() + "\n" + error.getMessage()));
        }
    }

    public static int getNextId () {
        Realm realm = null;
        int result;
        try {
            realm = Realm.getDefaultInstance();
            Number maxId = realm.where(Habit.class).max("id");
            if (maxId != null) {
                result = maxId.intValue() + 1;
            } else {
                result = 0;
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return result;
    }
}
