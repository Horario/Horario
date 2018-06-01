package hft.wiinf.de.horario.view;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import hft.wiinf.de.horario.model.AcceptedState;

public class CalendarFragment extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";

    public static CompactCalendarView calendarCvCalendar;
    public static Date selectedMonth;
    static ListView calendarLvList;
    static TextView calendarTvMonth;
    TextView calendarIsFloatMenuOpen;
    FloatingActionButton calendarFcMenu, calendarFcQrScan, calendarFcNewEvent;
    ConstraintLayout cLayout_calendar_main;
    static TextView calendarTvDay;
    static Context context = null;
    static DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    static DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    Animation ActionButtonOpen, ActionButtonClose, ActionButtonRotateRight, ActionButtonRotateLeft;

    public static void update(Date date) {
        calendarTvDay.setText(dayFormat.format(date));
        calendarLvList.setAdapter(getAdapter(date));
        calendarTvMonth.setText(monthFormat.format(date));
        updateCompactCalendar();
    }

    //is marking the day in the calendar for the parameter date
    public static void updateCompactCalendar() {
        List<hft.wiinf.de.horario.model.Event> acceptedEvents = EventController.findMyEvents();
        for (int i = 0; i < acceptedEvents.size(); i++) {
            if (calendarCvCalendar.getEvents(acceptedEvents.get(i).getStartTime().getTime()).size() == 0 && acceptedEvents.get(i).getAccepted() != AcceptedState.REJECTED) {
                Event event = new Event(Color.DKGRAY, acceptedEvents.get(i).getStartTime().getTime());
                calendarCvCalendar.addEvent(event, true);
            }
        }
    }

    public static ArrayAdapter getAdapter(Date date) {
        ArrayList<String> eventsAsString = new ArrayList<>();
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(date);
        endOfDay.add(Calendar.DAY_OF_MONTH, 1);
        final List<hft.wiinf.de.horario.model.Event> eventList = EventController.findEventsByTimePeriod(date, endOfDay.getTime());
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getAccepted() != AcceptedState.REJECTED) {
                eventsAsString.add(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle());
            }
        }
        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, eventsAsString) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (eventList.get(position).getAccepted().equals(AcceptedState.ACCEPTED)) {
                    textView.setTextColor(Color.DKGRAY);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_mydate,0);
                } else if (eventList.get(position).getAccepted().equals(AcceptedState.WAITING)) {
                    textView.setTextColor(Color.DKGRAY);
                } else {
                    textView.setTextColor(Color.DKGRAY);
                }
                return textView;
            }
        };
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //initialize variables
        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        calendarTvMonth = view.findViewById(R.id.calendarTvMonth);
        calendarLvList = view.findViewById(R.id.calendarLvList);
        calendarTvDay = view.findViewById(R.id.calendarTvDay);
        context = this.getActivity();
        //FloatingButton
        calendarFcMenu = view.findViewById(R.id.calendar_floatingActionButtonMenu);
        calendarFcNewEvent = view.findViewById(R.id.calendar_floatingActionButtonNewEvent);
        calendarFcQrScan = view.findViewById(R.id.calendar_floatingActionButtonScan);
        cLayout_calendar_main = view.findViewById(R.id.calendar_constrainLayout_main);
        calendarIsFloatMenuOpen = view.findViewById(R.id.calendar_hiddenField);

        ActionButtonOpen = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonopen);
        ActionButtonClose = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonclose);
        ActionButtonRotateRight = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateright);
        ActionButtonRotateLeft = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateleft);

        calendarFcQrScan.hide();
        calendarFcNewEvent.hide();

        //Date today = new Date();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        selectedMonth = today.getTime();
        calendarTvMonth.setText(monthFormat.format(today.getTime())); //initialize month field
        update(today.getTime());

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            //when a day get clicked, the date field will be updated and the events for the day displayed in the ListView
            public void onDayClick(Date dateClicked) {
                update(dateClicked);
                closeFABMenu();
            }

            @Override
            //handle everything when the user swipe the month
            public void onMonthScroll(Date firstDayOfNewMonth) {
                update(firstDayOfNewMonth);
                selectedMonth = firstDayOfNewMonth;
            }
        });

        //handle actions after a event entry get clicked
        calendarLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position); //Get the clicked item as String
                Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
                closeFABMenu();
            }
        });

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
                NewEventFragment newEventFragment = new NewEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "Calendar");
                newEventFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.calendar_frameLayout, newEventFragment, "NewEvent");
                fr.addToBackStack("NewEvent");
                fr.commit();
                closeFABMenu();
            }
        });

        calendarFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRScanFragment qrScanFragment = new QRScanFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "Calendar");
                qrScanFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.calendar_frameLayout, qrScanFragment, "QrScan");
                fr.addToBackStack("QrScan");
                fr.commit();
                closeFABMenu();
            }
        });

        cLayout_calendar_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        return view;
    }

    public void showFABMenu() {
        calendarFcQrScan.startAnimation(ActionButtonOpen);
        calendarFcNewEvent.startAnimation(ActionButtonOpen);
        calendarFcMenu.startAnimation(ActionButtonRotateRight);
        calendarFcQrScan.setClickable(true);
        calendarFcNewEvent.setClickable(true);
        calendarIsFloatMenuOpen.setText("true");
        calendarFcQrScan.show();
        calendarFcNewEvent.show();
        calendarFcMenu.setImageResource(R.drawable.ic_plusmenu);
    }

    public void closeFABMenu() {
        if (calendarIsFloatMenuOpen.getText().equals("true")) {
            calendarFcQrScan.startAnimation(ActionButtonClose);
            calendarFcNewEvent.startAnimation(ActionButtonClose);
            calendarFcMenu.startAnimation(ActionButtonRotateLeft);
            calendarFcQrScan.setClickable(false);
            calendarFcNewEvent.setClickable(false);
            calendarIsFloatMenuOpen.setText("false");
            calendarFcQrScan.hide();
            calendarFcNewEvent.hide();
            calendarFcMenu.setImageResource(R.drawable.ic_plusmenu);
        }
    }

}