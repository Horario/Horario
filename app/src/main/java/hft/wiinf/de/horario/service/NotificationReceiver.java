package hft.wiinf.de.horario.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        addNotification(context, intent);
    }

    private void addNotification(Context context, Intent intent) {
        String msg = "Erinnerung an \"" + intent.getStringExtra("Event") + "\" um " + intent.getIntExtra("Hour", 0) + ":" + intent.getStringExtra("Minute") + " Uhr";

        Intent notificationIntent = new Intent(context, TabActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, intent.getIntExtra("ID", 0), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 26) {
            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = String.valueOf(intent.getIntExtra("ID", 0));
            //The user-visible name of the channel.
            CharSequence name = "Eventnotification";
            // The user-visible description of the channel.
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.YELLOW);
            mChannel.enableVibration(false);
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, id)
                            .setSmallIcon(R.drawable.ic_notification_h)
                            .setContentTitle("Terminerinnerung")
                            .setContentText(msg);
            builder.setContentIntent(contentIntent);
            manager.notify(intent.getIntExtra("ID", 0), builder.build());
        } else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.ic_notification_h)
                            .setContentTitle("Terminerinnerung")
                            .setContentText(msg);

            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(intent.getIntExtra("ID", 0), builder.build());
        }
    }
}