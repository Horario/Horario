package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    RelativeLayout newFragment_relativLayout, calendar_temp_relativeLayout_main;
    TextView calendarHeadline_textView;

    //Temp Method to Change on Frame CaledarActivity with QRScannerActivity Fragments
    //ToDo Diese Methode muss später auf den Floatingbutten gebunden werden dazu brauch es auch eine Anpassung der XML stattfinden. -> Es muss ein neuer Container erstellt werden in den dann die das Fragment geladen wird. Zielsetzung bis ende der Weoche sollte das gehen!
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        calendarCvCalendar = view.findViewById(R.id.calendarCvCalendar);
        calendarTvMonth = view.findViewById(R.id.calendarTvMonth);
        calendarLvList = view.findViewById(R.id.calendarLvList);
        calendarTvDay = view.findViewById(R.id.calendarTvDay);

        //ToDo -> TestButtons löschen
        Button scnbtn = view.findViewById(R.id.gotoscanner);
        Button genbtn = view.findViewById(R.id.gotogenerator);

        Date today = new Date();
        calendarTvMonth.setText(monthFormat.format(today)); //initialize month field
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
            }

        });

        //Change onClick the Fragment CalendarActivity with the QRScannerActivity
        scnbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new QRScannerActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    calendar_temp_relativeLayout_main.setVisibility(View.GONE);
                    calendarLvList.setVisibility(View.GONE);
                    calendarTvDay.setVisibility(View.GONE);
                    calendarTvMonth.setVisibility(View.GONE);
                    calendarCvCalendar.setVisibility(View.GONE);
                    newFragment_relativLayout.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Log.d(TAG, "CalendarActivity:" + e.getMessage());
                }
            }
        });
        //Change onClick the Fragment CalendarActivity with the QRGenerator
        genbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new QRGeneratorActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    calendar_temp_relativeLayout_main.setVisibility(View.GONE);
                    calendarLvList.setVisibility(View.GONE);
                    calendarTvDay.setVisibility(View.GONE);
                    calendarTvMonth.setVisibility(View.GONE);
                    calendarCvCalendar.setVisibility(View.GONE);
                    newFragment_relativLayout.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Log.d(TAG, "CalendarActivity:" + e.getMessage());
                }
            }
        });
        return view;
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

    //Method will be called directly after View is created
    public void onViewCreated(final View view, Bundle saveInstanceStage) {
        newFragment_relativLayout = view.findViewById(R.id.newFragment);
        calendar_temp_relativeLayout_main = view.findViewById(R.id.calendar_temp_relativeLayout_main);
        }

}
