package hft.wiinf.de.horario.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hft.wiinf.de.horario.R;

//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment{
    private static final String TAG = "CalendarFragmentActivity";

    ConstraintLayout calendarLayoutCalendar;
    public static CompactCalendarView calendarCvCalendar;
    public static Date selectedMonth;
    ListView calendarLvList;
    TextView calendarTvMonth;
    TextView calendarTvDay;
    ConstraintLayout calendarLayoutOverview;
    ListView overviewLvList; //TODO Format der ListView ändern um MockUps zu entsprechen
    TextView overviewTvMonth;

    DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Initialize all Gui-Elements
        final View view = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarLayoutCalendar = view.findViewById(R.id.calendarLayoutCalendar);
        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        calendarTvMonth = view.findViewById(R.id.calendarTvMonth);
        calendarLvList = view.findViewById(R.id.calendarLvList);
        calendarTvDay = view.findViewById(R.id.calendarTvDay);
        calendarLayoutOverview = view.findViewById(R.id.calendarLayoutOverview);
        overviewLvList = view.findViewById(R.id.overviewLvList);
        overviewTvMonth = view.findViewById(R.id.overviewTvMonth);

        Date today = new Date();
        calendarTvMonth.setText(monthFormat.format(today)); //initialize month field
        selectedMonth = today;
        calendarTvDay.setText(dayFormat.format(today));

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                calendarTvDay.setText(dayFormat.format(dateClicked));
                calendarLvList.setAdapter(getAdapter(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTvMonth.setText(monthFormat.format(firstDayOfNewMonth)); //is updating month field after a swipe
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                calendarTvDay.setText(dayFormat.format(firstDayOfNewMonth));
                selectedMonth = firstDayOfNewMonth;
            }

        });

        /** TODO */
        calendarTvMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "test", Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
                callOverview();
                //Intent i = new Intent(getActivity(), EventOverview.class);
                //startActivity(i);
            }
        });

        return view;
    }

    //TODO just a placeholder, maybe need a rework (1523318400000L)
    public static void addEvent(Date date){
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    /** TODO need a description */
    public ArrayAdapter getAdapter(Date date){
        //TODO Datenbank zugriff, um alle Termine für das Datum zu erhalten und diese dann in die List zu speichern.
        ArrayList<String> eventArray = new ArrayList<>();
        eventArray.add("Test eins"); //TODO just for testing, delete
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, eventArray);
        return adapter;
    }

    public void callOverview(){
        calendarLayoutCalendar.setVisibility(View.GONE);
        calendarLayoutOverview.setVisibility(View.VISIBLE);
        overviewTvMonth.setText(monthFormat.format(selectedMonth));
        overviewLvList.setAdapter(iterateOverMonth());
    }

    public ArrayAdapter iterateOverMonth(){ //TODO create own Adapter
        ArrayList<String> eventArray = new ArrayList<>();
        Date day = new Date(CalendarActivity.selectedMonth.getTime());
        int endDate = CalendarActivity.selectedMonth.getMonth();
        while (day.getMonth() <= endDate){
            eventArray.add(dayFormat.format(day));
            day.setTime(day.getTime() + 86400000); //TODO rework?
            //TODO Termine aus der DB wählen die am jeweiligen Tag stattfinden
        }
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, eventArray);
        return adapter;
    }


}