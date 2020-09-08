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
                        habit.setId(HabitRealmManager.getNextId(realmInstance));
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

    public static void setHabitRepetition (int habitId, int newValue) {
        Realm realm = null;
        Habit habit;
        try {
            realm = Realm.getDefaultInstance();
            habit = realm.where(Habit.class).equalTo("id", habitId).findFirst();
            if (habit != null) {
                Habit finalHabit = habit;
                if (newValue <= finalHabit.getMaxRepetitions()) {
                    //FIXME: gestione degli eventuali errori della transazione
                    realm.executeTransaction(
                            realmInstance -> {
                                finalHabit.setRepetitions(newValue);
                                //TODO: status del task completato
                                realmInstance.insertOrUpdate(finalHabit);
                                Log.d("SetHabitRepetition", "Aggiornato");
                            });
                } else {
                    Log.d("SetHabitRepetition", "Repetitions già al massimo");
                }

            } else {
                Log.d("SetHabitRepetition", "Non ho trovato l'abitudine");
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
