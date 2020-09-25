package utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hcifedii.sprout.R;

import java.util.Calendar;
import java.util.Random;

public class NotificationAlarmManager {

    private static final Random random = new Random();

    private final Context context;

    private String habitTitle;
    private int habitId;
    private int requestCode;

    public NotificationAlarmManager(@NonNull Context context) {
        this.context = context;
    }

    public void setNotificationData(String habitTitle, int habitId, int requestCode) {
        this.habitTitle = habitTitle;
        this.habitId = habitId;
        this.requestCode = requestCode;
    }

    public void setNotificationData(String habitTitle, int habitId) {
        setNotificationData(habitTitle, habitId, 0);
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    /**
     * Set the hour and the minutes when the notification needs to show up. If the trigger time
     * you specify is in the past, the alarm triggers immediately.
     *
     * @param hour    Hour
     * @param minutes Minutes
     * @return The request code used to create the alarm, you can use it if you want to update or delete it.
     */
    public int setAlarmAt(int hour, int minutes) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);

            long timeInMillis = calendar.getTimeInMillis();

            if (requestCode == 0)
                requestCode = random.nextInt();

            Intent in = new Intent(context, NotificationAlarm.class);

            in.putExtra(NotificationAlarm.HABIT_TITLE, habitTitle);
            in.putExtra(NotificationAlarm.HABIT_ID, habitId);
            in.putExtra(NotificationAlarm.REQUEST_CODE, requestCode);

            //Log.i(this.getClass().getSimpleName(), calendar.getTime().toString());
            //Log.i("LOG", Integer.toString(requestCode));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, in, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent);

            return requestCode;

        } else {
            Log.e(this.getClass().getSimpleName(), "alarmManager is null. The notification won't be sent.");
        }

        return 0;
    }

    public static class NotificationAlarm extends BroadcastReceiver {

        final static String HABIT_ID = "habitId";
        final static String HABIT_TITLE = "habitTitle";
        final static String REQUEST_CODE = "requestCode";

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {

                int habitId = intent.getIntExtra(HABIT_ID, 0);
                String habitTitle = intent.getStringExtra(HABIT_TITLE);
                int requestCode = intent.getIntExtra(REQUEST_CODE, 0);

                //Log.i(this.getClass().getSimpleName(), "Alarm received: " + Integer.toString(requestCode));
                // Alarm received. Make a new notification and shows it
                SproutNotification notification = SproutNotification.getInstance(context);

                if (habitTitle != null)
                    notification.setTitle(habitTitle);
                else
                    notification.setTitle(context.getString(R.string.app_name));

                notification.showNotification(habitId);

                cancelAlarm(context, requestCode);
            }

        }

        public static void cancelAlarm(@NonNull Context context, int requestCode) {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotificationAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pendingIntent);
        }
    }


}
