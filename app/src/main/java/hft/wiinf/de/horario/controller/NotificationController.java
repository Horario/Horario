package hft.wiinf.de.horario.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.service.NotificationReceiver;

public class NotificationController {

    //Method is going to set the alarm x minutes before the event
    public static void setAlarmForNotification(Context context, Event event) {
        if (PersonController.getPersonWhoIam() != null) {
            Person notificationPerson = PersonController.getPersonWhoIam();
            if (notificationPerson.isEnablePush()) {
                Calendar testToday = GregorianCalendar.getInstance();
                testToday.setTimeInMillis(System.currentTimeMillis());

                Intent alarmIntent;
                Date date;
                Calendar calendar;
                PendingIntent pendingIntent;
                AlarmManager manager;

                if (event.getStartTime().after(testToday.getTime())) {
                    alarmIntent = new Intent(context, NotificationReceiver.class);
                    date = event.getStartTime();
                    calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);

                    alarmIntent.putExtra("Event", event.getShortTitle());
                    alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
                    if (calendar.get(Calendar.MINUTE) < 10) {
                        alarmIntent.putExtra("Minute", "0" + String.valueOf(calendar.get(Calendar.MINUTE)));
                    } else {
                        alarmIntent.putExtra("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
                    }
                    alarmIntent.putExtra("ID", event.getId().intValue());
                    pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

                    manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, notificationPerson), pendingIntent);
                }
                if (!event.getRepetition().equals(Repetition.NONE)) {
                    List<Event> allEvents = EventController.findRepeatingEvents(event.getId());
                    for (Event repEvent : allEvents) {
                        if (repEvent.getStartTime().after(testToday.getTime())) {
                            alarmIntent = new Intent(context, NotificationReceiver.class);
                            //Get startTime an convert into a Calender to use it
                            date = repEvent.getStartTime();
                            calendar = GregorianCalendar.getInstance();
                            calendar.setTime(date);

                            //Put extra Data which is needed for the Notification
                            alarmIntent.putExtra("Event", repEvent.getShortTitle());
                            alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
                            if (calendar.get(Calendar.MINUTE) < 10) {
                                alarmIntent.putExtra("Minute", "0" + String.valueOf(calendar.get(Calendar.MINUTE)));
                            } else {
                                alarmIntent.putExtra("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
                            }
                            alarmIntent.putExtra("ID", repEvent.getId().intValue());
                            pendingIntent = PendingIntent.getBroadcast(context, repEvent.getId().intValue(), alarmIntent, 0);

                            //Set AlarmManager --> NotificationReceiver will be called
                            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, notificationPerson), pendingIntent);
                        }
                    }
                }
            }
        }
    }

    public static void deleteAllAlarms(Context context) {
        //Get all events that are in the future to set the alarm
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        for (Event event : allEvents) {
            Intent alarmIntent = new Intent(context, NotificationReceiver.class);

            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

            //Set AlarmManager --> NotificationReceiver will be called
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }
    }

    public static void startAlarmForAllEvents(Context context) {
        //Get all events that are in the future to set the alarm
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        Person person = PersonController.getPersonWhoIam();
        for (Event event : allEvents) {
            Intent alarmIntent = new Intent(context, NotificationReceiver.class);

            //Get startTime an convert into a Calender to use it
            Date date = event.getStartTime();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            //Put extra Data which is needed for the Notification
            alarmIntent.putExtra("Event", event.getShortTitle());
            alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
            if (calendar.get(Calendar.MINUTE) < 10) {
                alarmIntent.putExtra("Minute", "0" + String.valueOf(calendar.get(Calendar.MINUTE)));
            } else {
                alarmIntent.putExtra("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
            }
            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId().intValue(), alarmIntent, 0);

            //Set AlarmManager --> NotificationReceiver will be called
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, person), pendingIntent);
        }
    }

    public static long calcNotificationTime(Calendar cal, Person person) {
        cal.add(Calendar.MINUTE, ((-1) * person.getNotificationTime()));
        return cal.getTimeInMillis();
    }
}
