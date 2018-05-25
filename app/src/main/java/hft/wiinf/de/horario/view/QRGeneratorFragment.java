package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import java.util.Objects;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class QRGeneratorFragment extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private TextView mQRGenerator_textView_description, mQRGenerator_textView_headline;
    private RelativeLayout mQRGenerator_relativeLayout_buttonFrame, mQRGenerator_relativeLayout_show_newFragment;
    private Button mQRGenerator_button_start_sharingFragment, mQRGenerator_button_start_eventFeedbackFragment;
    private Person mPerson;
    private StringBuffer mQRGenerator_StringBuffer_Result;
    private Event mEvent;

    public QRGeneratorFragment() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
    @SuppressLint("LongLogTag")
    public Long eventIdDescription() {
        Bundle qrEventIdBundle = getArguments();
        assert qrEventIdBundle != null;
        Long qrEventIdLongResult = qrEventIdBundle.getLong("eventId");
        return qrEventIdLongResult;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrgenerator, container, false);

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
        //Modify the DateFormat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | ";

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // CreatorID (not EventID!!), StartDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
        // Place, Description, Name and PhoneNumber of EventCreator
        mQRGenerator_StringBuffer_Result = new StringBuffer();
        mQRGenerator_StringBuffer_Result.append(mEvent.getCreatorEventId()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getEndDate())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getEndTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getRepetition()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getShortTitle()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getPlace()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getDescription()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getName()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getPhoneNumber());

        return mQRGenerator_StringBuffer_Result;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        try {
            stringBufferGenerator();

            //Put StringBuffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName; 10 = PhoneNumber
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

            // Check the EventCreatorName and is it itself Change the eventCreatorName to "Your Self"
            if (eventCreatorName.equals(mPerson.getName())) {
                eventCreatorName = getString(R.string.yourself);
            }

            // Event shortTitle in Headline with StartDate
            mQRGenerator_textView_headline.setText(getString(R.string.your_Event) + "\n" + shortTitle + ", " + startDate);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                mQRGenerator_textView_description.setText(getString(R.string.on) + startDate
                        + getString(R.string.find) + getString(R.string.from) + startTime + getString(R.string.until)
                        + endTime + getString(R.string.clock_at_room) + place + " " + shortTitle
                        + getString(R.string.instead_of) + "\n" + getString(R.string.eventDetails)
                        + description + "\n" + "\n" + getString(R.string.organizer) + eventCreatorName);
            } else {
                mQRGenerator_textView_description.setText(getString(R.string.as_of) + startDate
                        + getString(R.string.until) + endDate + getString(R.string.find)
                        + repetition + getString(R.string.at) + startTime + getString(R.string.clock_to)
                        + endTime + getString(R.string.clock_at_room) + place + " " + shortTitle
                        + getString(R.string.instead_of) + "\n" + getString(R.string.eventDetails) + description +
                        "\n" + "\n" + getString(R.string.organizer) + eventCreatorName);
            }
            // In the CatchBlock the User see a SnackBar Information and was pushed to CalendarActivity
        } catch (NullPointerException e) {
            Log.d(TAG, "QRGeneratorFragmentActivity:" + e.getMessage());
            mQRGenerator_button_start_eventFeedbackFragment.setVisibility(View.GONE);
            mQRGenerator_button_start_sharingFragment.setVisibility(View.GONE);

            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.generator_button_frame),
                    getString(R.string.ups_an_error),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.toCalender), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.calendar_frameLayout, new CalendarActivity());
                    fragmentTransaction.commit();
                    TabLayout tabLayout = getActivity().findViewById(R.id.tabBarLayout);
                    tabLayout.getTabAt(1).select();
                }
            }).show();

        } catch (ArrayIndexOutOfBoundsException z) {
            //If there is an Exception the Views change to Invisible and SnackBar tell that's anything wrong
            // and Push him back to the CalendarActivity
            Log.d(TAG, "QRGeneratorFragmentActivity:" + z.getMessage());
            mQRGenerator_textView_headline.setVisibility(View.GONE);
            mQRGenerator_textView_description.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_buttonFrame.setVisibility(View.GONE);
            mQRGenerator_relativeLayout_show_newFragment.setVisibility(View.VISIBLE);
            mQRGenerator_textView_description.setText(getString(R.string.wrongQRCodeResult) + "\n" + "\n"
                    + mQRGenerator_StringBuffer_Result + "\n" + "\n" + getString(R.string.notAsEventSaveable));

            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.generator_button_frame),
                    getString(R.string.ups_an_error),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.toCalender), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.calendar_frameLayout, new CalendarActivity());
                    fragmentTransaction.commit();
                    TabLayout tabLayout = getActivity().findViewById(R.id.tabBarLayout);
                    tabLayout.getTabAt(1).select();
                }
            }).show();
        }

        //Create a QR Code and Show it in the ImageView.
        mQRGenerator_button_start_sharingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a Bundle to send the Information to an other Fragment
                //The Bundle Input is the StringBuffer with the EventInformation
                QRSharingFragment qrSharingBundle = new QRSharingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("qrStringBufferDescription", String.valueOf(mQRGenerator_StringBuffer_Result));
                qrSharingBundle.setArguments(bundle);

                Bundle whichFragment = getArguments();

                if (whichFragment.get("fragment").equals("EventOverview")) {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.eventOverview_frameLayout, qrSharingBundle, "");
                    fragmentTransaction.addToBackStack("QrShareCA");
                    fragmentTransaction.commit();
                } else {
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                    fragmentTransaction.replace(R.id.calendar_frameLayout, qrSharingBundle, "QrShareCA");
                    fragmentTransaction.addToBackStack("QrShareCA");
                    fragmentTransaction.commit();
                }
            }
        });
    }
}
