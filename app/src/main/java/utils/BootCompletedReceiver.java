package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hcifedii.sprout.enumerations.Days;

import java.util.Calendar;
import java.util.List;

import model.Habit;
import model.Reminder;

/**
 * You can test this class from command like with:
 *
 *      > adb shell am broadcast -a android.intent.action.BOOT_COMPLETED com.hcifedii.sprout
 *
 *  If it fails, then try first: > adb root
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private Context context;
    private final Calendar calendar = Calendar.getInstance();


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            this.context = context;

            Log.i(this.getClass().getSimpleName(), "Boot received " + intent.getAction());


            Days today = Days.today(calendar);

            // Boot detected. Recreate the alarms for today
            habitsAlarmsSetUp();

            // Do other stuff

        }
    }


    private void habitsAlarmsSetUp() {

        NotificationAlarmManager manager = new NotificationAlarmManager(context);

        int today = calendar.get(Calendar.DAY_OF_WEEK);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        //TODO: aggiungere metodo che restituisce le abitudini di oggi

        // Get today's habit list
        List<Habit> habitList = HabitRealmManager.getAllHabits();

        for (Habit habit : habitList) {

            List<Reminder> reminderList = habit.getReminders();

            if (reminderList.size() < 1)
                // No reminders for this habit
                continue;

            // Set the notification info
            manager.setNotificationData(habit.getTitle(), habit.getId());

            for (Reminder reminder : reminderList) {

                if (!reminder.isActive() || reminder.isInThePast(hour, minutes))
                    // This reminder shouldn't get an alarm
                    continue;

                int oldRequestCode = reminder.getAlarmRequestCode();

                manager.setRequestCode(reminder.getAlarmRequestCode());
                int reqCode = manager.setAlarmAt(reminder.getHours(), reminder.getMinutes());

                reminder.setAlarmRequestCode(reqCode);

                // Reset the request code
                manager.setRequestCode(0);

                if (oldRequestCode == 0) {
                    // Since it's an unmanaged RealmList, I have to save the reminder changes.
                    HabitRealmManager.saveOrUpdateHabit(habit);
                }
            }
        }
    }


}
