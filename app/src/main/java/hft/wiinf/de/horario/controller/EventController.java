package hft.wiinf.de.horario.controller;

import android.support.annotation.NonNull;

import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetitiondate;

public class EventController {
    //saves (update or create)an event
    public static void saveEvent(Event event) {
        event.save();
    }

    public static void deleteEvent(Event event) {
        event.delete();
//deletes all persons that accepted the event
        for (Person person : PersonController.getEventCancelledPersons(event)) {
            person.delete();
        }
        //deletes all persons that cancelled the event
        for (Person person : PersonController.getEventAcceptedPersons(event)) {
            person.delete();
        }
        //deletes all repetition dates
        for (Repetitiondate date : RepetitiondateController.) {
            date.delete();
        }
    }

    public static Event getEventById(@NonNull Long id) {
        return Event.load(Event.class, id);
    }

    public static List<Event> findEventsByTimePeriod(Date startDate, Date endDate) {
        List<Event> events = new Select().from(Event.class).leftJoin(Repetitiondate.class).on("events.id=repetitiondates.event_id").where("starttime between ? AND ?", startDate.getTime(), endDate.getTime()).or("date BETWEEN ? AND ?", startDate.getTime(), endDate.getTime()).execute();
        return events;
    }

    public static List<Event> findEventsByTimeStamp(Date date) {
        List<Event> events = new Select().from(Event.class).leftJoin(Repetitiondate.class).on("events.id=repetitiondates.event_id").where("starttime =?", date.getTime()).or("date =", date.getTime()).execute();
        return events;
    }
}

