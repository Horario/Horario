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

import java.text.SimpleDateFormat;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.Event;

public class SavedEventDetailsFragment extends Fragment {

    Button savedEventDetailsButtonRefuseAppointment, savedEventDetailsButtonAcceptAppointment, savedEventDetailsButtonShowQr;
    RelativeLayout rLayout_savedEvent_helper;
    TextView savedEventDetailsOrganisatorText, savedEventphNumberText, savedEventeventDescription;
    Event selectedEvent;
    StringBuffer eventToStringBuffer;

    public SavedEventDetailsFragment() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
    @SuppressLint("LongLogTag")
    public Long getEventID() {
        Bundle MYEventIdBundle = getArguments();
        Long MYEventIdLongResult = MYEventIdBundle.getLong("EventId");
        return MYEventIdLongResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_event_details, container, false);
        savedEventDetailsButtonRefuseAppointment = view.findViewById(R.id.savedEventDetailsButtonRefuseAppointment);
        savedEventDetailsButtonAcceptAppointment = view.findViewById(R.id.savedEventDetailsButtonAcceptAppointment);
        savedEventDetailsButtonShowQr = view.findViewById(R.id.savedEventDetailsButtonShowQr);
        rLayout_savedEvent_helper = view.findViewById(R.id.savedEvent_relativeLayout_helper);
        savedEventDetailsOrganisatorText = view.findViewById(R.id.savedEventDetailsOrganisatorText);
        savedEventphNumberText = view.findViewById(R.id.savedEventphNumberText);
        savedEventeventDescription = view.findViewById(R.id.savedEventeventDescription);
        setSelectedEvent(EventController.getEventById(getEventID()));
        buildDescriptionEvent(EventController.getEventById(getEventID()));

        savedEventDetailsButtonRefuseAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for cancelling an event eg. take it out of the DB and Calendar View
            }
        });

        savedEventDetailsButtonAcceptAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for accepting an event eg. update the DB and Calendar View
            }
        });
        savedEventDetailsButtonShowQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code for showing the QR Code
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
        //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
        //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName
        String[] eventStringBufferArray = String.valueOf(stringBufferGenerator()).split("\\|");
        String startDate = eventStringBufferArray[1].trim();
        String endDate = eventStringBufferArray[2].trim();
        String startTime = eventStringBufferArray[3].trim();
        String endTime = eventStringBufferArray[4].trim();
        String repetition = eventStringBufferArray[5].toUpperCase().trim();
        String shortTitle = eventStringBufferArray[6].trim();
        String place = eventStringBufferArray[7].trim();
        String description = eventStringBufferArray[8].trim();
        String eventCreatorName = eventStringBufferArray[9].trim();
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
        savedEventDetailsOrganisatorText.setText(eventCreatorName + "\n" + shortTitle + ", " + startDate);
        savedEventphNumberText.setText(phNumber);
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            savedEventeventDescription.setText("Am " + startDate + " findet von " + startTime + " bis "
                    + endTime + " Uhr in Raum " + place + " " + shortTitle + " statt." + "\n" + "Termindetails sind: "
                    + description + "\n" + "\n" + "Organisator: " + eventCreatorName);
        } else {
            savedEventeventDescription.setText("Vom " + startDate + " bis " + endDate +
                    " findet " + repetition + " um " + startTime + "Uhr bis " + endTime + "Uhr in Raum "
                    + place + " " + shortTitle + " statt." + "\n" + "Termindetails sind: " + description +
                    "\n" + "\n" + "Organisator: " + eventCreatorName);
        }
    }

    public StringBuffer stringBufferGenerator() {

        //Modify the Dateformat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | "; //

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // CreatorID (not EventID!!), StartDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
        // Place, Description and Name of EventCreator
        eventToStringBuffer = new StringBuffer();
        eventToStringBuffer.append(selectedEvent.getId() + stringSplitSymbol);
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