package hft.wiinf.de.horario.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Date;

import hft.wiinf.de.horario.R;

public class CalendarActivity extends AppCompatActivity {

    public static CompactCalendarView calendarCvCalendar;
    //ListView calendarLvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarCvCalendar = findViewById(R.id.calendarCvCalendar);
        //calendarLvList = findViewById(R.id.calendarLvList);

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Toast.makeText(CalendarActivity.this, dateClicked.toString(), Toast.LENGTH_SHORT).show(); //TODO just a placeholder, need to be deleted
                //calendarLvList.setAdapter(getAdapter(dateClicked)); //TODO need test
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }

        });


    }

    //TODO just a placeholder, maybe need a rework (1523318400000L)
    public static void addEvent(Date date){
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    public ArrayAdapter getAdapter(Date date){
        //TODO Datenbank zugriff, um alle Termine für das Datum zu erhalten und diese dann in die List zu speichern.
        ArrayList<String> eventArray = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventArray);
        return adapter;

    }
}
