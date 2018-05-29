package hft.wiinf.de.horario.view;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.AcceptedState;

public class EventOverviewFragment extends Fragment {

    public static Date selectedMonth = new Date();
    static ListView overviewLvList;
    static TextView overviewTvMonth;
    static Context context = null;
    static DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    FloatingActionButton eventOverviewFcMenu, eventOverviewFcQrScan, eventOverviewFcNewEvent;
    Button overviewBtNext;
    TextView eventOverview_HiddenIsFloatingMenuOpen;
    Button overviewBtPrevious;
    Animation ActionButtonOpen, ActionButtonClose, ActionButtonRotateRight, ActionButtonRotateLeft;
    ConstraintLayout layout_eventOverview_main;
    ConstraintLayout layoutOverview;

    public static void update() {
        overviewTvMonth.setText(CalendarFragment.monthFormat.format(selectedMonth));
        overviewLvList.setAdapter(iterateOverMonth(selectedMonth));
    }

    //get all events for the selected month and save them in a adapter
    public static ArrayAdapter iterateOverMonth(Date date) {
        final ArrayList<Appointment> eventArray = new ArrayList<>();
        Calendar helper = Calendar.getInstance();
        helper.setTime(date);
        helper.set(Calendar.DAY_OF_MONTH, 1);
        helper.set(Calendar.HOUR_OF_DAY, 0);
        helper.set(Calendar.MINUTE, 0);
        int endDate = helper.get(Calendar.MONTH);
        while (helper.get(Calendar.MONTH) == endDate) {
            Calendar endOfDay = Calendar.getInstance();
            endOfDay.setTime(helper.getTime());
            endOfDay.add(Calendar.DAY_OF_MONTH, 1);
            List<hft.wiinf.de.horario.model.Event> eventList = EventController.findEventsByTimePeriod(helper.getTime(), endOfDay.getTime());
            if (eventList.size() > 0) {
                eventArray.add(new Appointment(CalendarFragment.dayFormat.format(helper.getTime()), 0));
            }
            for (int i = 0; i < eventList.size(); i++) {
                if (eventList.get(i).getAccepted().equals(AcceptedState.ACCEPTED)) {
                    eventArray.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 1));
                } else if (eventList.get(i).getAccepted().equals(AcceptedState.WAITING)) {
                    eventArray.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 2));
                } else {
                    eventArray.clear();
                }
            }
            helper.setTime(endOfDay.getTime());
        }
        if (eventArray.size() < 1) { //when no events this month do stuff
            eventArray.add(new Appointment("Du hast keine Termine diesen Monat", 0));
        }
        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, eventArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (eventArray.get(position).getType() == 1) {
                    textView.setTextColor(Color.DKGRAY);

                    textView.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_mydate,0);
                } else if (eventArray.get(position).getType() == 2) {
                    textView.setTextColor(Color.DKGRAY);
                } else if (eventArray.get(position).getType() == 3) {
                    textView.setTextColor(Color.DKGRAY);
                } else if (eventArray.get(position).getType() == 0) {
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setFocusable(false);
                }
                textView.setText(eventArray.get(position).getDescription());
                return textView;
            }
        };
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_overview, container, false);


        //initialize variables
        overviewLvList = view.findViewById(R.id.overviewTvList);
        overviewTvMonth = view.findViewById(R.id.overviewTvMonth);
        overviewBtNext = view.findViewById(R.id.overviewBtNext);
        overviewBtPrevious = view.findViewById(R.id.overviewBtPrevious);
        layoutOverview = view.findViewById(R.id.layoutOverview);
        context = this.getActivity();

        //Floating Button
        eventOverviewFcMenu = view.findViewById(R.id.eventOverview_floatingActionButtonMenu);
        eventOverviewFcNewEvent = view.findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
        eventOverviewFcQrScan = view.findViewById(R.id.eventOverview_floatingActionButtonScan);
        layout_eventOverview_main = view.findViewById(R.id.eventOverview_Layout_main);
        eventOverview_HiddenIsFloatingMenuOpen = view.findViewById(R.id.eventOverviewFabClosed);
        ActionButtonOpen = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonopen);
        ActionButtonClose = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonclose);
        ActionButtonRotateRight = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateright);
        ActionButtonRotateLeft = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateleft);
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        //selectedMonth = CalendarFragment.selectedMonth; TODO connect selectedMonth of Calendar and Overview
        selectedMonth = Calendar.getInstance().getTime();
        update();

        overviewBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedMonth);
                calendar.add(Calendar.MONTH, 1);
                selectedMonth.setTime(calendar.getTimeInMillis());
                update();
            }
        });

        overviewBtPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedMonth);
                calendar.add(Calendar.MONTH, -1);
                selectedMonth.setTime(calendar.getTimeInMillis());
                update();
            }
        });

        //handle actions after a event entry get clicked
        overviewLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment selectedItem = (Appointment) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), selectedItem.getDescription(), Toast.LENGTH_SHORT).show(); //TODO just for testing, delete
                closeFABMenu();
            }
        });

        eventOverviewFcMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventOverview_HiddenIsFloatingMenuOpen.getText().equals("false")) {
                    showFABMenu();
                    eventOverview_HiddenIsFloatingMenuOpen.setText("true");
                } else {
                    closeFABMenu();
                    eventOverview_HiddenIsFloatingMenuOpen.setText("false");
                }
            }
        });

        //Open new Fragment "NewEvent"
        eventOverviewFcNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewEventFragment newEventFragment = new NewEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "EventOverview");
                newEventFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_frameLayout, new NewEventFragment());
                fr.addToBackStack(null);
                fr.commit();
                closeFABMenu();
            }
        });

        //Open new Fragment "QRCodeScan"
        eventOverviewFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRScanFragment qrScanFragment = new QRScanFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "EventOverview");
                qrScanFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_frameLayout, new QRScanFragment());
                fr.addToBackStack(null);
                fr.commit();
                closeFABMenu();
            }
        });

        layoutOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        return view;
    }

    //Show the menu Buttons
    public void showFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("true");
        eventOverviewFcQrScan.show();
        eventOverviewFcNewEvent.show();
        eventOverviewFcQrScan.startAnimation(ActionButtonOpen);
        eventOverviewFcNewEvent.startAnimation(ActionButtonOpen);
        eventOverviewFcMenu.startAnimation(ActionButtonRotateRight);
        eventOverviewFcQrScan.setClickable(true);
        eventOverviewFcNewEvent.setClickable(true);
        eventOverviewFcMenu.setImageResource(R.drawable.ic_plusmenu);
    }

    //Hide the menu Buttons
    public void closeFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("false");
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        if (eventOverviewFcNewEvent.isClickable()) {
            eventOverviewFcQrScan.startAnimation(ActionButtonClose);
            eventOverviewFcNewEvent.startAnimation(ActionButtonClose);
            eventOverviewFcMenu.startAnimation(ActionButtonRotateLeft);
            eventOverviewFcQrScan.setClickable(false);
            eventOverviewFcNewEvent.setClickable(false);
            eventOverviewFcMenu.setImageResource(R.drawable.ic_plusmenu);
        }
    }

}

class Appointment {
    private String description;
    private int type;

    Appointment(String description, int type) {
        this.description = description;
        this.type = type;
    }

    /**
     * 0 = date, 1 = accepted, 2 = waiting, 3 = own
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}