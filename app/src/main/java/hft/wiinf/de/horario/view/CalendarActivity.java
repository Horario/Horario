package hft.wiinf.de.horario.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Date;
import java.util.Locale;

import hft.wiinf.de.horario.R;

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

        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        //FloatingButton
        calendarFcMenu = view.findViewById(R.id.calendar_floatingActionButtonMenu);
        calendarFcNewEvent = view.findViewById(R.id.calendar_floatingActionButtonNewEvent);
        calendarFcQrScan = view.findViewById(R.id.calendar_floatingActionButtonScan);
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
                closeFABMenu();
                calendarTvMonth.setVisibility(View.GONE);
                calendarCvCalendar.setVisibility(View.GONE);
                calendarLvList.setVisibility(View.GONE);
                calendarTvDay.setVisibility(View.GONE);
                calendarFcMenu.setVisibility(View.GONE);
                rLayout_calendar_helper.setVisibility(View.VISIBLE);
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
        calendarTvMonth.setText(monthFormat.format(today)); //initialize month field
        calendarTvDay.setText(dayFormat.format(today));

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                calendarTvDay.setText(dayFormat.format(dateClicked));
                calendarLvList.setAdapter(getAdapter(dateClicked));
                closeFABMenu();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTvMonth.setText(monthFormat.format(firstDayOfNewMonth)); //is updating month field after a swipe
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                calendarTvDay.setText(dayFormat.format(firstDayOfNewMonth));
            }

        });

        /** TODO */
        calendarTvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //TODO just a placeholder, maybe need a rework (1523318400000L)
    public static void addEvent(Date date) {
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    /**
     * TODO need a description
     */
    public ArrayAdapter getAdapter(Date date) {
        //TODO Datenbank zugriff, um alle Termine für das Datum zu erhalten und diese dann in die List zu speichern.
        ArrayList<String> eventArray = new ArrayList<>();
        eventArray.add("Test eins"); //TODO just for testing, delete
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, eventArray);
        return adapter;
    }

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