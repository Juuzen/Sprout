package utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.MainActivity;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.adapter.HabitCardAdapter;
import com.hcifedii.sprout.enumerations.GoalType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;
import model.Task;
import model.Tree;

public class DBAlarmReceiver extends BroadcastReceiver  {
    private static final String TAG = "Broadcastreceiver";
    public DBAlarmReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            /*
             * v. Richiamare storeHabitListInfo() sulla lista di habit giornalieri
             * X. OBSERVER
             * v. Ri-settare l'alarm manager per il giorno successivo
             */

            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<Habit> habitList = realm.where(Habit.class).findAll();
                for (Habit habit: habitList) {
                    realm.executeTransaction(
                            realmInstance -> {
                                boolean isTaskPassed = habit.getRepetitions() == habit.getMaxRepetitions();
                                Task task = new Task();
                                task.setId(TaskManager.getNextId());
                                if (habit.getIsSnoozed()) task.setTaskStatus(Task.Status.SNOOZED);
                                else if (isTaskPassed) task.setTaskStatus(Task.Status.PASSED);
                                else task.setTaskStatus(Task.Status.FAILED);
                                task.setTaskDate(Calendar.getInstance().getTimeInMillis());
                                habit.addTaskToHistory(task);

                                // TODO: fare il check mensile per resettare snoozesMade
                                habit.setRepetitions(0);
                                if (habit.getIsSnoozed()) {
                                    int snoozesMade = habit.getSnoozesMade();
                                    int maxSnoozes = habit.getMaxSnoozes();
                                    if (snoozesMade < maxSnoozes) {
                                        habit.setSnoozesMade(snoozesMade + 1);
                                    }
                                }

                                Tree tree = habit.getTree();
                                Tree.Health health = tree.getHealth();
                                Tree.Growth growth = tree.getGrowth();
                                int experience = tree.getExperience();
                                switch (health) {
                                    case HEALTHY:
                                        if (isTaskPassed) {
                                            switch (growth) {
                                                case SPROUT:
                                                    if (experience == 0) {
                                                        tree.setGrowth(Tree.Growth.SMALL);
                                                        tree.setExperience(0);
                                                    }
                                                    break;
                                                case SMALL:
                                                    if(experience >= 3) {
                                                        tree.setGrowth(Tree.Growth.MEDIUM);
                                                        tree.setExperience(0);
                                                    } else {
                                                        tree.setExperience(experience + 1);
                                                    }
                                                    break;
                                                case MEDIUM:
                                                    if (experience >= 5) {
                                                        tree.setGrowth(Tree.Growth.MATURE);
                                                        tree.setExperience(0);
                                                    } else {
                                                        tree.setExperience(experience + 1);
                                                    }
                                                    break;
                                                case MATURE:
                                                    if (experience >= 2) {
                                                        tree.setGrowth(Tree.Growth.SPARKLING);
                                                        tree.setExperience(0);
                                                    } else {
                                                        tree.setExperience(experience + 1);
                                                    }
                                                    break;
                                                case SPARKLING:
                                                    break;
                                                default:
                                            }
                                        } else {
                                            if (growth == Tree.Growth.SPARKLING) tree.setGrowth(Tree.Growth.MATURE);
                                            else if (growth != Tree.Growth.SPROUT) tree.setHealth(Tree.Health.DRYING);
                                        }
                                        break;
                                    case DRYING:
                                        if (isTaskPassed) tree.setHealth(Tree.Health.HEALTHY);
                                        else tree.setHealth(Tree.Health.WITHERED);
                                        break;
                                    case WITHERED:
                                        if (isTaskPassed) tree.setHealth(Tree.Health.HEALTHY);
                                        else {
                                            if ((experience - 1) >= 0) {
                                                tree.setExperience(experience - 1);
                                            }
                                        }
                                        break;
                                    default:
                                }

                                GoalType goal = habit.getGoalType();
                                if (!habit.getIsSnoozed()) {
                                    switch (goal) {
                                        case ACTION:
                                            if (isTaskPassed) {
                                                habit.setGoalValue(habit.getGoalValue() + 1);
                                                if (habit.getGoalValue() >= habit.getMaxAction()) {
                                                    habit.setCompleted(true);
                                                }
                                            }
                                            break;
                                        case STREAK:
                                            if (isTaskPassed) {
                                                habit.setGoalValue(habit.getGoalValue() + 1);
                                                if (habit.getGoalValue() >= habit.getMaxStreakValue()) {
                                                    habit.setCompleted(true);
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
                                                habit.setCompleted(true);
                                            }
                                            break;
                                        default: // case NONE:
                                    }
                                }
                            });
                }
            }
//            AdapterObservable mObs = AdapterObservable.getInstance();
////            Log.d(TAG, "" + mObs.countObservers());


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
                    long millis = cal.getTimeInMillis();
                    Intent mIntent = new Intent (context, DBAlarmReceiver.class);
                    mIntent.putExtra("repeat", false);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, millis, pendingIntent);
                }


        }
    }


}
