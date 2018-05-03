package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private TextView mQRGenerator_textView_description, mQRGenerator_textView_headline;
    private RelativeLayout mQRGenerator_RelativeLayout_show_qrSharingFragment;
    private Button mQRGenerator_button_start_sharingFragment, mQRGenerator_button_start_eventFeedbackFragment;
    private Person mPerson;
    private StringBuffer mQRGenerator_StringBuffer_headline, mQRGenerator_StringBuffer_description, mQRGenerator_StringBuffer_test;
    private Event mEvent, mEvent2, mEvent3;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);

        //Initial GUI
        mQRGenerator_button_start_sharingFragment = view.findViewById(R.id.generator_button_start_qrShareingFragment);
        mQRGenerator_button_start_eventFeedbackFragment = view.findViewById(R.id.generator_button_show_eventFeedbackFragment);
        mQRGenerator_textView_description = view.findViewById(R.id.generator_textView_description);
        mQRGenerator_textView_headline = view.findViewById(R.id.generator_textView_Headline);
        mQRGenerator_RelativeLayout_show_qrSharingFragment = view.findViewById(R.id.generator_realtivLayout_show_qrSharingFragment);

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

        mEvent2 = new Event();
        mEvent2.setDescription("Mathe mit Hr. Conradt");
        mEvent2.setPlace("1/208");
        mEvent2.setStartTime(startDate);
        mEvent2.setEndTime(endDate);
        EventController.saveEvent(mEvent2);

        mEvent3 = new Event();
        mEvent3.setDescription("Quell des Wissens kann ein DataLake sein");
        mEvent3.setPlace("Hallenbad");
        mEvent3.setStartTime(startDate);
        mEvent3.setEndTime(endDate);
        EventController.saveEvent(mEvent3);

        mPerson = PersonController.getPersonWhoIam();

        return view;
    }

    public StringBuffer stringBufferGenerator(){
        //Modify the Dateformat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | "; //

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // Creator, StartDate, EndDate, StartTime, EndTime, Place, Description
        mQRGenerator_StringBuffer_description = new StringBuffer();
        mQRGenerator_StringBuffer_description.append(simpleDateFormat.format(mEvent.getStartTime())+ stringSplitSymbol);
        mQRGenerator_StringBuffer_description.append(simpleTimeFormat.format(mEvent.getStartTime())+ stringSplitSymbol);
        mQRGenerator_StringBuffer_description.append(simpleTimeFormat.format(mEvent.getEndTime())+ stringSplitSymbol);
        mQRGenerator_StringBuffer_description.append(mEvent.getPlace()+ stringSplitSymbol);
        mQRGenerator_StringBuffer_description.append(mEvent.getDescription()+stringSplitSymbol);
        mQRGenerator_StringBuffer_description.append(mPerson.getName());

        return mQRGenerator_StringBuffer_description;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        String [] eventStringBufferArray = String.valueOf(stringBufferGenerator()).split("\\|");
        String startDate = eventStringBufferArray[0];
        String startTime = eventStringBufferArray[1];
        String endTime = eventStringBufferArray[2];
        String description = eventStringBufferArray[4];
        String location = eventStringBufferArray[3];
        String eventCreator = eventStringBufferArray[5];

        mQRGenerator_textView_headline.setText("Dein Termin"+"\n"+description+", "+startDate);
        mQRGenerator_textView_description.setText(stringBufferGenerator());

        //Create a QR Code and Show it in the ImageView.
        //ToDo Imput nicht fester String sondern muss aus DB kommen. Dazu müssen die Daten mit toString und StringBuffer zusammengeführt werden.
        mQRGenerator_button_start_sharingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a Bundle to Send the Information to an other Fragment
                //The Bundle input is the StringBuffer with the EventInformation
                QRSharingActivity qrSharingBundle = new QRSharingActivity();
                Bundle bundle = new Bundle();
                bundle.putString("qrStringBufferDescription", String.valueOf(mQRGenerator_StringBuffer_description));
                qrSharingBundle.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, qrSharingBundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mQRGenerator_textView_headline.setVisibility(View.GONE);
                    mQRGenerator_textView_description.setVisibility(View.GONE);
                    mQRGenerator_button_start_sharingFragment.setVisibility(View.GONE);
                    mQRGenerator_button_start_eventFeedbackFragment.setVisibility(View.GONE);
                    mQRGenerator_RelativeLayout_show_qrSharingFragment.setVisibility(View.VISIBLE);

            }
        });
    }

}
