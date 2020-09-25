package utils;

import android.util.Log;

import androidx.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmList;
import model.Habit;
import model.Task;

public class TaskRealmManager {
    private static final String LOGCAT_TAG = "TaskManager";

    public static void insertTask(@NonNull Task task) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(
                    realmInstance -> realmInstance.insertOrUpdate(task)
            );
        }
    }

    public static void deleteTask(int taskId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(
                    realmInstance -> {
                        Task task = realmInstance.where(Task.class).equalTo("id", taskId).findFirst();
                        if (task != null) task.deleteFromRealm();
                    }
            );
        }
    }


    public static void deleteHabitTaskHistory(int habitId) {
        try (Realm realm = Realm.getDefaultInstance()) {

            realm.executeTransaction(realmInstance -> {
                Habit habit = realmInstance.where(Habit.class).equalTo("id", habitId).findFirst();
                if (habit != null) {
                    RealmList<Task> tasks = habit.getTaskHistory();
                    if (tasks.size() > 0) {
                        if (!tasks.deleteAllFromRealm())
                            Log.e(LOGCAT_TAG, "Delete task from Realm returned false");

                    }
                }
            });
        }
    }

    public static int getNextId() {
        int result;
        try (Realm realm = Realm.getDefaultInstance()) {
            Number maxId = realm.where(Task.class).max("id");
            if (maxId != null) result = maxId.intValue() + 1;
            else result = 0;
        }
        return result;
    }
}
