package hft.wiinf.de.horario.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SQLiteDatabase db = context.openOrCreateDatabase("horario.db",context.MODE_PRIVATE,null);
            if (PersonController.getPersonWhoIam() != null) {
                Person notificationPerson = PersonController.getPersonWhoIam();
                Log.d("dede", "DeviceREboot: Event" + notificationPerson.getName());
                if (notificationPerson.isAllowNotifications()) {
                    startNotification(context, intent, notificationPerson);
                }
            }
        }
    }

    public void startNotification(Context context, Intent intent, Person notificationPerson) {
        List<Event> allEvents = EventController.findMyAcceptedEvents();
        Log.d("dede", "DeviceREboot: Event" + notificationPerson.getName());
        for (Event event : allEvents) {
            Log.d("dede", "DeviceREboot: Event");
            Log.d("dede", "DeviceREboot: " + event.getDescription());
            Intent alarmIntent = new Intent(context, context.NOTIFICATION_SERVICE.getClass());
            alarmIntent.putExtra("Event", event.getDescription());
            alarmIntent.putExtra("Hour", event.getStartTime().getHours());
            alarmIntent.putExtra("Minute", event.getStartTime().getMinutes());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(event.getStartTime().getTime(), notificationPerson), pendingIntent);
        }
    }

    public long calcNotificationTime(long eventTimeInMillis, Person person) {
        return eventTimeInMillis - (person.getNotificationTime() * 60000);
    }
}
