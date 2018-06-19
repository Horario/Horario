package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Objects;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;

public class MyOwnEventDetailsFragment extends Fragment {

    Button myOwnEventDetailsButtonShowQR, myOwnEventDetailsButtonShowAcceptances;
    RelativeLayout rLayout_myOwnEvent_helper;
    ConstraintLayout myOwnEventDetails_constraintLayout;
    TextView myOwnEventeventDescription, myOwnEventYourAppointment;
    Event selectedEvent, event;
    StringBuffer eventToStringBuffer;

    public MyOwnEventDetailsFragment() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the CalenderFragment to Start later a DB Request
    @SuppressLint("LongLogTag")
    public Long getEventID() {
        Bundle MYEventIdBundle = getArguments();
        assert MYEventIdBundle != null;
        Long MYEventIdLongResult = MYEventIdBundle.getLong("EventId");
        return MYEventIdLongResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_own_event_details, container, false);
        myOwnEventDetailsButtonShowAcceptances = view.findViewById(R.id.myOwnEventDetailsButtonShowAcceptances);
        myOwnEventDetailsButtonShowQR = view.findViewById(R.id.myOwnEventDetailsButtonShowQR);
        rLayout_myOwnEvent_helper = view.findViewById(R.id.myOwnEvent_relativeLayout_helper);
        myOwnEventeventDescription = view.findViewById(R.id.myOwnEventeventDescription);
        myOwnEventYourAppointment = view.findViewById(R.id.myOwnEventyourAppointmentText);
        myOwnEventDetails_constraintLayout = view.findViewById(R.id.myOwnEventDetails_constraintLayout);
        setSelectedEvent(EventController.getEventById(getEventID()));
        buildDescriptionEvent(EventController.getEventById(getEventID()));


        myOwnEventDetailsButtonShowAcceptances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParticipantsListFragment participantsFragment = new ParticipantsListFragment();
                Bundle bundleparticipants = new Bundle();
                bundleparticipants.putLong("EventId", getEventID());
                participantsFragment.setArguments(bundleparticipants);

                Bundle whichFragment = getArguments();

                if (whichFragment.get("fragment").equals("EventOverview")) {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.eventOverview_frameLayout, participantsFragment, "MyOwnEventDetails");
                    fragmentTransaction.addToBackStack("MyOwnEventDetails");
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.calendar_frameLayout, participantsFragment, "MyOwnEventDetails");
                    fragmentTransaction.addToBackStack("MyOwnEventDetails");
                    fragmentTransaction.commit();
                }
            }
        });

        // Open the QRGeneratorFragment to Show the QRCode form this Event.
        myOwnEventDetailsButtonShowQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle whichFragment = getArguments();
                QRGeneratorFragment qrFrag = new QRGeneratorFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("eventId", getEventID());
                bundle.putString("fragment", whichFragment.getString("fragment"));
                qrFrag.setArguments(bundle);

                if (whichFragment.getString("fragment").equals("EventOverview")) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.eventOverview_frameLayout, qrFrag, "QrGeneratorEO")
                            .addToBackStack("QrGeneratorEO")
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.calendar_frameLayout, qrFrag, "QrGeneratorCA")
                            .addToBackStack("QrGeneratorCA")
                            .commit();
                }
            }
        });

        return view;
    }


    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    private void buildDescriptionEvent(Event selectedEvent) {
        //Put StringBuffer in an Array and split the Values to new String Variables
        //Index: 0 = CreatorID; 1 = StartDate; 2=date of event (for serial events) 3 = EndDate; 4 = StartTime; 5 = EndTime;
        //       6 = Repetition; 7 = ShortTitle; 8 = Place; 9 = Description;  10 = EventCreatorName
        String[] eventStringBufferArray = String.valueOf(stringBufferGenerator()).split("\\|");
        String startDate = eventStringBufferArray[1].trim();
        String currentDate = eventStringBufferArray[2].trim();
        String endDate = eventStringBufferArray[3].trim();
        String startTime = eventStringBufferArray[4].trim();
        String endTime = eventStringBufferArray[5].trim();
        String repetition = eventStringBufferArray[6].toUpperCase().trim();
        String shortTitle = eventStringBufferArray[7].trim();
        String place = eventStringBufferArray[8].trim();
        String description = eventStringBufferArray[9].trim();
        String eventCreatorName = eventStringBufferArray[10].trim();

        // Change the DataBase Repetition Information in a German String for the Repetition Element
        // like "Daily" into "täglich" and so on
        switch (repetition) {
            case "YEARLY":
                repetition = getString(R.string.yearly);
                break;
            case "MONTHLY":
                repetition = getString(R.string.monthly);
                break;
            case "WEEKLY":
                repetition = getString(R.string.weekly);
                break;
            case "DAILY":
                repetition = getString(R.string.daily);
                break;
            case "NONE":
                repetition = "";
                break;
            default:
                repetition = getString(R.string.without_repetition);
        }

        // Check the EventCreatorName and is it itself Change the eventCreaterName to "Your Self"
        if (eventCreatorName.equals(PersonController.getPersonWhoIam().getName())) {
            eventCreatorName = getString(R.string.yourself);
        }
        // Event shortTitel in Headline with StartDate
        myOwnEventYourAppointment.setText("Dein Termin" + "\n" + shortTitle);
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            myOwnEventeventDescription.setText(getString(R.string.event_date) + currentDate +"\n"+ getString(R.string.time)+ startTime + getString(R.string.until)
                    + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + "\n" + getString(R.string.eventDetails)
                    + description);
        } else {
            myOwnEventeventDescription.setText(getString(R.string.as_of) + startDate
                    + getString(R.string.until) + endDate + "\n"+ getString(R.string.time) + startTime + getString(R.string.until)
                    + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + "\n" + getString(R.string.eventDetails)
                    + description);
        }
    }

    public StringBuffer stringBufferGenerator() {

        //Modify the Dateformat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | "; //

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // CreatorID (not EventID!!), StartDate, currentDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
        // Place, Description and Name of EventCreator
        eventToStringBuffer = new StringBuffer();
        eventToStringBuffer.append(selectedEvent.getId() + stringSplitSymbol);
        if (selectedEvent.getStartEvent() == null)
            eventToStringBuffer.append(simpleDateFormat.format(selectedEvent.getStartTime()) + stringSplitSymbol);
        else
            eventToStringBuffer.append(simpleDateFormat.format(selectedEvent.getStartEvent().getStartTime()) + stringSplitSymbol);
        eventToStringBuffer.append(simpleDateFormat.format(selectedEvent.getStartTime()) + stringSplitSymbol);
        eventToStringBuffer.append(simpleDateFormat.format(selectedEvent.getEndDate()) + stringSplitSymbol);
        eventToStringBuffer.append(simpleTimeFormat.format(selectedEvent.getStartTime()) + stringSplitSymbol);
        eventToStringBuffer.append(simpleTimeFormat.format(selectedEvent.getEndTime()) + stringSplitSymbol);
        eventToStringBuffer.append(selectedEvent.getRepetition() + stringSplitSymbol);
        eventToStringBuffer.append(selectedEvent.getShortTitle() + stringSplitSymbol);
        eventToStringBuffer.append(selectedEvent.getPlace() + stringSplitSymbol);
        eventToStringBuffer.append(selectedEvent.getDescription() + stringSplitSymbol);
        eventToStringBuffer.append(selectedEvent.getCreator().getName());

        return eventToStringBuffer;

    }
}


