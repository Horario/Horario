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
import java.util.Date;
import java.util.Locale;

import hft.wiinf.de.horario.R;

public class CalendarFragment extends Fragment {

    public static CompactCalendarView calendarCvCalendar;
    ListView calendarLvList;
    TextView calendarTvMonth;
    TextView calendarTvDay;
    TextView calendarIsFloatMenuOpen;

    FloatingActionButton calendarFcMenu, calendarFcQrScan, calendarFcNewEvent;
    ConstraintLayout cLayout_calendar_main;

    DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    DateFormat dayFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    Animation ActionButtonOpen, ActionButtonClose, ActionButtonRotateRight, ActionButtonRotateLeft;

    //TODO just a placeholder, maybe need a rework (1523318400000L)
    public static void addEvent(Date date) {
        Event event = new Event(Color.BLUE, date.getTime());
        calendarCvCalendar.addEvent(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
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
