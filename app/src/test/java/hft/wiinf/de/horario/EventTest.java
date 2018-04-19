package hft.wiinf.de.horario;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.model.Repetitiondate;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleInstrumentedTest {


    @Before
    public void createStandardEvent() {
new Delete().from(Event.class).execute();
        new Delete().from(Person.class).execute();
        new Delete().from(Repetitiondate.class).execute();
        Event event = new Event();
        event.setAccepted(true);
        event.setCreator(new Person());
        event.setDescription("Testevent");
        event.setAccepted(true);
        event.setStartTime(new Time(0));
        event.setEndTime(new Time(79200000));
        List<Person> persons = new LinkedList<>();
        Person person = new Person();
        person.setPhoneNumber("+23/45678");
        person.setName("Hanswurst");
        persons.add(person);
        event.setPersonAccepted(persons);
        event.setPersonCancelled(persons);
        event.setCreator(person);

    }

    @Test
    //writes the standard person into database and reads it out (by ID)
    public void testsearchEventById() {

        Event event = new Event();
        Long id = event.save();
        Event eventFromDatabase = Event.load(Event.class, id);
        assertEquals(event, eventFromDatabase);

    }

    @Test
    //writes the standard event and deletes it
    public void deleteEvent() {
        Event event = new Event();
        Long id = event.save();
        event.delete();
        assertEquals(new Select().from(Event.class).execute().size(), 0);

    }

    @Test
    public void updateEvent() {
        Event event = new Event();
        Long id = event.save();
        event.setDescription("Hanswurst");
        event.save();
        assertEquals(Event.load(Event.class, id), event);
    }

    @Test
    //saves and search event of a date period
    public void searchByTimePeriod() {
        Event event = new Event();
        event.save();
        Event beforeEvent = new Event();
        beforeEvent.setStartTime(new Date(-1));
        beforeEvent.save();
        Event afterEvent = new Event();
        afterEvent.setStartTime(new Date(172800000));
        afterEvent.save();
        Event repeatingEvent = new Event();
        repeatingEvent.setStartTime(new Date(-86400000));
        List<Repetitiondate> repetitionDates = new LinkedList<>();
        Repetitiondate date = new Repetitiondate();
        date.setDate(new Date(1));
        repetitionDates.add(date);
        repeatingEvent.setRepetition(Repetition.DAILY);
        repeatingEvent.setRepetitionDates(repetitionDates);
        repeatingEvent.save();
        List<Event> events = Event.findEventByTimePeriod(new Date(0), new Date(172800));
        assertEquals(2, events.size());
        assertTrue(events.contains(event));
        assertTrue(events.contains(repeatingEvent));
        assertFalse(events.contains(beforeEvent));
        assertFalse(events.contains(afterEvent));
    }
}
