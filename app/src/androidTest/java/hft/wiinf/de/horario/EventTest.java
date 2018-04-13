package hft.wiinf.de.horario;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import hft.wiinf.de.horario.hft.winf.de.horario.model.Event;
import hft.wiinf.de.horario.hft.winf.de.horario.model.Person;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EventTest{
    Event event= new Event();
    //delete all persons from database to ensure corrects test results
    @BeforeClass
    public static void deleteAllPersons(){
    }
    //sets the Person to the default values to ensure the correct data before each test
    @Before
    public void createStandardPerson(){
        event.setAccepted(true);
        event.setCreator(new Person());
        event.setDescription("Testevent");
        event.setAccepted(true);
        event.setDate(new Date());
        event.setStartTime(new Time(Calendar.getInstance().getTimeInMillis()));
        Calendar.getInstance().add(Calendar.HOUR,14);
        event.setEndTime(new Time(Calendar.getInstance().getTimeInMillis()));
        List<Person> persons = new LinkedList<>();
        persons.add(new Person());
        event.setPersonAccepted(persons);
        event.setCreator(new Person());

    }
    @Test
    //writes the standard person into database and reads it out (by ID)
    public void searchPersonById(){
        event.save();
       // Event eventFromDatabase = Event.findById(Event.class, event.getId());
       // assertEquals(event, eventFromDatabase);

    }
    @Test
    //saves and delete the standard person
    public void deletePerson(){
        event.save();
        event.delete();
      //  assertEquals(Event.listAll(Event.class).size(),0);

    }
    @Test
    //saves and updates the standard person
    public void updatePerson(){
        event.save();
        event.setDescription("Hanswurst");
        event.save();
      //  Event eventFromDatabase = Event.findById(Event.class, event.getId());
       // assertEquals(event,eventFromDatabase);
    }
    @Test
    //saves and search event of a date period
    public void searchByTimePeriod(){
        event.save();
        Event beforeEvent = event;
        beforeEvent.setDate(new Date(-1));
        beforeEvent.save();
        Event afterEvent = event;
        beforeEvent.setDate(Calendar.getInstance().getTime());
        afterEvent.save();
        List<Event> events = Event.findEventByTimePeriod(new Date(0),new Date(86400000));
        assertEquals(1, events.size());
        assertTrue(events.contains(event));
        assertFalse(events.contains(beforeEvent));
        assertFalse(events.contains(afterEvent));
    }
}