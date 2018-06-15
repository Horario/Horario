package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.SendSmsController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;

public class SavedEventDetailsFragment extends Fragment {

    Button savedEventDetailsButtonRefuseAppointment, savedEventDetailsButtonAcceptAppointment, savedEventDetailsButtonShowQr;
    RelativeLayout rLayout_savedEvent_helper;
    TextView savedEventDetailsOrganisatorText, savedEventphNumberText, savedEventeventDescription;
    Event selectedEvent;
    StringBuffer eventToStringBuffer;

    Long creatorEventId;
    String shortTitle, phNumber;

    public SavedEventDetailsFragment() {
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
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                if (cal.getTime().before(EventController.getEventById(getEventID()).getEndTime())) {
                    //Code for cancelling an event eg. take it out of the DB and Calendar View
                    EventRejectEventFragment eventRejectEventFragment = new EventRejectEventFragment();
                    Bundle bundleAcceptedEventId = new Bundle();
                    bundleAcceptedEventId.putLong("EventId", getEventID());
                    bundleAcceptedEventId.putString("fragment", "AcceptedEventDetails");
                    eventRejectEventFragment.setArguments(bundleAcceptedEventId);
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.savedEvent_relativeLayout_main, eventRejectEventFragment, "RejectEvent");
                    fr.addToBackStack("RejectEvent");
                    fr.commit();
                } else {
                    Toast.makeText(getContext(), R.string.startTime_afterScanning_past, Toast.LENGTH_SHORT).show();
                }
            }
        });

        savedEventDetailsButtonAcceptAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                if (cal.getTime().before(EventController.getEventById(getEventID()).getEndTime())) {
                    askForPermissionToSave();
                } else {
                    Toast.makeText(getContext(), R.string.startTime_afterScanning_past, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Open the QRGeneratorFragment to Show the QRCode form this Event.
        savedEventDetailsButtonShowQr.setOnClickListener(new View.OnClickListener() {
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

    public void askForPermissionToSave() {
        final AlertDialog.Builder dialogAskForFinalDecission = new AlertDialog.Builder(getContext());
        dialogAskForFinalDecission.setView(R.layout.dialog_afterrejectevent);
        dialogAskForFinalDecission.setTitle(R.string.titleDialogSaveEvent);
        dialogAskForFinalDecission.setCancelable(true);

        final AlertDialog alertDialogAskForFinalDecission = dialogAskForFinalDecission.create();
        alertDialogAskForFinalDecission.show();

        alertDialogAskForFinalDecission.findViewById(R.id.dialog_button_event_delete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Pull the EventID change the AcceptedState and Save again.
                        Event event = EventController.getEventById(getEventID());

                        //SMS
                        creatorEventId = event.getCreatorEventId();

                        //Check the Event if its a SingleEvent it set Accepted State just for this Event
                        //and send a SMS
                        if (event.getRepetition().equals("NONE")) {
                            Toast.makeText(getContext(), R.string.accept_event_hint, Toast.LENGTH_SHORT).show();
                            event.setAccepted(AcceptedState.ACCEPTED);
                            EventController.saveEvent(event);
                            SendSmsController.sendSMS(getContext(), phNumber, null, true,
                                    creatorEventId, shortTitle);
                            NotificationController.setAlarmForNotification(getContext(), event);
                            Intent intent = new Intent(getActivity(), hft.wiinf.de.horario.TabActivity.class);
                            startActivity(intent);
                            // If have the Event a Repetition it set all Events to Accepted and send a SMS
                        } else {
                            Toast.makeText(getContext(), R.string.accept_event_hint, Toast.LENGTH_SHORT).show();
                            //Create a List with all Events with the same CreatorEventId an set the State
                            //to Accepted
                            List<Event> findMyEventsByEventCreatorId =
                                    //ToDo Creator EventId are not unique so its necessary to Enlarge the Select Statement
                                    new Select().from(Event.class).where("CreatorEventId=?",
                                            String.valueOf(event.getCreatorEventId())).execute();
                            for (Event x : findMyEventsByEventCreatorId) {
                                x.setAccepted(AcceptedState.ACCEPTED);
                                NotificationController.setAlarmForNotification(getContext(), x);
                                EventController.saveEvent(x);
                            }
                            SendSmsController.sendSMS(getContext(), phNumber, null, true,
                                    creatorEventId, shortTitle);

                            Intent intent = new Intent(getActivity(), hft.wiinf.de.horario.TabActivity.class);
                            startActivity(intent);
                        }
                    }
                });

        alertDialogAskForFinalDecission.findViewById(R.id.dialog_button_event_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogAskForFinalDecission.cancel();
                    }
                });
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
        shortTitle = eventStringBufferArray[7].trim();
        String place = eventStringBufferArray[8].trim();
        String description = eventStringBufferArray[9].trim();
        String eventCreatorName = eventStringBufferArray[10].trim();
        phNumber = selectedEvent.getCreator().getPhoneNumber();

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
        savedEventDetailsOrganisatorText.setText(eventCreatorName + " (" + phNumber+ ")" +"\n" + shortTitle + ", " + currentDate);
        savedEventphNumberText.setText("");
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            savedEventeventDescription.setText(getString(R.string.time)+ startTime + getString(R.string.until)
                    + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + "\n" + getString(R.string.eventDetails)
                    + description);
        } else {
            savedEventeventDescription.setText(getString(R.string.as_of) + startDate
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
        // CreatorID (not EventID!!), StartDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
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