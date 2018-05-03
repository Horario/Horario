package hft.wiinf.de.horario.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            startNotification(context,intent);
            //Get all events an startNotification for every single one
        }
    }

    public void startNotification(Context context, Intent intent){
        Intent alarmIntent = new Intent(context, context.NOTIFICATION_SERVICE.getClass());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pendingIntent);
    }
}
