package hft.wiinf.de.horario;


import android.support.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Test;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Event event = new Event();

    @Before
    public void createStandardEvent() {
new Delete().from(Event.class).execute();
        Event event = new Event();
        event.setAccepted(true);
        event.setCreator(new Person());
        event.setDescription("Testevent");
        event.setAccepted(true);
        event.setDate(new Date(0));
        event.setStartTime(new Time(0));
        event.setEndTime(new Time(79200000));
        List<Person> persons = new LinkedList<>();
        persons.add(new Person());
        event.setPersonAccepted(persons);
        event.setCreator(new Person());

    }

    @Test
    //writes the standard person into database and reads it out (by ID)
    public void searchEventById() {

        Event event = new Event();
        Long id = event.save();
        Event eventFromDatabase = Event.load(Event.class, id);
        assertEquals(event, eventFromDatabase);

    }

    @Test
    //writes the standard event and deletes it
    public void deleteEvent() {
        Long id = event.save();
        event.delete();
        assertEquals(new Select().from(Event.class).execute().size(), 0);

    }

    @Test
    public void updateEvent() {
        Long id = event.save();
        event.setDescription("Hanswurst");
        event.save();
        assertEquals(Event.load(Event.class, id), event);
    }

    @Test
    //saves and search event of a date period
    public void searchByTimePeriod() {
        event.save();
        Event beforeEvent = new Event();
        beforeEvent.setDate(new Date(-1));
        beforeEvent.save();
        Event afterEvent = new Event();
        afterEvent.setDate(new Date(172800000));
        afterEvent.save();
        Event repeatingEvent = new Event();
        repeatingEvent.setDate(new Date(-86400000));
        List<Date> repetitionDates = new LinkedList<>();
        repetitionDates.add(new Date(1));
        repeatingEvent.setRepetition(Repetition.DAILY);
        repeatingEvent.setRepetitionDates(repetitionDates);
        repeatingEvent.save();
        List<Event> events = Event.findEventByTimePeriod(new Date(0), new Date(86400000));
        List<Model> execute = new Select().from(Event.class).where("date BETWEEN -1 AND 172800000").execute();
        assertThat(events.size(), is(1));
        assertTrue(events.contains(event));
        assertFalse(events.contains(repeatingEvent));
        assertFalse(events.contains(beforeEvent));
        assertFalse(events.contains(afterEvent));
    }
}
