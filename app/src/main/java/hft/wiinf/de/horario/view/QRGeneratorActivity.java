package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private TextView mQRGenerator_textView_description, mQRGenerator_textView_headline;
    private RelativeLayout mQRGenerator_relativeLayout_show_newFragment, mQRGenerator_relativeLayout_buttonFrame;
    private Button mQRGenerator_button_start_sharingFragment, mQRGenerator_button_start_eventFeedbackFragment;
    private Person mPerson;
    private StringBuffer mQRGenerator_StringBuffer_headline, mQRGenerator_StringBuffer_Result;
    private Event mEvent;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
    public Long eventIdDescription() {
        Bundle qrEventIdBundle = getArguments();
        Long qrEventIdLongResult = qrEventIdBundle.getLong("eventId");
        return qrEventIdLongResult;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);

        //Initial GUI
        mQRGenerator_button_start_sharingFragment = view.findViewById(R.id.generator_button_start_qrShareingFragment);
        mQRGenerator_button_start_eventFeedbackFragment = view.findViewById(R.id.generator_button_show_eventFeedbackFragment);
        mQRGenerator_textView_description = view.findViewById(R.id.generator_textView_description);
        mQRGenerator_textView_headline = view.findViewById(R.id.generator_textView_Headline);
        mQRGenerator_relativeLayout_show_newFragment = view.findViewById(R.id.generator_realtivLayout_show_qrSharingFragment);
        mQRGenerator_relativeLayout_buttonFrame = view.findViewById(R.id.generator_button_frame);

        //Create Event form the DB with the EventId (eventIdResultBundle) to put it in a StringBuffer
        mEvent = EventController.getEventById(eventIdDescription());
        /*
        //Erstellen von drei Dummydaten
        //ToDo Dummydaten löschen
        Date startDate = new Date();
        Date endDate = new Date();

        mEvent = new Event();
        mEvent.setDescription("Wir machen Experimente im Labor 3");
        mEvent.setPlace("Labor 3");
        mEvent.setStartTime(startDate);
        mEvent.setEndTime(endDate);
        EventController.saveEvent(mEvent);
        */

        mPerson = PersonController.getPersonWhoIam();

        return view;
    }

    public StringBuffer stringBufferGenerator() {
        //Modify the Dateformat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | "; //

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // Creator, StartDate, EndDate, StartTime, EndTime, Place, Description
        //Put StringBufffer in an Array and split the Values to new String Variables
        //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
        //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
        mQRGenerator_StringBuffer_Result = new StringBuffer();
        mQRGenerator_StringBuffer_Result.append(mEvent.getCreatorEventId() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getStartTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getEndTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getStartTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getEndTime()) + stringSplitSymbol);
       // mQRGenerator_StringBuffer_Result.append(mEvent.getRepetition + stringSplitSymbol);

        mQRGenerator_StringBuffer_Result.append(mEvent.getPlace() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getDescription() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getName());

        return mQRGenerator_StringBuffer_Result;

    }
/*
    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        try {
            //Put StringBufffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
            String[] eventStringBufferArray = qrResult.split("\\|");
            String startDate = eventStringBufferArray[1].trim();
            String endDate = eventStringBufferArray[2].trim();
            String startTime = eventStringBufferArray[3].trim();
            String endTime = eventStringBufferArray[4].trim();
            String repetition = eventStringBufferArray[5].toUpperCase().trim();
            String shortTitle = eventStringBufferArray[6].trim();
            String place = eventStringBufferArray[7].trim();
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
                    repetition = "täglisch";
                    break;
                case "NONE":
                    repetition = "";
                    break;
                default:
                    repetition = "ohne Wiederholung";
            }

            // Event shortTitel in Headline with StartDate
            mScannerResult_TextureView_Headline.setText(shortTitle);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                mScannerResult_TextureView_Description.setText(startDate + "\n" + place + "\n" + eventCreatorName);
            } else {
                mScannerResult_TextureView_Description.setText(startDate + "-" + endDate + "\n" + repetition + "\n" + startTime + " Uhr - "
                        + endTime + " Uhr \n" + "Raum " + place + "\n" + "Organisator: " + eventCreatorName);
            }
            // In the CatchBlock the User see a Snackbar Information and was pushed to CalendarActivity
        } catch (NullPointerException e) {
            Log.d(TAG, "QRSharingFragmentActivity:" + e.getMessage());
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

        } catch (ArrayIndexOutOfBoundsException z){
            Log.d(TAG, "QRSharingFragmentActivity:" + z.getMessage());
            mQRGenerator_textView_headline.setVisibility(View.GONE);
            mQRGenerator_textView_description.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_buttonFrame.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
            mScannerResult_TextureView_Description.setText("Das ist der Inhalt vom QR Code: "+"\n"+qrResult+
                    "\n"+"Das können wir leider nicht als Termin speichern!");

            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                    "Ups! Falscher QR-Code!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.scanner_result_realtiveLayout_CalendarFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mQRGenerator_textView_headline.setVisibility(View.GONE);
                    mQRGenerator_textView_description.setVisibility(View.GONE);
                    mQRGenerator_relativeLayout_buttonFrame.setVisibility(View.GONE);
                    mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
                }
            }).show();
        }

        //Create a QR Code and Show it in the ImageView.
        mQRGenerator_button_start_sharingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a Bundle to Send the Information to an other Fragment
                //The Bundle input is the StringBuffer with the EventInformation
                QRSharingActivity qrSharingBundle = new QRSharingActivity();
                Bundle bundle = new Bundle();
                bundle.putString("qrStringBufferDescription", String.valueOf(mQRGenerator_StringBuffer_Result));
                qrSharingBundle.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, qrSharingBundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mQRGenerator_textView_headline.setVisibility(View.GONE);
                mQRGenerator_textView_description.setVisibility(View.GONE);
                mQRGenerator_button_start_sharingFragment.setVisibility(View.GONE);
                mQRGenerator_button_start_eventFeedbackFragment.setVisibility(View.GONE);
                mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);

            }
        });
    }

*/


}
