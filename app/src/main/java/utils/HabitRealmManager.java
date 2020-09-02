package utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class HabitRealmManager {

    private static final String LOG_TAG = "DBManager";

    /**
     * @param id
     * @return Habit with the selected id or null.
     */
    public Habit getHabit(int id) {
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

    /**
     * @param id Id of the habit you want to delete.
     * @return
     */
    public boolean deleteHabit(int id) {

        if (id < 0)
            return false;

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
        return true; //FIXME: dovrebbe tornare l'esito della cancellazione
    }

    /**
     * @return A list of Habits or an empty list if there are no habits.
     */
    public List<Habit> getAllHabits() {

        Realm realm = null;
        List<Habit> habitsList;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Habit> realmResults = realm.where(Habit.class).findAll();
            habitsList = realm.copyFromRealm(realmResults);
        } finally {
            if (realm != null)
                realm.close();
        }
        return habitsList;
    }

    public void saveOrUpdateHabit(@NonNull Habit habit) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(realmInstance -> {
                if (habit.getId() < 0) {
                    // Have to save a new Habit
                    habit.setId(getNextId(realmInstance));
                }//else{
                // Update an existent Habit

                //}

                realmInstance.insertOrUpdate(habit);

            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.i(LOG_TAG, "Transaction success! - ID: " + habit.getId());
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(@NonNull Throwable error) {
                    Log.i(LOG_TAG, "Transaction error! - ID: " + habit.getId() + "\n" + error.getMessage());
                }
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * @param realm
     * @return The next available id. it's 0 if there are no habits.
     */
    private int getNextId(Realm realm) {
        Number newId = realm.where(Habit.class).max("id");
        if (newId != null)
            return newId.intValue() + 1;
        return 0;
    }
}
