package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.util.Log;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;


public class QRScannerActivity extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "QRScannerFragmentActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private String qrResult;
    private RelativeLayout mScannerResult_RelativeLayout_Main, mScannerResult_RelativeLayout_ButtonFrame, mScannerResult_RelativeLayout_goTo_CalendarFragment;
    private TextView mScannerResult_TextureView_Headline, mScannerResult_TextureView_Description;
    private Button mScannerResult_Button_addEvent, mScannerResult_Button_saveWithoutassent, mScannerResult_Button_rejectEvent;
    private StringBuffer mScannerResult_StingBuffer_QRResult;
    private int counter = 0;

    @Override
    public void onActivityCreated(Bundle savednstanceState) {
        super.onActivityCreated(savednstanceState);
    }

    //The Scanner start with the Call form CalendarActivity directly
    //ToDo Versuchen die Ansicht immernoch zu verbessern ..
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.activity_reader, container, false);
        return view;

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

        showCameraPreview();
    }

    public void startScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class); //Necessary to use the intern Sensor for Orientation
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Termincode scannen\n" +
                "Halte dein Smartphone vor den QR-Code und \n" +
                "scanne ihn ab, um den Termin zu öffnen");
        integrator.setCameraId(0);
        //ToDo Größe des Anzeigebereiches im Hochformat ändern.
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    /**
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    public void showCameraPreview() {
        //Check if User has permission to start to scan
        if (!isCameraPermissionGranted()) {
            requestCameraPermission();
        } else {
            startScanner();

        }
    }

    public boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        //For Fragment: requestPermissions(permissionsList,REQUEST_CODE);
        //For Activity: ActivityCompat.requestPermissions(this,permissionsList,REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If Permission ist Granted User get a SnackbarMessage and the Scanner Started
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make((getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame)),
                            "Danke für die Zugriffsrechte auf die Kamera!",
                            Snackbar.LENGTH_LONG).show();
                    startScanner();
                } else {
                    //If the User deny the access to the Camera he get two Chance to accept the Request
                    //The Counter count from 0 to 2. If the Counter 2 user is pushed to CalendarActivity
                    //The Default is to push the User to CalendarActivity
                    switch (counter) {
                        case 0:
                            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    "Wir brauchen den Kamerazugriff um den QR-Code einzuscannen.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("Nochmal", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    showCameraPreview();
                                }
                            }).show();
                            break;

                        case 1:
                            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    "Letzer Versuch! Bitte gestatte den Zugriff um Scannen zu können.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("Nochmal", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    showCameraPreview();
                                }
                            }).show();
                            break;
                        case 2:
                            Snackbar.make(getActivity().findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    "Okay du magst nicht? Vielleicht ein anderes mal :)",
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
                            break;
                        default:
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.scanner_result_realtiveLayout_CalendarFragment, new CalendarActivity());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            mScannerResult_RelativeLayout_ButtonFrame.setVisibility(View.GONE);
                            mScannerResult_RelativeLayout_Main.setVisibility(View.GONE);
                            mScannerResult_RelativeLayout_goTo_CalendarFragment.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    //Check the Scanner Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                qrResult = "Canceled";
            } else {
                qrResult = result.getContents();
            }
            displayQRResult();

        }
    }

    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void displayQRResult() {
        if (getActivity() != null && qrResult != null) {
            mScannerResult_TextureView_Description.setVisibility(View.VISIBLE);

            if (qrResult.equals("Canceled")) {
                mScannerResult_TextureView_Description.setText("Du hast das Scannen abgebrochen, " +
                        "bitte starte den Scanner neu");
            } else {
                try {
                    //If the Scan wasn't Canceled the GUI elements set to Visible
                    mScannerResult_TextureView_Headline.setVisibility(View.VISIBLE);
                    mScannerResult_Button_addEvent.setVisibility(View.VISIBLE);
                    mScannerResult_Button_saveWithoutassent.setVisibility(View.VISIBLE);
                    mScannerResult_Button_rejectEvent.setVisibility(View.VISIBLE);


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
                    mScannerResult_Button_addEvent.setVisibility(View.GONE);
                    mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
                    mScannerResult_Button_rejectEvent.setVisibility(View.GONE);
                    mScannerResult_TextureView_Headline.setVisibility(View.GONE);
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
                            mScannerResult_RelativeLayout_ButtonFrame.setVisibility(View.GONE);
                            mScannerResult_RelativeLayout_Main.setVisibility(View.GONE);
                            mScannerResult_RelativeLayout_goTo_CalendarFragment.setVisibility(View.VISIBLE);
                        }
                    }).show();
                }
            }
            //mScannerResult_TextureView_Description.setText("Das ist der Inhalt vom QR Code: "+"\n"+qrResult+
            //        "\n"+"Das können wir leider nicht als Termin speichern!");
            qrResult = null;
        }
    }

    // Put the Scanned Result to the Database
    //ToDo Speichern in die DB muss noch ausgearbeitet werden Daniels US.
    /*
    private void qrResultToDatabase() {

    */
    }

