package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;

import java.nio.channels.NonReadableChannelException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;

public class QRScanResultFragment extends Fragment {
    private static final String TAG = "QRScanResultFragment";
    private RelativeLayout mScannerResult_RelativeLayout_Main, mScannerResult_RelativeLayout_ButtonFrame,
            mScannerResult_RelativeLayout_goTo_CalendarFragment, mScannerResult_RelativeLayout_Calendar;
    private TextView mScannerResult_TextureView_Headline, mScannerResult_TextureView_Description;
    private Button mScannerResult_Button_addEvent, mScannerResult_Button_saveWithoutassent,
            mScannerResult_Button_rejectEvent;
    Person person;
    //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
    //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
    private String creatorID, startDate, endDate, startTime, endTime, repetition, shortTitle, place, description, eventCreatorName, creatorPhoneNumber;
    private String hourOfDay, minutesOfDay, year, month, day;

    Calendar myStartTime = Calendar.getInstance();
    Calendar myEndTime = Calendar.getInstance();
    Calendar myEndDate = Calendar.getInstance();

    public QRScanResultFragment() {
        // Required empty public constructor
    }

    public String qrScanResultBundle() {
        Bundle qrScanBundle = getArguments();
        String qrScanBundleResult = qrScanBundle.getString("scanResult");
        return qrScanBundleResult;
    }


    @SuppressLint("ResourceType")
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        myStartTime.set(Calendar.SECOND, 0);
        myStartTime.set(Calendar.MILLISECOND, 0);
        myEndTime.set(Calendar.SECOND, 0);
        myEndTime.set(Calendar.MILLISECOND, 0);
        myEndDate.set(Calendar.SECOND, 0);
        myEndDate.set(Calendar.MILLISECOND, 0);
        //GUI initial
        mScannerResult_RelativeLayout_Main = view.findViewById(R.id.scanner_result_relativeLayout_main);
        mScannerResult_RelativeLayout_ButtonFrame = view.findViewById(R.id.scanner_result_relativeLayout_buttonFrame);
        mScannerResult_RelativeLayout_goTo_CalendarFragment = view.findViewById(R.id.scanner_result_realtiveLayout_CalendarFragment);
        mScannerResult_RelativeLayout_Calendar = view.findViewById(R.id.calendar_constrainLayout_main);
        mScannerResult_TextureView_Description = view.findViewById(R.id.scanner_result_textview_eventText);
        mScannerResult_TextureView_Headline = view.findViewById(R.id.scanner_result_textView_headline);
        mScannerResult_Button_saveWithoutassent = view.findViewById(R.id.scanner_result_button_save_without_assent);
        mScannerResult_Button_rejectEvent = view.findViewById(R.id.scanner_result_button_reject_event);
        mScannerResult_Button_addEvent = view.findViewById(R.id.scanner_result_button_addEvent);

        //Make the Element at first unvisible
        mScannerResult_TextureView_Description.setVisibility(View.GONE);
        mScannerResult_TextureView_Headline.setVisibility(View.GONE);
        mScannerResult_Button_addEvent.setVisibility(View.GONE);
        mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
        mScannerResult_Button_rejectEvent.setVisibility(View.GONE);

        displayQRResult();

        //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
        //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName
        mScannerResult_Button_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person = PersonController.getPersonWhoIam();

                Person person = new Person();
                person.setName(eventCreatorName);
                person.setPhoneNumber(creatorPhoneNumber);
                person.save();

                Event event = new Event(person);
                event.setAccepted(AcceptedState.ACCEPTED);
                event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                event.setStartTime(getStartTime().getTime());
                event.setEndTime(getEndTime().getTime());
                event.setRepetition(getRepetition());
                event.setShortTitle(shortTitle);
                event.setPlace(place);
                event.setDescription(description);

                if (event.getRepetition() != Repetition.NONE) {
                    event.setEndDate(getEndDate().getTime());
                    EventController.saveSerialevent(event);
                } else {
                    EventController.saveEvent(event);
                }

                Toast toast = Toast.makeText(view.getContext(), R.string.acceptEvent, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        mScannerResult_Button_rejectEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person = PersonController.getPersonWhoIam();

                Person person = new Person();
                person.setName(eventCreatorName);
                person.setPhoneNumber(creatorPhoneNumber);
                person.save();

                Event event = new Event(person);
                event.setAccepted(AcceptedState.REJECTED);
                event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                event.setStartTime(getStartTime().getTime());
                event.setEndTime(getEndTime().getTime());
                event.setRepetition(getRepetition());
                event.setShortTitle(shortTitle);
                event.setPlace(place);
                event.setDescription(description);

                if (event.getRepetition() != Repetition.NONE) {
                    event.setEndDate(getEndDate().getTime());
                    EventController.saveSerialevent(event);
                } else {
                    EventController.saveEvent(event);
                }

                Toast toast = Toast.makeText(view.getContext(), R.string.rejectEvent, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        mScannerResult_Button_saveWithoutassent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person = PersonController.getPersonWhoIam();

                Person person = new Person();
                person.setName(eventCreatorName);
                person.setPhoneNumber(creatorPhoneNumber);
                person.save();

                Event event = new Event(person);
                event.setAccepted(AcceptedState.WAITING);
                event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                event.setStartTime(getStartTime().getTime());
                event.setEndTime(getEndTime().getTime());
                event.setRepetition(getRepetition());
                event.setShortTitle(shortTitle);
                event.setPlace(place);
                event.setDescription(description);

                if (event.getRepetition() != Repetition.NONE) {
                    event.setEndDate(getEndDate().getTime());
                    EventController.saveSerialevent(event);
                } else {
                    EventController.saveEvent(event);
                }

                Toast toast = Toast.makeText(view.getContext(), R.string.saveEvent, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrscan_result, container, false);
    }


    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void displayQRResult() {
        mScannerResult_TextureView_Description.setVisibility(View.VISIBLE);

        try {
            //If the Scan wasn't Canceled the GUI elements set to Visible
            mScannerResult_TextureView_Headline.setVisibility(View.VISIBLE);
            mScannerResult_Button_addEvent.setVisibility(View.VISIBLE);
            mScannerResult_Button_saveWithoutassent.setVisibility(View.VISIBLE);
            mScannerResult_Button_rejectEvent.setVisibility(View.VISIBLE);

            //Put StringBufffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName

            //split qrCodeExample String at |
            String[] eventStringBufferArray = qrScanResultBundle().split("\\|");
            creatorID = eventStringBufferArray[0].trim();
            startDate = eventStringBufferArray[1].trim();
            endDate = eventStringBufferArray[2].trim();
            startTime = eventStringBufferArray[3].trim();
            endTime = eventStringBufferArray[4].trim();
            repetition = eventStringBufferArray[5].toUpperCase().trim();
            shortTitle = eventStringBufferArray[6].trim();
            place = eventStringBufferArray[7].trim();
            description = eventStringBufferArray[8].trim();
            eventCreatorName = eventStringBufferArray[9].trim();
            //creatorPhoneNumber = eventStringBufferArray[10].trim();

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

            // Event shortTitle in Headline with StartDate
            mScannerResult_TextureView_Headline.setText(shortTitle);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                mScannerResult_TextureView_Description.setText(startDate + "\n" + place +
                        "\n" + eventCreatorName);
            } else {
                mScannerResult_TextureView_Description.setText(startDate + "-" + endDate
                        + "\n" + repetition + "\n" + startTime + " Uhr - "
                        + endTime + " Uhr \n" + "Raum " + place + "\n" + "Organisator: " + eventCreatorName);
            }
            // In the CatchBlock the User see a Snackbar Information and was pushed to CalendarActivity
        } catch (NullPointerException e) {
            Log.d(TAG, "QRScanResultFragment" + e.getMessage());
            mScannerResult_Button_addEvent.setVisibility(View.GONE);
            mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
            mScannerResult_Button_rejectEvent.setVisibility(View.GONE);
            mScannerResult_TextureView_Headline.setVisibility(View.GONE);

            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                    "Ups! Fehler Aufgetreten!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.scanner_result_realtiveLayout_CalendarFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mScannerResult_RelativeLayout_ButtonFrame.setVisibility(View.GONE);
                    mScannerResult_RelativeLayout_Main.setVisibility(View.GONE);
                    mScannerResult_RelativeLayout_goTo_CalendarFragment.setVisibility(View.VISIBLE);
                }
            }).show();

        } catch (ArrayIndexOutOfBoundsException z) {
            Log.d(TAG, "QRScanResultFragment" + z.getMessage());
            mScannerResult_Button_addEvent.setVisibility(View.GONE);
            mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
            mScannerResult_Button_rejectEvent.setVisibility(View.GONE);
            mScannerResult_TextureView_Headline.setVisibility(View.GONE);
            mScannerResult_TextureView_Description.setText("Das ist der Inhalt vom QR Code: " + "\n" +
                    "\n" + "Das können wir leider nicht als Termin speichern!");

            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                    "Ups! Falscher QR-Code!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.scanner_result_realtiveLayout_CalendarFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mScannerResult_RelativeLayout_ButtonFrame.setVisibility(View.GONE);
                    mScannerResult_RelativeLayout_Main.setVisibility(View.GONE);
                    mScannerResult_RelativeLayout_goTo_CalendarFragment.setVisibility(View.VISIBLE);
                }
            }).show();
        }
    }

    private Repetition getRepetition() {
        switch (repetition) {
            case "jährlich":
                return Repetition.YEARLY;
            case "monatlich":
                return Repetition.MONTHLY;
            case "wöchentlich":
                return Repetition.WEEKLY;
            case "täglich":
                return Repetition.DAILY;
            default:
                return Repetition.NONE;
        }
    }
    private Calendar getStartTime(){
        String[] startDateStringBufferArray = startDate.split("\\.");
        day = startDateStringBufferArray[0].trim();
        month = startDateStringBufferArray[1].trim();
        year = startDateStringBufferArray[2].trim();

        String[] startTimeStringBufferArray = startTime.split(":");
        hourOfDay = startTimeStringBufferArray[0].trim();
        minutesOfDay = startTimeStringBufferArray[1].trim();

        myStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myStartTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myStartTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        return myStartTime;
    }

    private Calendar getEndTime(){
        String[] endTimeStringBufferArray = endTime.split(":");
        hourOfDay = endTimeStringBufferArray[0].trim();
        minutesOfDay = endTimeStringBufferArray[1].trim();

        myEndTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myEndTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));

        return myEndTime;
    }
    private Calendar getEndDate(){
        String[] endDateStringBufferArray = endDate.split("\\.");
        day = endDateStringBufferArray[0].trim();
        month = endDateStringBufferArray[1].trim();
        year = endDateStringBufferArray[2].trim();
        myEndTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        return myEndDate;
    }
}

