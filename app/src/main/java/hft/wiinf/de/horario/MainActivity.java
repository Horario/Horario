package hft.wiinf.de.horario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.model.Repetitiondate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.dispose();
        ActiveAndroid.initialize(this);
        setContentView(R.layout.activity_new_event);
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

    }
    }

