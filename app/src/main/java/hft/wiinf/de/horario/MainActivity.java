package hft.wiinf.de.horario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.activeandroid.ActiveAndroid;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.initialize(this);
        setContentView(R.layout.activity_main);
        Event event = new Event();
        event.setAccepted(true);
        event.setCreator(new Person());
        event.setDescription("Testevent");
        event.setAccepted(true);
        event.setStartTime(new Date(0));
        event.setEndTime(new Date(79200000));
        List<Person> persons = new LinkedList<>();
        persons.add(new Person());
        event.setPersonAccepted(persons);
        event.setCreator(new Person());
        event.save();
    }
    }

