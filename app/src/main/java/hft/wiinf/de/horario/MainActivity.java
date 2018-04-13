package hft.wiinf.de.horario;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.hft.winf.de.horario.model.Event;
import hft.wiinf.de.horario.hft.winf.de.horario.model.Person;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("horario.db", MODE_NO_LOCALIZED_COLLATORS, null);
        sqLiteDatabase.beginTransaction();
        String[] args = {"date"};
        Cursor cursor = sqLiteDatabase.query("events", args, null, null, null, null, null, null);
        sqLiteDatabase.endTransaction();
    }
    }

