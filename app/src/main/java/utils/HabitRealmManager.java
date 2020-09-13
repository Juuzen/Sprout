package utils;

import android.util.Log;
import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;
import model.Task;

public class HabitRealmManager {

    private static final String LOG_TAG = "HabitManager";

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
                        //FIXME: rimuovere tutti gli eventuali RealmObject all'interno dell'habit
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
                    realmInstance -> realmInstance.copyToRealmOrUpdate(habit),
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

    public static void storeHabitListInfo() {
        /*
         * Probabilmente qui per velocizzare le info potremmo controllare solo
         * gli habit mostrati nell'adapter, e non lavorare su tutta la lista
         *
         * Per il momento teniamocelo così
         *
         * Dopo questa funzione, dovrebbe essere richiamato l'update dell'adapter
         * per mostrare gli habit del giorno
         */

        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Habit> habitList = realm.where(Habit.class).findAll();
            for (Habit habit: habitList) {
                /*
                 * 1. Creare un Task con i risultati del giorno (ricorda di inserirlo nel Realm)
                 * 2. Aggiornare eventuali campi dell'habit
                 * 3. Aggiornare lo status dell'albero
                 */

                /* Parte 1 */
                /* Mi creo il task e setto i suoi campi */
                Task task = new Task();
                task.setId(TaskManager.getNextId());
                /* Se l'abitudine è rinviata, non ho bisogno di fare nessun controllo */
                if (habit.getIsSnoozed()) task.setTaskStatus(Task.Status.SNOOZED);
                /* Altrimenti faccio il controllo tra repetitions e maxRepetitions */
                else if (habit.getRepetitions() == habit.getMaxRepetitions()) task.setTaskStatus(Task.Status.PASSED);
                else task.setTaskStatus(Task.Status.FAILED);
                /* Non è meglio usare un campo Date? */
                task.setTaskDate(Calendar.getInstance().getTimeInMillis());
                /* Aggiungo al DB (necessario per poterlo rimuovere successivamente) */
                TaskManager.insertTask(task);
                habit.addTaskToHistory(task);

                /* Parte 2 */
                habit.setRepetitions(0); //FIXME: Transazione
                if (habit.getIsSnoozed()) {
                    int snoozesMade = habit.getSnoozesMade();
                    int maxSnoozes = habit.getMaxSnoozes();
                    if (snoozesMade < maxSnoozes) {
                        habit.setSnoozesMade(snoozesMade + 1);
                    }
                }
                /* Aggiornare i vari valori riferiti al goalType */

                /* Parte 3 */
                //TODO: Gestione dell'albero
            }
        }
    }
}
