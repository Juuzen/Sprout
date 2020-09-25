package utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hcifedii.sprout.enumerations.GoalType;

import java.util.Calendar;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;
import model.Task;
import model.Tree;

public class DBAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Broadcastreceiver";
    //TODO: should include this value inside Habit?
    private final int snoozesDayLimit = 7;
    String debugDay = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
    String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK - 1, Calendar.LONG, Locale.US);

    public DBAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<Habit> habitList = realm
                        .where(Habit.class)
                        .equalTo("isArchived", false)
                        .and()
                        .contains("frequencyTest", debugDay, Case.INSENSITIVE)
                        .sort("id")
                        .findAll();
                for (Habit habit : habitList) {
                    realm.executeTransaction(
                            realmInstance -> {
                                boolean isTaskPassed = habit.getRepetitions() == habit.getMaxRepetitions();
                                boolean isTaskSnoozed = habit.getIsSnoozed();
                                Tree tree = habit.getTree();
                                Tree.Health health = tree.getHealth();
                                Tree.Growth growth = tree.getGrowth();
                                int experience = tree.getExperience();
                                GoalType goal = habit.getGoalType();

                                /* Task creation */
                                Task task = new Task();
                                task.setId(TaskRealmManager.getNextId());
                                if (isTaskSnoozed) task.setTaskStatus(Task.Status.SNOOZED);
                                else if (isTaskPassed) task.setTaskStatus(Task.Status.PASSED);
                                else task.setTaskStatus(Task.Status.FAILED);
                                task.setTaskDate(Calendar.getInstance().getTimeInMillis());
                                habit.addTaskToHistory(task);

                                if (!isTaskSnoozed) {
                                    /* Tree handler */
                                    switch (health) {
                                        case HEALTHY:
                                            // only in HEALTHY state, growth state can change
                                            if (isTaskPassed) {
                                                if (growth == Tree.Growth.SPROUT) {
                                                    tree.setGrowth(TreeRealmManager.getNextGrowthStep(growth));
                                                    tree.setExperience(0);
                                                } else if (growth != Tree.Growth.SPARKLING) {
                                                    if (experience >= TreeRealmManager.getRequiredExperience(growth)) {
                                                        tree.setGrowth(TreeRealmManager.getNextGrowthStep(growth));
                                                        tree.setExperience(0);
                                                    } else {
                                                        tree.setExperience(experience + 1);
                                                    }
                                                }
                                            } else {
                                                // While in SPARKLING growth state, health state cannot change
                                                // but growth state will be reverted to MATURE state
                                                if (growth == Tree.Growth.SPARKLING)
                                                    tree.setGrowth(Tree.Growth.MATURE);
                                                    // While in SPROUT growth state, health state cannot change
                                                else if (growth != Tree.Growth.SPROUT)
                                                    tree.setHealth(Tree.Health.DRYING);
                                            }
                                            break;
                                        case DRYING:
                                            if (isTaskPassed) tree.setHealth(Tree.Health.HEALTHY);
                                            else tree.setHealth(Tree.Health.WITHERED);
                                            break;
                                        case WITHERED:
                                            if (isTaskPassed) tree.setHealth(Tree.Health.DRYING);
                                            else {
                                                if ((experience - 1) >= 0) {
                                                    tree.setExperience(experience - 1);
                                                }
                                            }
                                            break;
                                        default:
                                    }

                                    /* Goal handler */
                                    switch (goal) {
                                        case ACTION:
                                            if (isTaskPassed) {
                                                habit.setGoalValue(habit.getGoalValue() + 1);
                                                if (habit.getGoalValue() >= habit.getMaxAction()) {
                                                    habit.setArchived(true);
                                                    habit.setFinalDate(Calendar.getInstance().getTimeInMillis());
                                                }
                                            }
                                            break;
                                        case STREAK:
                                            if (isTaskPassed) {
                                                habit.setGoalValue(habit.getGoalValue() + 1);
                                                if (habit.getGoalValue() >= habit.getMaxStreakValue()) {
                                                    habit.setArchived(true);
                                                    habit.setFinalDate(Calendar.getInstance().getTimeInMillis());
                                                }
                                            } else {
                                                habit.setGoalValue(0);
                                            }
                                            break;
                                        case DEADLINE:
                                            Calendar cal = Calendar.getInstance();
                                            int currentDay = cal.get(Calendar.DAY_OF_YEAR);
                                            int currentYear = cal.get(Calendar.YEAR);
                                            cal.setTimeInMillis(habit.getFinalDate());
                                            int finalDay = cal.get(Calendar.DAY_OF_YEAR);
                                            int finalYear = cal.get(Calendar.YEAR);
                                            if ((currentDay >= finalDay) && (currentYear >= finalYear)) {
                                                habit.setArchived(true);
                                            }
                                            break;
                                        default: // case NONE:
                                    }
                                }

                                /* Habit temporary info resets */
                                habit.setRepetitions(0);
                                if (habit.getSnoozesPassedDays() == snoozesDayLimit) {
                                    habit.setSnoozesPassedDays(0);
                                    habit.setSnoozesMade(0);
                                } else {
                                    habit.setSnoozesPassedDays(habit.getSnoozesPassedDays() + 1);
                                }
                                if (isTaskSnoozed) {
                                    habit.setIsSnoozed(false);
                                }
                            }
                    );
                }
            }

            //FIXME: implement observable pattern to trigger UI update in MainActivity

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Calendar cal = Calendar.getInstance();
                //TODO: Get SharedPreference to set custom day change
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
                long millis = cal.getTimeInMillis();
                Intent mIntent = new Intent(context, DBAlarmReceiver.class);
                mIntent.putExtra("repeat", false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, millis, pendingIntent);
            }
        }
    }


}
