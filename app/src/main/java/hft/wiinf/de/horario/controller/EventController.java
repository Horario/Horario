package hft.wiinf.de.horario.controller;

import android.support.annotation.NonNull;

import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class EventController {
    //saves (update or create)an event
    public static void saveEvent(@NonNull Event event) {
        event.setCreatorEventId(event.save());
        event.save();
    }

    public static void deleteEvent(@NonNull Event event) {
        //deletes all persons that accepted the event
        for (Person person : PersonController.getEventCancelledPersons(event)) {
            PersonController.deletePerson(person);
        }
        //deletes all persons that cancelled the event
        for (Person person : PersonController.getEventAcceptedPersons(event)) {
            PersonController.deletePerson(person);
        }
        //if other events point to the deleted event set only accepted state to rejected
        if (EventController.findRepeatingEvents(event.getId()).size() > 0) {
            event.setAccepted(AcceptedState.REJECTED);
            event.save();
        } else {
            event.delete();
        }
    }

    public static Event getEventById(@NonNull Long id) {
        return Event.load(Event.class, id);
    }

    //find the list of events that start in the given period (enddate is not included!)
    public static List<Event> findEventsByTimePeriod(Date startDate, Date endDate) {
        return new Select().from(Event.class).where("starttime between ? AND ?", startDate.getTime(), endDate.getTime() - 1).execute();

    }

    //get a list of all events that I accepted
    public static List<Event> findMyAcceptedEvents() {
        return new Select().from(Event.class).where("accepted=?", true).execute();
    }

    //find all events that point to the given event as an start event
    public static List<Event> findRepeatingEvents(@NonNull Long eventId) {
        return new Select().from(Event.class).where("startevent=?", eventId).execute();
    }

    // saves a serial event, firstEvent="StartEvent",
    public static void saveSerialevent(Event firstEvent) {
        int fieldNumber;
        //determine field number of calendar object that should be updated laer (day, month or year)
        switch (firstEvent.getRepetition()) {
            case DAILY:
                fieldNumber = Calendar.DAY_OF_MONTH;
                break;
            case WEEKLY:
                fieldNumber = Calendar.WEEK_OF_YEAR;
                break;
            case MONTHLY:
                fieldNumber = Calendar.MONTH;
                break;
            default:
                fieldNumber = Calendar.YEAR;
        }
        //save first event;
        firstEvent.setStartEvent(firstEvent);
        saveEvent(firstEvent);
        for (int i = 1; ; i++) {
            //copy first event in new temporary event
            Event repetitionEvent = new Event(firstEvent.getCreator());
            repetitionEvent.setPlace(firstEvent.getPlace());
            repetitionEvent.setDescription(firstEvent.getDescription());
            repetitionEvent.setAccepted(firstEvent.getAccepted());
            repetitionEvent.setEndTime(firstEvent.getEndTime());
            repetitionEvent.setShortTitle(firstEvent.getShortTitle());
            repetitionEvent.setStartEvent(firstEvent);
            //copy the start and end time of the start event into a temporary variable, add 1 to the corresponding field and save the new value into the next event
            Calendar temporary = new GregorianCalendar();
            temporary.setTime(firstEvent.getStartTime());
            temporary.add(fieldNumber, i);
            repetitionEvent.setStartTime(temporary.getTime());
            temporary.setTime(firstEvent.getEndTime());
            temporary.add(fieldNumber, i);
            repetitionEvent.setEndTime(temporary.getTime());
            //if end of repetition is overruned, stopp else save the new Event;
            if (repetitionEvent.getStartTime().after(firstEvent.getEndDate()))
                break;
            //save the new event
            saveEvent(repetitionEvent);
        }

    }
}