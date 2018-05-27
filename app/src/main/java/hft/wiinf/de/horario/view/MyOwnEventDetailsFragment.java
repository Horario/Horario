package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.Event;

public class MyOwnEventDetailsFragment extends Fragment {


    Button myOwnEventDetailsButtonShowQR, myOwnEventDetailsButtonShowAcceptances;
    RelativeLayout rLayout_myOwnEvent_helper;
    ConstraintLayout myOwnEventDetails_constraintLayout;
    TextView myOwnEventeventDescription, myOwnEventYourAppointment;
    Event selectedEvent;
    StringBuffer eventToStringBuffer;

    public MyOwnEventDetailsFragment() {
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
                FragmentTransaction fr1 = getFragmentManager().beginTransaction();
                fr1.replace(R.id.myOwnEvent_relativeLayout_helper,participantsFragment);
                fr1.addToBackStack(null);
                fr1.commit();
                rLayout_myOwnEvent_helper.setVisibility(View.VISIBLE);
                myOwnEventDetails_constraintLayout.setVisibility(View.GONE);
            }
        });
        myOwnEventDetailsButtonShowQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO in another US uncomment and adapt with Class-Own variables

//                //Create a Bundle to Send the Information to an other Fragment
//                //The Bundle input is the StringBuffer with the EventInformation
//                QRSharingActivity qrSharingBundle = new QRSharingActivity();
//                Bundle bundle = new Bundle();
//                bundle.putString("qrStringBufferDescription", String.valueOf(mQRGenerator_StringBuffer_Result));
//                qrSharingBundle.setArguments(bundle);
//
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, qrSharingBundle);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                mQRGenerator_textView_headline.setVisibility(View.GONE);
//                mQRGenerator_textView_description.setVisibility(View.GONE);
//                mQRGenerator_button_start_sharingFragment.setVisibility(View.GONE);
//                mQRGenerator_button_start_eventFeedbackFragment.setVisibility(View.GONE);
//                mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
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

        // Check the EventCreatorName and is it itself Change the eventCreaterName to "Your Self"
        if (eventCreatorName.equals(selectedEvent.getCreator().getName())) {
            eventCreatorName = "Du";
        }
        // Event shortTitel in Headline with StartDate
        myOwnEventYourAppointment.setText("Dein Termin" + "\n" + shortTitle + ", " + startDate);
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            myOwnEventeventDescription.setText("Am " + startDate + " findet von " + startTime + " bis "
                    + endTime + " Uhr in Raum " + place + " " + shortTitle + " statt." + "\n" + "Termindetails sind: "
                    + description + "\n" + "\n" + "Organisator: " + eventCreatorName);
        } else {
            myOwnEventeventDescription.setText("Vom " + startDate + " bis " + endDate +
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
        eventToStringBuffer.append(selectedEvent.getCreatorEventId() + stringSplitSymbol);
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
