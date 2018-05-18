package hft.wiinf.de.horario;

import com.activeandroid.query.Delete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class EventTest {
    Event event;
    Person person;
    Person person2;

    @Before
    public void createStandardEvent() {
        new Delete().from(Event.class).execute();
        new Delete().from(Person.class).execute();
        person = new Person();
        person.setPhoneNumber("+23/45678");
        person.setName("Hanswurst");
        person.setItMe(true);
        PersonController.savePerson(person);
        event = new Event(person);
        // event.setAccepted(true);
        event.setDescription("Testevent");
        //  event.setAccepted(true);
        event.setStartTime(new Date(-3600000));
        event.setEndTime(new Date(-3600000));
        List<Person> persons = new LinkedList<>();
        person = new Person();
        person.setPhoneNumber("+23/45678");
        person.setName("Hanswurst");
        person.setItMe(true);
        persons.add(person);
        person2 = new Person();
        person2.setPhoneNumber("+23/45678");
        person2.setName("HanswurstWurst");
        person2.setAcceptedEvent(event);
        EventController.saveEvent(event);
        PersonController.savePerson(person2);
    }

    @Test
    //writes the standard person into database and reads it out (by ID)
    public void searchEventById() {
        Event eventFromDatabase = EventController.getEventById(event.getId());
        assertEquals(event, eventFromDatabase);
        assertEquals(event.getCreator(), eventFromDatabase.getCreator());
        List<Person> accepted = PersonController.getEventAcceptedPersons(event);
        assertEquals(1, accepted.size());
        assertEquals(person2, accepted.get(0));


    }

    @Test
    //writes the standard event and deletes it
    public void deleteEvent() {
        EventController.deleteEvent(event);
        EventController.getEventById(event.getId());
        assertNull(EventController.getEventById(event.getId()));
        assertEquals(0, PersonController.getEventAcceptedPersons(event).size());

    }

    @Test
    public void updateEvent() {
        event.setDescription("Schönes Event");
        event.save();
        assertEquals(EventController.getEventById(event.getId()), event);
    }

    @Test
    //saves and search event of a date period
    public void searchByTimePeriod() {
        Event beforeEvent = new Event();
        beforeEvent.setStartTime(new Date(-90000000));
        beforeEvent.save();
        Event afterEvent = new Event();
        afterEvent.setStartTime(new Date(82800000));
        afterEvent.save();
        Event repeatingEvent = new Event();
        repeatingEvent.setStartTime(new Date(-90000000));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(-3600000);
        // EventController.saveSerialevent(repeatingEvent, Repetition.DAILY, calendar);
        List<Event> events = EventController.findEventsByTimePeriod(new Date(-3600000), new Date(82800000));
        assertEquals(2, events.size());
        assertTrue(events.contains(event));
        assertFalse(events.contains(repeatingEvent));
        assertFalse(events.contains(beforeEvent));
        assertFalse(events.contains(afterEvent));
    }
}
