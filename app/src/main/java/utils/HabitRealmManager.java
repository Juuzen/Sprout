package utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class HabitRealmManager {

    private static final String LOG_TAG = "DBManager";

    public static Habit getHabit(int id) {
        if (id < 0)
            return null;
        Realm realm = null;
        Habit habit;
        try {
            realm = Realm.getDefaultInstance();
            habit = realm.where(Habit.class).equalTo("id", id).findFirst();
        } finally {
            if (realm != null)
                realm.close();
        }
        return habit;
    }

    public static boolean deleteHabit(int id) {
        if (id < 0)
            return false;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmInstance -> {
                Habit habit = realmInstance.where(Habit.class).equalTo("id", id).findFirst();
                if (habit != null) {
                    habit.deleteFromRealm();
                    Log.i(LOG_TAG, "Habit deleted: " + id); //FIXME: rimuovere in produzione
                }
            });
        } finally {
            if (realm != null)
                realm.close();
        }
        return true; //FIXME: dovrebbe tornare l'esito della cancellazione
    }

    public static ArrayList<Habit> getAllHabits() {
        Realm realm = null;
        ArrayList<Habit> habitList;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Habit> results = realm.where(Habit.class).findAll();
            habitList = new ArrayList<>(results);
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
                    realmInstance -> realmInstance.insertOrUpdate(habit),
                    () -> Log.i(LOG_TAG, "Transaction success! - ID: " + habit.getId()),
                    error -> Log.i(LOG_TAG, "Transaction error! - ID: " + habit.getId() + "\n" + error.getMessage()
                    ));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * @param realm
     * @return The next available id. it's 0 if there are no habits.
     */
    private static int getNextId(Realm realm) {
        Number newId = realm.where(Habit.class).max("id");
        if (newId != null)
            return newId.intValue() + 1;
        return 0;
    }
}
