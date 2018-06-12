package hft.wiinf.de.horario.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import java.text.SimpleDateFormat;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.SendSmsController;
import hft.wiinf.de.horario.model.Event;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EventRejectEventFragment extends Fragment {

    private static final String TAG = "EventRejectEvent";
    EditText reason_for_rejection;
    TextView reject_event_header, reject_event_description;
    Spinner spinner_reason;
    Button button_reject_event, button_dialog_delete, button_dialog_back;

    Event selectedEvent;
    Event event;
    StringBuffer eventToStringBuffer;

    String phNumber, rejectMessage, shortTitle;
    Long creatorEventId;

    public EventRejectEventFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_reject_event, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize GUI-Elements
        reason_for_rejection = view.findViewById(R.id.reject_event_editText_note);
        reject_event_description = view.findViewById(R.id.reject_event_textView_description);
        reject_event_header = view.findViewById(R.id.reject_event_textView_header);
        spinner_reason = view.findViewById(R.id.reject_event_spinner_reason);
        button_reject_event = view.findViewById(R.id.reject_event_button_reject);
        button_dialog_delete = view.findViewById(R.id.dialog_button_event_delete);
        button_dialog_back = view.findViewById(R.id.dialog_button_event_back);
        setSelectedEvent(EventController.getEventById(getEventID()));
        buildDescriptionEvent(EventController.getEventById(getEventID()));

        //initialize adapter
        ArrayAdapter reasonAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reason_for_rejection, android.R.layout.simple_spinner_item);
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason.setAdapter(reasonAdapter);

        //Make EditText-Field editable
        reason_for_rejection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                reason_for_rejection.setFocusable(true);
                reason_for_rejection.setFocusableInTouchMode(true);
                return false;
            }
        });

        reason_for_rejection.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //on click: close the keyboard after input is done
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                reason_for_rejection.setFocusable(false);
                reason_for_rejection.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;

            }
        });

        button_reject_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForInput()) {
                    askForPermissionToDelete();
                }
            }
        });
    }

    public void askForPermissionToDelete() {
        //Build dialog
        final AlertDialog.Builder dialogAskForFinalDecission = new AlertDialog.Builder(getContext());
        dialogAskForFinalDecission.setView(R.layout.dialog_afterrejectevent);
        dialogAskForFinalDecission.setTitle(R.string.titleDialogRejectEvent);
        dialogAskForFinalDecission.setCancelable(true);

        final AlertDialog alertDialogAskForFinalDecission = dialogAskForFinalDecission.create();
        alertDialogAskForFinalDecission.show();

        alertDialogAskForFinalDecission.findViewById(R.id.dialog_button_event_delete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event = EventController.getEventById((getEventID()));

                        //If an Event of a recurring event is cancelled, all events
                        // of the recurring event are deleted. This way the user can Scan the
                        // Event again and confirm it again.
                        new Delete().from(Event.class).where("CreatorEventId=?",
                                String.valueOf(event.getCreatorEventId())).execute();

                        //delete alarm for notification
                        NotificationController.deleteAlarmNotification(getContext(), event);

                        //SMS
                        rejectMessage = spinner_reason.getSelectedItem().toString() + "!" + reason_for_rejection.getText().toString();
                        creatorEventId = event.getCreatorEventId();
                        Log.i("Absagegrund", rejectMessage);
                        SendSmsController.sendSMS(getContext(), phNumber, rejectMessage, false, creatorEventId, shortTitle);

                        Toast.makeText(getContext(), R.string.reject_event_hint, Toast.LENGTH_SHORT).show();
                        //restart Activity
                        Intent intent = new Intent(getActivity(), TabActivity.class);
                        startActivity(intent);
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

    public Long getEventID() {
        Bundle MYEventIdBundle = getArguments();
        Long MYEventIdLongResult = MYEventIdBundle.getLong("EventId");
        return MYEventIdLongResult;
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
        shortTitle = eventStringBufferArray[6].trim();
        String place = eventStringBufferArray[7].trim();
        String description = eventStringBufferArray[8].trim();
        String eventCreatorName = eventStringBufferArray[9].trim();
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
        reject_event_header.setText(eventCreatorName + "\n" + shortTitle + ", " + startDate);
        // Check for a Repetition Event and Change the Description Output with and without
        // Repetition Element inside.
        if (repetition.equals("")) {
            reject_event_description.setText("Am " + startDate + " findet von " + startTime + " bis "
                    + endTime + " Uhr in Raum " + place + " " + shortTitle + " statt." + "\n" + "Termindetails sind: "
                    + description + "\n" + "\n" + "Organisator: " + eventCreatorName);
        } else {
            reject_event_description.setText("Vom " + startDate + " bis " + endDate +
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

    //check for userinput
    private boolean checkForInput() {
        if (reason_for_rejection.getText().length() == 0 || spinner_reason.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), R.string.reject_event_reason, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reason_for_rejection.getText().toString().contains("|")) {
            Toast.makeText(getContext(), R.string.reject_event_reason_contains_pipe, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reason_for_rejection.getText().toString().matches(" +.*")) {
            Toast.makeText(getContext(), R.string.reject_event_reason_free_text_field_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reason_for_rejection.getText().length() > 50) {
            Toast.makeText(getContext(), R.string.reject_event_reason_free_text_field_to_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        //check if "," and "!" is not part of user input
        //if they are: replace them with empty string " "
        if (reason_for_rejection.getText().toString().contains(",") ||
                reason_for_rejection.getText().toString().contains("!") || reason_for_rejection.getText().toString().contains(":")) {
            reason_for_rejection.getText().toString().replaceAll(",", " ");
            reason_for_rejection.getText().toString().replaceAll("!", " ");
            reason_for_rejection.getText().toString().replaceAll(":", " ");
            return true;
        }
        return true;
    }
}
