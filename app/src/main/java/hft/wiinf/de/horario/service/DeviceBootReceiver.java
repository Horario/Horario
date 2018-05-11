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
        //Will be executed after Device has finished reboot
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Check if there is person added with isItMe = true
            if (PersonController.getPersonWhoIam() != null) {
                Person notificationPerson = PersonController.getPersonWhoIam();
                //Only set Alarm if Person wants to receive them
                if (notificationPerson.isEnablePush()) {
                    startNotification(context, intent, notificationPerson);
                }
            }
        }
    }

    public void startNotification(Context context, Intent intent, Person notificationPerson) {
        //Get all events that are in the future to set the alarm
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        for (Event event : allEvents) {
            Intent alarmIntent = new Intent(context, NotificationReceiver.class);

            //Get startTime an convert into a Calender to use it
            Date date = event.getStartTime();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            //Put extra Data which is needed for the Notification
            alarmIntent.putExtra("Event", event.getDescription());
            alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
            if (calendar.get(Calendar.MINUTE) <= 10) {
                alarmIntent.putExtra("Minute", "0" + String.valueOf(calendar.get(Calendar.MINUTE)));
            } else {
                alarmIntent.putExtra("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
            }
            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

            //Set AlarmManager --> NotificaionReceiver will be called
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, notificationPerson), pendingIntent);
        }
    }

    public long calcNotificationTime(Calendar cal, Person person) {
        cal.add(Calendar.MINUTE, ((-1) * person.getNotificationTime()));
        return cal.getTimeInMillis();
    }
}