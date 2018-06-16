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
        if (event.getAccepted() != AcceptedState.REJECTED) {
            if (event.getCreatorEventId() < 0)
                event.setCreatorEventId(event.save());
            event.save();
        }
        //if state is rejected and id!=null delete event from db
        else if (event.getId() != null) {
            EventController.deleteEvent(event);
        }
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
        //delete also the repeating events if applicable
        List<Event> repeatingEvents = EventController.findRepeatingEvents(event.getId());
        for (Event repeatingEvent : repeatingEvents) {
            repeatingEvent.delete();
        }
        event.delete();
    }

    public static Event getEventById(@NonNull Long id) {
        return Event.load(Event.class, id);
    }

    public static List<Event> getMyEventsByCreatorEventId(@NonNull Long creatorEventId) {
        return new Select().from(Event.class).where("creatorEventId=? AND startEvent=?", creatorEventId, creatorEventId).execute();

    }

    //find the list of events that start in the given period (enddate is not included!)
    public static List<Event> findEventsByTimePeriod(Date startDate, Date endDate) {
        return new Select().from(Event.class).where("starttime between ? AND ?", startDate.getTime(), endDate.getTime() - 1).orderBy("startTime,endTime,shortTitle").execute();
    }

    //get a list of all events
    public static List<Event> findMyEvents() {
        return new Select().from(Event.class).orderBy("startTime,endTime,shortTitle").execute();
    }

    //get a list of all events that I accepted
    public static List<Event> findMyAcceptedEvents() {
        return new Select().from(Event.class).where("accepted=?", true).orderBy("startTime,endTime,shortTitle").execute();
    }

    public static List<Event> getAllEvents() {
        return new Select().from(Event.class).orderBy("startTime, endTime, shortTitle").execute();
    }

    public static boolean createdEventsYet() {
        List<Event> resultSet = new Select().from(Event.class).execute();
        if (resultSet.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static List<Event> findMyAcceptedEventsInTheFuture() {
        return new Select().from(Event.class).where("accepted=? AND startTime>=?", AcceptedState.ACCEPTED, System.currentTimeMillis()).execute();
    }

    //find all events that point to the given event as an start event
    public static List<Event> findRepeatingEvents(@NonNull Long eventId) {
        return new Select().from(Event.class).where("startevent=?", eventId).orderBy("startTime,endTime,shortTitle").execute();
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
            repetitionEvent.setRepetition(firstEvent.getRepetition());
            repetitionEvent.setEndDate(firstEvent.getEndDate());
            repetitionEvent.setShortTitle(firstEvent.getShortTitle());
            repetitionEvent.setStartEvent(firstEvent);
            repetitionEvent.setCreatorEventId(firstEvent.getCreatorEventId());
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

    public static Event checkIfEventIsInDatabase(String description, String shortTitle,
                                                 String place,
                                                 Calendar startTime, Calendar endTime) {
        return new Select()
                .from(Event.class)
                .where("description = ?", description)
                .where("shortTitle = ?", shortTitle)
                .where("place = ?", place)
                .where("startTime = ?", startTime.getTimeInMillis())
                .where("endTime = ?", endTime.getTimeInMillis())
                .executeSingle();
    }


    public static boolean checkIfEventIsInDatabaseThroughId(Long eventIdInSMS) {
        List<Event> resultSet = new Select().from(Event.class).where("Id=?", eventIdInSMS).execute();
        if (resultSet.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}