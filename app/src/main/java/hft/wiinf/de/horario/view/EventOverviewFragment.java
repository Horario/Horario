package hft.wiinf.de.horario.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    static ListView overviewLvList;
    static TextView overviewTvMonth;
    Button overviewBtNext;
    Button overviewBtPrevious;
    public static Date selectedMonth = new Date();
    FloatingActionButton eventOverviewFcMenu, eventOverviewFcQrScan, eventOverviewFcNewEvent;
    RelativeLayout rLayout_eventOverview_helper;
    ConstraintLayout layout_eventOverview_main;
    TextView eventOverview_HiddenIsFloatingMenuOpen;
    ConstraintLayout layoutOverview;
    ConstraintLayout layoutHelper;
    static Context context = null;
    static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

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
        layoutHelper = view.findViewById(R.id.layoutHelper);
        context = this.getActivity();

        //Floating Button
        eventOverviewFcMenu = view.findViewById(R.id.eventOverview_floatingActionButtonMenu);
        eventOverviewFcNewEvent = view.findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
        eventOverviewFcQrScan = view.findViewById(R.id.eventOverview_floatingActionButtonScan);
        rLayout_eventOverview_helper = view.findViewById(R.id.eventOverview_relativeLayout_helper);
        layout_eventOverview_main = view.findViewById(R.id.eventOverview_Layout_main);
        eventOverview_HiddenIsFloatingMenuOpen = view.findViewById(R.id.eventOverviewFabClosed);
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        selectedMonth = CalendarFragment.selectedMonth;
        update();

        overviewBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedMonth);
                calendar.add(Calendar.MONTH, 1);
                selectedMonth.setTime(calendar.getTimeInMillis());
                //selectedMonth.setMonth(selectedMonth.getMonth()+1); //TODO delete
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
                //selectedMonth.setMonth(selectedMonth.getMonth()-1); //TODO delete
                update();
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
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_relativeLayout_helper, new NewEventFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                layoutHelper.setVisibility(View.VISIBLE);
                layoutOverview.setVisibility(View.GONE);
                closeFABMenu();
                eventOverviewFcMenu.setVisibility(View.GONE);
            }
        });

        //Open new Fragment "QRCodeScan"
        eventOverviewFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_relativeLayout_helper, new QRScanFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                layoutHelper.setVisibility(View.VISIBLE);
                layoutOverview.setVisibility(View.GONE);
                closeFABMenu();
                eventOverviewFcMenu.setVisibility(View.GONE);
            }
        });

        return view;
    }

    public static void update(){
        overviewTvMonth.setText(CalendarFragment.monthFormat.format(selectedMonth));
        overviewLvList.setAdapter(iterateOverMonth(selectedMonth));
    }

    //get all events for the selected month and save them in a adapter
    public static ArrayAdapter iterateOverMonth(Date date){ //TODO create own Adapter
        final ArrayList<Appointment> eventArray = new ArrayList<>();
        Date day = new Date(date.getTime());
        int endDate = date.getMonth();
        while (day.getMonth() <= endDate){
            Calendar endOfDay = Calendar.getInstance();
            endOfDay.setTime(day);
            endOfDay.add(Calendar.DAY_OF_MONTH, 1);
            List<hft.wiinf.de.horario.model.Event> eventList = EventController.findEventsByTimePeriod(day, endOfDay.getTime());
            if (eventList.size()>0){
                eventArray.add(new Appointment(CalendarFragment.dayFormat.format(day), 0));
            }
            for (int i = 0; i<eventList.size(); i++){
                if(eventList.get(i).getAccepted().equals(AcceptedState.ACCEPTED)){
                    eventArray.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 1));
                }else if(eventList.get(i).getAccepted().equals(AcceptedState.WAITING)){
                    eventArray.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 2));
                }else{
                    eventArray.clear();
                }
            }
            day.setTime(endOfDay.getTimeInMillis());
        }
        if(eventArray.size() < 1){ //when no events this month do stuff
            eventArray.add(new Appointment("Du hast keine Termine diesen Monat", 0));
        }
        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, eventArray){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (eventArray.get(position).getType() == 1){
                    textView.setBackgroundColor(Color.GREEN);
                }else if(eventArray.get(position).getType() == 2){
                    textView.setBackgroundColor(Color.RED);
                }else if(eventArray.get(position).getType() == 0){
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setFocusable(false);
                }
                textView.setText(eventArray.get(position).getDescription());
                return textView;
            }
        };
        return adapter;
    }

    //Show the menu Buttons
    public void showFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("true");
        eventOverviewFcQrScan.show();
        eventOverviewFcNewEvent.show();
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black_24dp);
    }

    //Hide the menu Buttons
    public void closeFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("false");
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black2_24dp);
    }


}

class Appointment{
    private String description;
    private int type;

    Appointment(String description, int type){
        this.description = description;
        this.type = type;
    }

    /** 0 = date, 1 = accepted, 2 = waiting, 3 = rejected */
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