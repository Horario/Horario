package hft.wiinf.de.horario.controller;

import android.util.Log;

import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class PersonController {

    public static void addPersonMe(Person person) {
        try {
            Person me = PersonController.getPersonWhoIam();
            if (me == null)
                person.save();
            me.setName(person.getName());
            me.save();
        } catch (Exception e) {
            Log.d("PersonController", "addPersonMe:" + e.getMessage());
        }
    }

    public static Person getPersonWhoIam() {
        return new Select()
                .from(Person.class)
                .where("isItMe = ?", true)
                .executeSingle();
    }

    public static List<Person> getAllPersons() {
        return new Select()
                .from(Person.class)
                .execute();
    }

    public static void savePerson(Person person) {
        person.save();
    }

    public static void deletePerson(Person person) {
        person.delete();
    }

    public static List<Person> getEventAcceptedPersons(Event event) {
        return new Select().from(Person.class).where("event_accepted=?", event.getId()).execute();
    }

    public static List<Person> getEventCancelledPersons(Event event) {
        return new Select().from(Person.class).where("event_cacncelled=?", event.getId()).execute();
    }
}
