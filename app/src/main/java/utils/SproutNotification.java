package utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.hcifedii.sprout.R;

public class SproutNotification {

    // TODO: fare un'icona migliore
    private static final int iconResId = R.drawable.ic_sprout_fg_small;

    private Context context;

    private Notification.Builder builder;

    // Constructor for the older version of Android
    private SproutNotification(Context context) {

        this.context = context;

        builder = new Notification.Builder(context);

        builder.setSmallIcon(iconResId);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setShowWhen(true);
        builder.setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private SproutNotification(Context context, final String CHANNEL_ID) {

        this.context = context;

        builder = new Notification.Builder(context, CHANNEL_ID);

        builder.setSmallIcon(iconResId);
        builder.setShowWhen(true);
        builder.setAutoCancel(true);

        SproutNotification.createNotificationChannel(context);
    }

    public void setTitle(@NonNull String title) {
        builder.setContentTitle(title);
    }

    public void setContent(@NonNull String content) {
        builder.setContentText(content);
    }

    public void showNotification(int notificationId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }


    public static SproutNotification getInstance(@NonNull Context context) {

        final String CHANNEL_ID = "Sprout";

        // Require API > 25 (current API is 25)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return new SproutNotification(context, CHANNEL_ID);
        else
            return new SproutNotification(context);
    }


    /**
     * It should be called when the app starts. However multiple call don't cause harm.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(@NonNull Context context) {

        final String CHANNEL_ID = "Sprout";
        // Create the NotificationChannel, but only on API 26+

        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }


}
