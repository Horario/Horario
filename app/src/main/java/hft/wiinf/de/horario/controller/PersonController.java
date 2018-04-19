package hft.wiinf.de.horario.controller;

import android.util.Log;

import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.hft.winf.de.horario.model.Person;

public class PersonController {

    public static void addPersonMe(Person person){
        try {
            person.save();
        }catch(Exception e){
            Log.d("PersonController", "addPersonMe:" + e.getMessage());
        }
    }

    public List<Person> getAllPersons(){
        return new Select()
                .from(Person.class)
                .execute();
    }

    public static Person getPersonWhoIam(){
        return new Select()
                .from(Person.class)
                .where("isItMe = ?", true)
                .executeSingle();
    }
}
