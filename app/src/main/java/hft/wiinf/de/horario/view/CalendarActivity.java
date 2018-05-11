package hft.wiinf.de.horario.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    TextView calendarIsFloatMenuOpen;
    FloatingActionButton calendarFcMenu, calendarFcQrScan, calendarFcNewEvent;
    RelativeLayout rLayout_calendar_helper;
    ConstraintLayout cLayout_calendar_main;

    DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //initialize variables
        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        //FloatingButton
        calendarFcMenu = (FloatingActionButton) view.findViewById(R.id.calendar_floatingActionButtonMenu);
        calendarFcNewEvent = (FloatingActionButton) view.findViewById(R.id.calendar_floatingActionButtonNewEvent);
        calendarFcQrScan = (FloatingActionButton) view.findViewById(R.id.calendar_floatingActionButtonScan);
        rLayout_calendar_helper = view.findViewById(R.id.calendar_relativeLayout_helper);
        cLayout_calendar_main = view.findViewById(R.id.calendar_constrainLayout_main);
        calendarIsFloatMenuOpen = view.findViewById(R.id.calendar_hiddenField);

        calendarFcQrScan.hide();
        calendarFcNewEvent.hide();

        calendarFcMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarIsFloatMenuOpen.getText().equals("false")) {
                    showFABMenu();
                    calendarIsFloatMenuOpen.setText("true");
                } else {
                    closeFABMenu();
                    calendarIsFloatMenuOpen.setText("false");
                }
            }
        });

        calendarFcNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.calendar_relativeLayout_helper, new NewEventFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_calendar_helper.setVisibility(View.VISIBLE);
                closeFABMenu();
                calendarFcMenu.setVisibility(View.GONE);
            }
        });

        calendarFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.calendar_relativeLayout_helper, new QRScanFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_calendar_helper.setVisibility(View.VISIBLE);
                closeFABMenu();
                calendarFcMenu.setVisibility(View.GONE);
            }
        });

        cLayout_calendar_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        calendarTvMonth = view.findViewById(R.id.calendarTvMonth);
        calendarLvList = view.findViewById(R.id.calendarLvList);
        calendarTvDay = view.findViewById(R.id.calendarTvDay);

        calendarLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeFABMenu();
            }
        });

        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        calendarTvMonth.setText(monthFormat.format(today)); //initialize month field
        calendarTvDay.setText(dayFormat.format(today));
        calendarLvList.setAdapter(getAdapter(today));

        //TODO just for testing (add entry to database), delete
        hft.wiinf.de.horario.model.Event test = new hft.wiinf.de.horario.model.Event();
        test.setStartTime(new Date(1525465935000L)); //20.04.18 1524261326000L
        test.setEndTime(new Date(1525419135000L));
        test.setDescription("Termin 1");
        test.setAccepted(true);
        test.save();

        updateCompactCalendar();

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            //when a day get clicked, the date field will be updated and the events for the day displayed in the ListView
            public void onDayClick(Date dateClicked) {
                calendarTvDay.setText(dayFormat.format(dateClicked));
                calendarLvList.setAdapter(getAdapter(dateClicked));
                closeFABMenu();
            }

            @Override
            //handle everything when the user swipe the month
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTvMonth.setText(monthFormat.format(firstDayOfNewMonth));
                calendarTvDay.setText(dayFormat.format(firstDayOfNewMonth));
                calendarLvList.setAdapter(getAdapter(firstDayOfNewMonth));
            }
        });

        /** TODO maybe get deleted */
        calendarTvMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
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



    //is marking the day in the calendar for the parameter date
    public static void updateCompactCalendar(){
        List<hft.wiinf.de.horario.model.Event> acceptedEvents = EventController.findMyAcceptedEvents();
        for (int i = 0; i<acceptedEvents.size(); i++){
            if(calendarCvCalendar.getEvents(acceptedEvents.get(i).getStartTime().getTime()).size() == 0){
                Event event = new Event(Color.BLUE, acceptedEvents.get(i).getStartTime().getTime());
                calendarCvCalendar.addEvent(event, false);
            }
        }
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

    //TODO neue Methode die alle DB Einträge bei Programmstart lädt und mit updateCompactCalendar die markierung im Calendar durchführt




    public void showFABMenu() {
        calendarIsFloatMenuOpen.setText("true");
        calendarFcQrScan.show();
        calendarFcNewEvent.show();
        calendarFcMenu.setImageResource(R.drawable.ic_android_black_24dp);

    }

    public void closeFABMenu() {
        calendarIsFloatMenuOpen.setText("false");
        calendarFcQrScan.hide();
        calendarFcNewEvent.hide();
        calendarFcMenu.setImageResource(R.drawable.ic_android_black2_24dp);
    }

    public CalendarActivity() {
        super();
    }
}