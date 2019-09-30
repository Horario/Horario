package hft.wiinf.de.horario.controller;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class PersonController {

    public static void addPersonMe(Person person) {
        try {
            PersonController.savePerson(person);
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

    public static Person checkforPhoneNumber(String phoneNumber) {
        return new Select()
                .from(Person.class)
                .where("phoneNumber = ?", phoneNumber)
                .and("event_pending = ?", "")
                .executeSingle();
    }

    public static boolean personIsInvited(String phoneNumber, Event event){
        return new Select().from(Person.class)
                .where("event_pending = ?", String.valueOf(event.getId()))
                .and("phoneNumber = ?", phoneNumber)
                .exists();
    }

    public static void deleteInvitedPerson(String phoneNumber, String eventID){
        new Delete().from(Person.class)
                .where("event_pending = ?", eventID)
                .and("phoneNumber = ?", phoneNumber)
                .execute();
    }

    //get all persons
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

    //get persons that accepted the event
    public static List<Person> getEventAcceptedPersons(Event event) {
        return new Select().from(Person.class).where("event_accepted=?", event.getId()).execute();
    }

    //get all persons that cancelled the event
    public static List<Person> getEventCancelledPersons(Event event) {
        return new Select().from(Person.class).where("event_canceled=?", event.getId()).execute();
    }

}
