package utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.hcifedii.sprout.enumerations.GoalType;
import java.util.Calendar;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import model.Habit;

import model.Task;
import model.Tree;
import model.Reminder;


public class HabitRealmManager {

    private static final String LOG_TAG = "HabitManager";

    public static long getHabitCount() {
        try (Realm realm = Realm.getDefaultInstance()) {
            return realm.where(Habit.class).count();
        }
    }

    public static long getCompletedHabitCount() {
        try (Realm realm = Realm.getDefaultInstance()) {
            return realm.where(Habit.class).equalTo("isCompleted", true).count();
        }
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

                    realmInstance -> {

                        Habit oldHabit = realmInstance.where(Habit.class).equalTo("id", habit.getId()).findFirst();
                        if (oldHabit != null) {
                            // Delete old reminders
                            RealmList<Reminder> reminder = oldHabit.getReminders();
                            reminder.deleteAllFromRealm();
                        }

                        realmInstance.copyToRealmOrUpdate(habit);

                    },
                    () -> Log.i(LOG_TAG, "Transaction success! - ID: " + habit.getId()),
                    error -> Log.i(LOG_TAG, "Transaction error! - ID: " + habit.getId() + "\n" + error.getMessage()));
        }
    }

    public static int getNextId() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Number maxId = realm.where(Habit.class).max("id");
            if (maxId != null)
                return maxId.intValue() + 1;
            else return 0;
        }
    }

    public static void test(Context context) {
        Log.d(LOG_TAG, "test: prova");
        Toast.makeText(context, "Test riuscito", Toast.LENGTH_SHORT).show();
    }

    public static void storeHabitListInfo() {
        /*
         * Probabilmente qui per velocizzare le info potremmo controllare solo
         * gli habit mostrati nell'adapter, e non lavorare su tutta la lista
         *
         * Per il momento teniamocelo così
         *
         * Dopo questa funzione, dovrebbe essere fatto il refresh dell'adapter
         */

        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Habit> habitList = realm.where(Habit.class).findAll();
            for (Habit habit: habitList) {
                /*
                 * 1. Creare un Task con i risultati del giorno (ricorda di inserirlo nel Realm)
                 * 2. Aggiornare eventuali campi dell'habit
                 * 3. Aggiornare lo status dell'albero
                 * 4. Controllare lo status di completamento del goal
                 */

                realm.executeTransaction(
                        realmInstance -> {
                            boolean isTaskPassed = habit.getRepetitions() == habit.getMaxRepetitions();
                            /* Parte 1 */
                            /* Mi creo il task e setto i suoi campi */
                            Task task = new Task();
                            task.setId(TaskManager.getNextId());
                            /* Se l'abitudine è rinviata, non ho bisogno di fare nessun controllo */
                            if (habit.getIsSnoozed()) task.setTaskStatus(Task.Status.SNOOZED);
                                /* Altrimenti faccio il controllo tra repetitions e maxRepetitions */
                            else if (isTaskPassed) task.setTaskStatus(Task.Status.PASSED);
                            else task.setTaskStatus(Task.Status.FAILED);
                            /* Non è meglio usare un campo Date? */
                            task.setTaskDate(Calendar.getInstance().getTimeInMillis());
                            /* Aggiungo al DB (necessario per poterlo rimuovere successivamente) */

                            //realmInstance.insertOrUpdate(task);
                            //TaskManager.insertTask(task);

                            habit.addTaskToHistory(task);
                            Log.d(LOG_TAG, "storeHabitListInfo: " + habit.getTaskHistory());

                            /* Parte 2 */
                            habit.setRepetitions(0);
                            if (habit.getIsSnoozed()) {
                                int snoozesMade = habit.getSnoozesMade();
                                int maxSnoozes = habit.getMaxSnoozes();
                                if (snoozesMade < maxSnoozes) {
                                    habit.setSnoozesMade(snoozesMade + 1);
                                }
                            }

                            /* Parte 3 */
                            //TODO: Gestione dell'albero
                            /*
                            Tree tree = habit.getTree();
                            Tree.Health health = tree.getHealth();
                            Tree.Growth growth = tree.getGrowth();
                            if (health == Tree.Health.HEALTHY) {
                                if (isTaskPassed) {
                                    TreeManager.grow(tree);
                                } else {
                                    if (growth == Tree.Growth.SPARKLING) {
                                        TreeManager.setTreeGrowth(tree, Tree.Growth.MATURE);
                                    } else {
                                        TreeManager.setTreeHealth(tree, Tree.Health.DRYING);
                                    }
                                }
                            } else if (health == Tree.Health.DRYING) {
                                if (isTaskPassed) {
                                    TreeManager.setTreeHealth(tree, Tree.Health.HEALTHY);
                                } else {
                                    TreeManager.setTreeHealth(tree, Tree.Health.WITHERED);
                                }
                            } else {
                                if (isTaskPassed) {
                                    TreeManager.setTreeHealth(tree, Tree.Health.DRYING);
                                } else {
                                    int exp = tree.getExperience() - 1;
                                    if (exp >= 0) {
                                        TreeManager.setTreeExperience(tree, tree.getExperience() - 1);
                                    }
                                }
                            }
                             */

                            /* Parte 4 */
                            GoalType goal = habit.getGoalType();
                            if (!habit.getIsSnoozed()) {
                                switch (goal) {
                                    case ACTION:
                                        if (isTaskPassed) {
                                            habit.setGoalValue(habit.getGoalValue() + 1);
                                            if (habit.getGoalValue() >= habit.getMaxAction()) {
                                                habit.setIsCompleted(true);
                                            }
                                        }
                                        break;
                                    case STREAK:
                                        if (isTaskPassed) {
                                            habit.setGoalValue(habit.getGoalValue() + 1);
                                            if (habit.getGoalValue() >= habit.getMaxStreakValue()) {
                                                habit.setIsCompleted(true);
                                            }
                                        } else {
                                            habit.setGoalValue(0);
                                        }
                                        break;
                                    case DEADLINE:
                                        Calendar cal = Calendar.getInstance();
                                        int today = cal.get(Calendar.DAY_OF_YEAR);
                                        cal.setTimeInMillis(habit.getFinalDate());
                                        int finalDate = cal.get(Calendar.DAY_OF_YEAR);
                                        //FIXME: controllo anche sull'anno
                                        if (today >= finalDate) {
                                            habit.setIsCompleted(true);
                                        }
                                        break;
                                    default: /* case NONE: */
                                }
                            }
                        });
            }
        }
    }
}
