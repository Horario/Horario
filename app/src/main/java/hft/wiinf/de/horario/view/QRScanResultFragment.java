package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
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

import com.activeandroid.util.Log;

import hft.wiinf.de.horario.R;

import static android.support.constraint.Constraints.TAG;

public class QRScanResultFragment extends Fragment {
    private static final String TAG = "QRScanResultFragment";
    private RelativeLayout mScannerResult_RelativeLayout_Main, mScannerResult_RelativeLayout_ButtonFrame,
            mScannerResult_RelativeLayout_goTo_CalendarFragment;
    private TextView mScannerResult_TextureView_Headline, mScannerResult_TextureView_Description;
    private Button mScannerResult_Button_addEvent, mScannerResult_Button_saveWithoutassent,
            mScannerResult_Button_rejectEvent;


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

        //GUI initial
        mScannerResult_RelativeLayout_Main = view.findViewById(R.id.scanner_result_relativeLayout_main);
        mScannerResult_RelativeLayout_ButtonFrame = view.findViewById(R.id.scanner_result_relativeLayout_buttonFrame);
        mScannerResult_RelativeLayout_goTo_CalendarFragment = view.findViewById(R.id.scanner_result_realtiveLayout_CalendarFragment);
        mScannerResult_TextureView_Description = view.findViewById(R.id.scanner_result_textview_eventText);
        mScannerResult_TextureView_Headline = view.findViewById(R.id.scanner_result_textView_headline);
        mScannerResult_Button_saveWithoutassent = view.findViewById(R.id.scanner_result_button_save_without_assent);
        mScannerResult_Button_rejectEvent = view.findViewById(R.id.scanner_result_button_reject_event);
        mScannerResult_Button_addEvent = view.findViewById(R.id.scanner_result_button_addEvent);

        //Make the Element at first Unvisible
        mScannerResult_TextureView_Description.setVisibility(View.GONE);
        mScannerResult_TextureView_Headline.setVisibility(View.GONE);
        mScannerResult_Button_addEvent.setVisibility(View.GONE);
        mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
        mScannerResult_Button_rejectEvent.setVisibility(View.GONE);

        displayQRResult();


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
            String[] eventStringBufferArray = qrScanResultBundle().split("\\|");
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

}
