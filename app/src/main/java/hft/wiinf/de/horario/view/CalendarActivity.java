package hft.wiinf.de.horario.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Date;

import hft.wiinf.de.horario.R;

//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";

    public static CompactCalendarView calendarCvCalendar;
    //ListView calendarLvList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        //calendarLvList = findViewById(R.id.calendarLvList);

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Toast.makeText(getActivity(), dateClicked.toString(), Toast.LENGTH_SHORT).show(); //TODO just a placeholder, need to be deleted
                //calendarLvList.setAdapter(getAdapter(dateClicked)); //TODO need test
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }

        });





        return view;
    }

    //TODO just a placeholder, maybe need a rework (1523318400000L)
    public static void addEvent(Date date){
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    public ArrayAdapter getAdapter(Date date){
        //TODO Datenbank zugriff, um alle Termine für das Datum zu erhalten und diese dann in die List zu speichern.
        ArrayList<String> eventArray = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, eventArray);
        return adapter;

    }
}
