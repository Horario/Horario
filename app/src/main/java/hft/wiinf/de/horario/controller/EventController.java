package hft.wiinf.de.horario.controller;

import android.support.annotation.NonNull;

import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;

public class EventController {
    //saves (update or create)an event
    public static void saveEvent(Event event) {
        event.save();
    }

    public static void deleteEvent(Event event) {
        //deletes all persons that accepted the event
        for (Person person : PersonController.getEventCancelledPersons(event)) {
            PersonController.deletePerson(person);
        }

        //deletes all persons that cancelled the event
        for (Person person : PersonController.getEventAcceptedPersons(event)) {
            PersonController.deletePerson(person);
        }
        event.delete();
    }

    public static Event getEventById(@NonNull Long id) {
        return Event.load(Event.class, id);
    }

    public static Event getEventByCreatorEventId(@NonNull Long creatorEventId) {
        List<Event> resultSet = new Select().from(Event.class).where("creatorEventId=? AND creator.isItMe=?", creatorEventId, true).execute();
        return resultSet.get(0);
    }


    //find the list of events that start in the given period (enddate is ecluded!)
    public static List<Event> findEventsByTimePeriod(Date startDate, Date endDate) {
        return new Select().from(Event.class).where("starttime between ? AND ?", startDate.getTime(), endDate.getTime() - 1).execute();

    }

    //get a list of all events that I accepted
    public static List<Event> findMyAcceptedEvents() {
        return new Select().from(Event.class).where("accepted=?", true).execute();
    }

    // saves a serial event, firstEvent="StartEvent", repetition: repetition frequence (daily, ...), endOfRepetiton: last day of the repetition (including)
    public static void saveSerialevent(Event firstEvent, Repetition repetition, Calendar endOfRepetition) {
        int fieldNumber;
        //determine field number of calendar object that should be updated laer (day, month or year)
        switch (repetition) {
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
        saveEvent(firstEvent);
        for (int i = 1; ; i++) {
            //copy first event in new temporary event and update the needed field of start and end time
            Event repetitionEvent = new Event();
            repetitionEvent.setPlace(firstEvent.getPlace());
            repetitionEvent.setDescription(firstEvent.getDescription());
            repetitionEvent.setAccepted(firstEvent.isAccepted());
            repetitionEvent.setCreator(firstEvent.getCreator());
            repetitionEvent.setStartTime(firstEvent.getStartTime());
            repetitionEvent.setEndTime(firstEvent.getEndTime());
            Calendar temporary = new GregorianCalendar();
            temporary.setTime(repetitionEvent.getStartTime());
            temporary.add(fieldNumber, i);
            repetitionEvent.setStartTime(temporary.getTime());
            temporary.setTime(repetitionEvent.getEndTime());
            temporary.add(fieldNumber, i);
            repetitionEvent.setEndTime(temporary.getTime());
            //if end of repetition is overruned, stopp eelse save the new Event;
            if (repetitionEvent.getStartTime().after(endOfRepetition.getTime()))
                break;
            saveEvent(repetitionEvent);
        }

    }
}

