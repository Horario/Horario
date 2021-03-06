package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.Event;

public class AcceptedEventDetailsFragment extends Fragment {

    Button acceptedEventDetailsButtonShowQR, acceptedEventDetailsButtonRefuseAppointment, acceptedEventDetailButtonDelete;
    TextView acceptedEventDetailsOrganisatorText, acceptedEventphNumberText, acceptedEventeventDescription;
    RelativeLayout rLayout_acceptedEvent_helper;
    Event selectedEvent, event;
    StringBuffer eventToStringBuffer;

    public AcceptedEventDetailsFragment() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
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
        View view = inflater.inflate(R.layout.fragment_accepted_event_details, container, false);

        acceptedEventDetailsButtonRefuseAppointment = view.findViewById(R.id.acceptedEventDetailsButtonRefuseAppointment);
        acceptedEventDetailsButtonShowQR = view.findViewById(R.id.acceptedEventDetailsButtonShowQR);
        rLayout_acceptedEvent_helper = view.findViewById(R.id.acceptedEvent_relativeLayout_helper);
        acceptedEventDetailsOrganisatorText = view.findViewById(R.id.acceptedEventDetailsOrganisatorText);
        acceptedEventphNumberText = view.findViewById(R.id.acceptedEventphNumberText);
        acceptedEventeventDescription = view.findViewById(R.id.acceptedEventeventDescription);
        setSelectedEvent(EventController.getEventById(getEventID()));
        buildDescriptionEvent(EventController.getEventById(getEventID()));

        acceptedEventDetailsButtonRefuseAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for cancelling an event eg. take it out of the DB and Calendar View
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                if (EventController.getEventById(getEventID()).getEndTime().after(cal.getTime())) {
                    Bundle whichFragment = getArguments();
                    EventRejectEventFragment eventRejectEventFragment = new EventRejectEventFragment();
                    Bundle bundleAcceptedEventId = new Bundle();
                    bundleAcceptedEventId.putLong("EventId", getEventID());
                    bundleAcceptedEventId.putString("fragment", "AcceptedEventDetails");
                    eventRejectEventFragment.setArguments(bundleAcceptedEventId);
                    if (whichFragment.getString("fragment").equals("EventOverview")) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.eventOverview_frameLayout, eventRejectEventFragment, "RejectEvent")
                                .addToBackStack("RejectEvent")
                                .commit();
                    } else {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.calendar_frameLayout, eventRejectEventFragment, "RejectEvent")
                                .addToBackStack("RejectEvent")
                                .commit();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.startTime_afterScanning_past, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Open the QRGeneratorFragment to Show the QRCode form this Event.
        acceptedEventDetailsButtonShowQR.setOnClickListener(new View.OnClickListener() {
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
        String phNumber = selectedEvent.getCreator().getPhoneNumber();

        // Change the DataBase Repetition Information in a German String for the Repetition Element
        // like "Daily" into "täglich" and so on
        switch (repetition) {
            case "YEARLY":
                repetition = "jährlich";
                break;
            case "MONTHLY":
                repetition = "monatlich";
                break;
            case "WEEKLY":
                repetition = "wöchentlich";
                break;
            case "DAILY":
                repetition = "täglich";
                break;
            case "NONE":
                repetition = "";
                break;
            default:
                repetition = "ohne Wiederholung";
        }

        // Event shortTitel in Headline with StartDate
        acceptedEventDetailsOrganisatorText.setText(eventCreatorName + " (" + phNumber + ")" + "\n" + shortTitle);
        acceptedEventphNumberText.setText("");
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            acceptedEventeventDescription.setText(getString(R.string.event_date) + currentDate + "\n" + getString(R.string.time) + startTime + getString(R.string.until)
                    + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + "\n" + getString(R.string.eventDetails)
                    + description);
        } else {
            acceptedEventeventDescription.setText(getString(R.string.as_of) + startDate
                    + getString(R.string.until) + endDate + "\n" + getString(R.string.time) + startTime + getString(R.string.until)
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