package hft.wiinf.de.horario.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (PersonController.getPersonWhoIam() != null) {
                Person notificationPerson = PersonController.getPersonWhoIam();
                if (notificationPerson.isAllowNotifications()) {
                    startNotification(context, intent, notificationPerson);
                }
            }
        }
    }

    public void startNotification(Context context, Intent intent, Person notificationPerson) {
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);

        for (Event event : allEvents) {
            Date date = event.getStartTime();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            alarmIntent.putExtra("Event", event.getDescription());
            alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
            alarmIntent.putExtra("Minute", calendar.get(Calendar.MINUTE));
            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, notificationPerson), pendingIntent);
        }
    }

    public long calcNotificationTime(Calendar cal, Person person) {
        cal.add(Calendar.MINUTE, ((-1) * person.getNotificationTime()));
        return cal.getTimeInMillis();
    }
}
