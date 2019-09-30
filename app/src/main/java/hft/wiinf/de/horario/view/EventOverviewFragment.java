package hft.wiinf.de.horario.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.InvitationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Invitation;
import hft.wiinf.de.horario.model.Person;

public class EventOverviewFragment extends Fragment {

    public static Date selectedMonth = new Date();
    ListView overviewLvList;
    TextView overviewTvMonth;
    Context context = null;
    static DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    FloatingActionButton eventOverviewFcMenu, eventOverviewFcQrScan, eventOverviewFcNewEvent, eventOverviewFcInvites;
    boolean fabIsOpened = false;
    ImageButton overviewBtNext;
    TextView eventOverview_HiddenIsFloatingMenuOpen, eventOverviewTvInvitationNumber;
    ImageButton overviewBtPrevious;
    Animation ActionButtonOpen, ActionButtonClose, ActionButtonRotateRight, ActionButtonRotateLeft;
    AlphaAnimation fadeIn, fadeOut;
    ConstraintLayout layout_eventOverview_main;
    ConstraintLayout layoutOverview;
    static List<Event> eventList = new ArrayList<>();


    public void update() {
        if(overviewTvMonth != null && overviewLvList != null) {
            overviewTvMonth.setText(CalendarFragment.monthFormat.format(selectedMonth));
            overviewLvList.setAdapter(iterateOverMonth(selectedMonth));
        }
    }

    //get all events for the selected month and save them in a adapter
    public ArrayAdapter iterateOverMonth(final Date date) {
        ArrayList<Appointment> appointmentArrayDay = new ArrayList<>();
        final ArrayList<Appointment> appointmentArray = new ArrayList<>();
        List<Event> allEvents = EventController.getAllEvents();

        Calendar helper = Calendar.getInstance();
        helper.setTime(date);
        helper.set(Calendar.DAY_OF_MONTH, 1);
        helper.set(Calendar.HOUR_OF_DAY, 0);
        helper.set(Calendar.MINUTE, 0);
        helper.set(Calendar.SECOND, 0);
        helper.set(Calendar.MILLISECOND, 0);
        int endDate = helper.get(Calendar.MONTH);
        while (helper.get(Calendar.MONTH) == endDate) {
            Calendar endOfDay = Calendar.getInstance();
            endOfDay.setTime(helper.getTime());
            endOfDay.set(Calendar.HOUR_OF_DAY, 23);
            endOfDay.set(Calendar.MINUTE, 59);
            endOfDay.set(Calendar.SECOND, 59);
            endOfDay.set(Calendar.MILLISECOND, 59);

            eventList.clear();
            for (Event event : allEvents) {
                if (event.getEndTime().after(helper.getTime()) && event.getEndTime().before(endOfDay.getTime())) {
                    eventList.add(event);
                }
            }

            if (eventList.size() > 0) {
                appointmentArrayDay.add(new Appointment(CalendarFragment.dayFormat.format(helper.getTime()), 0));
            }
            for (int i = 0; i < eventList.size(); i++) {
                if (eventList.get(i).getCreator().equals(PersonController.getPersonWhoIam())) {
                    appointmentArrayDay.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 3, eventList.get(i).getId(), eventList.get(i).getCreator()));
                } else {
                    if (eventList.get(i).getAccepted().equals(AcceptedState.ACCEPTED)) {
                        appointmentArrayDay.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 1, eventList.get(i).getId(), eventList.get(i).getCreator()));
                    } else if (eventList.get(i).getAccepted().equals(AcceptedState.WAITING)) {
                        appointmentArrayDay.add(new Appointment(timeFormat.format(eventList.get(i).getStartTime()) + " - " + timeFormat.format(eventList.get(i).getEndTime()) + " " + eventList.get(i).getShortTitle(), 2, eventList.get(i).getId(), eventList.get(i).getCreator()));
                    }
                }
            }
            if (appointmentArrayDay.size() > 1) {
                appointmentArray.addAll(appointmentArrayDay);
            }
            appointmentArrayDay.clear();
            helper.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (appointmentArray.size() < 1) { //when no events this month do stuff
            appointmentArray.add(new Appointment("Du hast keine Termine diesen Monat", 0));
        }
        return new ArrayAdapter<Appointment>(context, android.R.layout.simple_list_item_1, appointmentArray) {
            @NonNull
            @Override

            public int getViewTypeCount() {
                return getCount();
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                // 0 = date, 1 = accepted, 2 = waiting, 3 = own
                if (appointmentArray.get(position).getType() == 1) {
                    textView.setTextColor(Color.DKGRAY);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mydate_approved, 0);
                } else if (appointmentArray.get(position).getType() == 2) {
                    textView.setTextColor(Color.DKGRAY);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mydate_questionmark, 0);
                } else if (appointmentArray.get(position).getType() == 3) {
                    textView.setTextColor(Color.DKGRAY);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mydate, 0);
                } else if (appointmentArray.get(position).getType() == 0) {
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setFocusable(false);
                }
                textView.setText(appointmentArray.get(position).getDescription());
                return textView;
            }
        };
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
        eventOverviewTvInvitationNumber = view.findViewById(R.id.eventOverview_tvInvitationNumber);
        context = this.getActivity();

        //Floating Button
        eventOverviewFcMenu = view.findViewById(R.id.eventOverview_floatingActionButtonMenu);
        eventOverviewFcNewEvent = view.findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
        eventOverviewFcQrScan = view.findViewById(R.id.eventOverview_floatingActionButtonScan);
        eventOverviewFcInvites = view.findViewById(R.id.eventOverview_floatingActionButtonInvites);
        layout_eventOverview_main = view.findViewById(R.id.eventOverview_Layout_main);
        eventOverview_HiddenIsFloatingMenuOpen = view.findViewById(R.id.eventOverviewFabClosed);
        ActionButtonOpen = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonopen);
        ActionButtonClose = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonclose);
        ActionButtonRotateRight = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateright);
        ActionButtonRotateLeft = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateleft);
        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeOut= new AlphaAnimation(1.0f, 0.0f);

        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        eventOverviewFcInvites.hide();
        //selectedMonth = CalendarFragment.selectedMonth;
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
                if (fabIsOpened) {
                    closeFABMenu();
                }

                // 0 = date, 1 = accepted, 2 = waiting, 3 = own
                switch (selectedItem.getType()) {
                    case 1:
                        AcceptedEventDetailsFragment acceptedEventDetailsFragment = new AcceptedEventDetailsFragment();
                        Bundle bundleAcceptedEventId = new Bundle();
                        bundleAcceptedEventId.putLong("EventId", selectedItem.getId());
                        bundleAcceptedEventId.putString("fragment", "EventOverview");
                        acceptedEventDetailsFragment.setArguments(bundleAcceptedEventId);
                        FragmentTransaction fr1 = getFragmentManager().beginTransaction();
                        fr1.replace(R.id.eventOverview_frameLayout, acceptedEventDetailsFragment, "EventOverview");
                        fr1.addToBackStack("EventOverview");
                        fr1.commit();
                        break;
                    case 2:
                        SavedEventDetailsFragment savedEventDetailsFragment = new SavedEventDetailsFragment();
                        Bundle bundleSavedEventId = new Bundle();
                        bundleSavedEventId.putLong("EventId", selectedItem.getId());
                        bundleSavedEventId.putString("fragment", "EventOverview");
                        savedEventDetailsFragment.setArguments(bundleSavedEventId);
                        FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                        fr2.replace(R.id.eventOverview_frameLayout, savedEventDetailsFragment, "EventOverview");
                        fr2.addToBackStack("EventOverview");
                        fr2.commit();
                        break;
                    case 3:
                        MyOwnEventDetailsFragment myOwnEventDetailsFragment = new MyOwnEventDetailsFragment();
                        Bundle bundleMyOwnEventId = new Bundle();
                        bundleMyOwnEventId.putLong("EventId", selectedItem.getId());
                        bundleMyOwnEventId.putString("fragment", "EventOverview");
                        myOwnEventDetailsFragment.setArguments(bundleMyOwnEventId);
                        FragmentTransaction fr3 = getFragmentManager().beginTransaction();
                        fr3.replace(R.id.eventOverview_frameLayout, myOwnEventDetailsFragment, "EventOverview");
                        fr3.addToBackStack("EventOverview");
                        fr3.commit();
                        break;
                    default:
                        break;
                }


            }
        });

        eventOverviewFcMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventOverview_HiddenIsFloatingMenuOpen.getText().equals("false")) {
                    showFABMenu();
                    eventOverview_HiddenIsFloatingMenuOpen.setText(R.string.wahr);
                } else {
                    if (fabIsOpened) {
                        closeFABMenu();
                        eventOverview_HiddenIsFloatingMenuOpen.setText(R.string.falsch);
                    }
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
                fr.replace(R.id.eventOverview_frameLayout, newEventFragment);
                fr.addToBackStack(null);
                fr.commit();
                if (fabIsOpened) {
                    closeFABMenu();
                }

            }
        });

        eventOverviewFcInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InvitationFragment invitationFragment = new InvitationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fragment", "EventOverview");
                invitationFragment.setArguments(bundle);

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_frameLayout, invitationFragment);
                fr.addToBackStack(null);
                fr.commit();
                if (fabIsOpened){
                    closeFABMenu();
                }
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
                fr.replace(R.id.eventOverview_frameLayout, qrScanFragment);
                fr.addToBackStack(null);
                fr.commit();
                if (fabIsOpened) {
                    closeFABMenu();
                }

            }
        });

        layoutOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabIsOpened) {
                    closeFABMenu();
                }

            }
        });

        eventOverviewTvInvitationNumber.setText(String.valueOf(InvitationController.getNumberOfInvitations()));
        return view;
    }


    //Show the menu Buttons
    public void showFABMenu() {

        eventOverviewFcQrScan.startAnimation(ActionButtonOpen);
        eventOverviewFcNewEvent.startAnimation(ActionButtonOpen);
        eventOverviewFcMenu.startAnimation(ActionButtonRotateRight);
        eventOverviewFcInvites.startAnimation(ActionButtonOpen);
        eventOverviewFcQrScan.setClickable(true);
        eventOverviewFcInvites.setClickable(true);
        eventOverviewFcNewEvent.setClickable(true);
        eventOverview_HiddenIsFloatingMenuOpen.setText(R.string.wahr);
        eventOverviewFcQrScan.show();
        eventOverviewFcInvites.show();
        eventOverviewFcNewEvent.show();
        eventOverviewFcMenu.setImageResource(R.drawable.ic_plusmenu);
        if(InvitationController.getNumberOfInvitations() > 0){
            eventOverviewTvInvitationNumber.startAnimation(fadeIn);
            fadeIn.setDuration(300);
            eventOverviewTvInvitationNumber.setVisibility(View.VISIBLE);
        }
        fabIsOpened = true;
    }

    //Hide the menu Buttons
    public void closeFABMenu() {
        if (fabIsOpened) {
            eventOverview_HiddenIsFloatingMenuOpen.setText(R.string.falsch);
            eventOverviewFcQrScan.hide();
            eventOverviewFcInvites.hide();
            eventOverviewFcNewEvent.hide();
            if (eventOverviewFcNewEvent.isClickable()) {
                eventOverviewFcQrScan.startAnimation(ActionButtonClose);
                eventOverviewFcNewEvent.startAnimation(ActionButtonClose);
                eventOverviewFcInvites.startAnimation(ActionButtonClose);
                eventOverviewFcMenu.startAnimation(ActionButtonRotateLeft);
                eventOverviewFcQrScan.setClickable(false);
                eventOverviewFcInvites.setClickable(false);
                eventOverviewFcNewEvent.setClickable(false);
                eventOverviewFcMenu.setImageResource(R.drawable.ic_plusmenu);
                eventOverviewTvInvitationNumber.setVisibility(View.INVISIBLE);
                fabIsOpened = false;
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        eventOverviewTvInvitationNumber.setText(String.valueOf(InvitationController.getNumberOfInvitations()));
    }
}

class Appointment {
    private String description;
    private int type;
    private long id;
    private Person creator;

    Appointment(String description, int type, long id, Person creator) {
        this.description = description;
        this.type = type;
        this.id = id;
        this.creator = creator;
    }

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

    public long getId() {
        return id;
    }

    public Person getCreator() {
        return creator;

    }
}