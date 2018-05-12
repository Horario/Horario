package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.jar.Attributes;

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
    @SuppressLint("LongLogTag")
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

        // Show always Scrollbar on Description TextView
        mQRGenerator_textView_description.setMovementMethod(new ScrollingMovementMethod());

        //Create Event form the DB with the EventId (eventIdResultBundle) to put it in a StringBuffer
        mEvent = EventController.getEventById(eventIdDescription());

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
        // CreatorID (not EventID!!), StartDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
        // Place, Description and Name of EventCreator
        mQRGenerator_StringBuffer_Result = new StringBuffer();
        mQRGenerator_StringBuffer_Result.append(mEvent.getCreatorEventId() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getStartTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getEndDate()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getStartTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getEndTime()) + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getRepetition()+stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getShortTitle()+stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getPlace() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getDescription() + stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getName());

        return mQRGenerator_StringBuffer_Result;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        try {
            stringBufferGenerator();

            //Put StringBufffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
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
            if(eventCreatorName.equals(mPerson.getName())) {
               eventCreatorName = "Du selber";
            }

            // Event shortTitel in Headline with StartDate
            mQRGenerator_textView_headline.setText("Dein Termin"+"\n"+shortTitle+", "+startDate);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                mQRGenerator_textView_description.setText("Am "+startDate+ " findet von "+startTime+" bis "
                        +endTime+" Uhr in Raum " +place+" "+shortTitle+" statt."+"\n"+"Termindetails sind: "
                        +description+"\n"+"\n"+"Organisator: "+ eventCreatorName);
            } else {
                mQRGenerator_textView_description.setText(  "Vom "+startDate+ " bis "+endDate+
                        " findet "+repetition+" um "+startTime+"Uhr bis "+endTime+"Uhr in Raum "
                        +place+" "+shortTitle+" statt." +"\n"+"Termindetails sind: "+description+
                        "\n"+"\n"+"Organisator: "+ eventCreatorName);
            }
            // In the CatchBlock the User see a Snackbar Information and was pushed to CalendarActivity
        } catch (NullPointerException e) {
            Log.d(TAG, "QRGeneratorFragmentActivity:" + e.getMessage());
            mQRGenerator_button_start_eventFeedbackFragment.setVisibility(View.GONE);
            mQRGenerator_button_start_sharingFragment.setVisibility(View.GONE);

            Snackbar.make(getActivity().findViewById(R.id.generator_button_frame),
                    "Ups! Fehler Aufgetreten!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mQRGenerator_relativeLayout_buttonFrame.setVisibility(View.GONE);
                    mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
                }
            }).show();

        } catch (ArrayIndexOutOfBoundsException z){
           //If there an Exeption the Views are Invisible and Snackbar tell that's anything wrong
           // and Push him back to the CalendarActivity
            Log.d(TAG, "QRGeneratorFragmentActivity:" + z.getMessage());
            mQRGenerator_textView_headline.setVisibility(View.GONE);
            mQRGenerator_textView_description.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_buttonFrame.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
            mQRGenerator_textView_description.setText("Das ist der Inhalt vom QR Code: "+"\n"+mQRGenerator_StringBuffer_Result+
                    "\n"+"Das können wir leider nicht als Termin speichern!");

            Snackbar.make(getActivity().findViewById(R.id.generator_button_frame),
                    "Ups! Falscher QR-Code!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, new CalendarActivity());
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
}
