package hft.wiinf.de.horario.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;

//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";

    public static CompactCalendarView calendarCvCalendar;
    ListView calendarLvList;
    TextView calendarTvMonth;
    TextView calendarTvDay;

    static DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    static DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    public static Date selectedMonth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //initialize variables
        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        calendarTvMonth = view.findViewById(R.id.calendarTvMonth);
        calendarLvList = view.findViewById(R.id.calendarLvList);
        calendarTvDay = view.findViewById(R.id.calendarTvDay);

        Date today = new Date();
        selectedMonth = today;
        calendarTvMonth.setText(monthFormat.format(today)); //initialize month field
        calendarTvDay.setText(dayFormat.format(today));
        calendarLvList.setAdapter(getAdapter(today));

        //TODO just for testing (add entry to database), delete
        hft.wiinf.de.horario.model.Event test = new hft.wiinf.de.horario.model.Event();
        test.setStartTime(new Date(1527413610000L)); //27.5.18
        test.setEndTime(new Date(1527413610000L));
        test.setDescription("Termin Test Overview 1");
        test.save();
        addEvent(test.getStartTime());

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            //when a day get clicked, the date field will be updated and the events for the day displayed in the ListView
            public void onDayClick(Date dateClicked) {
                calendarTvDay.setText(dayFormat.format(dateClicked));
                calendarLvList.setAdapter(getAdapter(dateClicked));
            }

            @Override
            //handle everything when the user swipe the month
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTvMonth.setText(monthFormat.format(firstDayOfNewMonth));
                calendarTvDay.setText(dayFormat.format(firstDayOfNewMonth));
                calendarLvList.setAdapter(getAdapter(firstDayOfNewMonth));
                selectedMonth = firstDayOfNewMonth;
            }
        });

        /** TODO maybe get deleted */
        calendarTvMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EventOverviewActivity.class);
                startActivity(intent);
            }
        });

        //handle actions after a event entry get clicked
        calendarLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position); //Get the clicked item as String
                Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
            }
        });

        return view;
    }



    //TODO just a placeholder, maybe need a rework (1523318400000L)
    //is marking the day in the calendar for the parameter date
    public static void addEvent(Date date){
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    /** TODO need a description */
    public ArrayAdapter getAdapter(Date date){
        //TODO Testing
        ArrayList<String> eventsAsString = new ArrayList<>();
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(date);
        endOfDay.add(Calendar.DAY_OF_MONTH, 1);
        List<hft.wiinf.de.horario.model.Event> eventList = EventController.findEventsByTimePeriod(date, endOfDay.getTime());
        for (int i = 0; i<eventList.size(); i++){
            eventsAsString.add(eventList.get(i).getDescription());
        }
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, eventsAsString);
        return adapter;
    }

    //TODO neue Methode die alle DB Einträge bei Programmstart lädt und mit addEvent die markierung im Calendar durchführt



}