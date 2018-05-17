package hft.wiinf.de.horario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {

    private static final String TAG = "QRScanFragmentActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private String scanningResult;

    private ConstraintLayout mScannerResult_ConstraintLayout_Main, mScannerResult_ConstraintLayout_ButtonFrame;
    private TextView mScannerResult_TextureView_Headline, mScannerResult_TextureView_Description;
    private Button mScannerResult_Button_addEvent, mScannerResult_Button_saveWithoutassent, mScannerResult_Button_rejectEvent;
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        //GUI initial
        mScannerResult_ConstraintLayout_Main = findViewById(R.id.scanner_result_constraintLayout_main);
        mScannerResult_ConstraintLayout_ButtonFrame = findViewById(R.id.scanner_result_constraintLayout_buttonFrame);
        mScannerResult_TextureView_Description = findViewById(R.id.scanner_result_textview_eventText);
        mScannerResult_TextureView_Headline = findViewById(R.id.scanner_result_textView_headline);
        mScannerResult_Button_saveWithoutassent = findViewById(R.id.scanner_result_button_save_without_assent);
        mScannerResult_Button_rejectEvent = findViewById(R.id.scanner_result_button_reject_event);
        mScannerResult_Button_addEvent = findViewById(R.id.scanner_result_button_addEvent);

        //Make the Element at first Invisible
        mScannerResult_TextureView_Description.setVisibility(View.GONE);
        mScannerResult_TextureView_Headline.setVisibility(View.GONE);
        mScannerResult_Button_addEvent.setVisibility(View.GONE);
        mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
        mScannerResult_Button_rejectEvent.setVisibility(View.GONE);

        //ClickListener for the Buttons to Add the Termin to the DB without assigning,
        // to Add the Event to the DB with assigning and Reject the Event! Assigning and Reject start
        // to send one SMS to the EventCreator
        mScannerResult_Button_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Dennis -> Hier den Code einfügen und das finish aber stehen lassen!

                finish();
            }
        });

        mScannerResult_Button_rejectEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Dennis -> Hier den Code einfügen und das finish aber stehen lassen!

                finish();
            }
        });

        mScannerResult_Button_saveWithoutassent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Dennis -> Hier den Code einfügen und das finish aber stehen lassen!

                finish();
            }
        });

        //Activity Starts with PermissionCheck for Camera Using
        showCameraPreview();
    }

    public void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
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


    @Override
    public void onPause() {
        super.onPause();
        }

    public void showCameraPreview() {
        //Check if User has permission to start to scan
        if (!isCameraPermissionGranted()) {
            requestCameraPermission();
        } else {
            startScanner();
        }
    }

    public boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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
                    Snackbar.make((this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame)),
                            "Danke für die Zugriffsrechte auf die Kamera!",
                            Snackbar.LENGTH_LONG).show();

                    startScanner();

                } else {
                    //If the User deny the access to the Camera he get two Chance to accept the Request
                    //The Counter count from 0 to 2. If the Counter 2 user is pushed to CalendarActivity
                    //The Default is to push the User to CalendarActivity
                    switch (counter) {
                        case 0:
                            Snackbar.make(this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame),
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
                            Snackbar.make(this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame),
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
                            Snackbar.make(this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame),
                                    "Okay du magst nicht? Vielleicht ein anderes mal :)",
                                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                   finish();
                                }
                            }).show();
                            break;
                        default:
                           finish();
                    }
                }
            }
        }
    }


    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void displayQRResult(String intentResult) {
        if (intentResult != null) {
            mScannerResult_TextureView_Description.setVisibility(View.VISIBLE);

            if (intentResult.equals("Canceled")) {
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
                    String[] eventStringBufferArray = intentResult.split("\\|");
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


                    Snackbar.make(this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame),
                            "Ups! Fehler Aufgetreten!",
                            Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();

                } catch (ArrayIndexOutOfBoundsException z) {
                    Log.d(TAG, "QRScanFragmentActivity:" + z.getMessage());
                    mScannerResult_Button_addEvent.setVisibility(View.GONE);
                    mScannerResult_Button_saveWithoutassent.setVisibility(View.GONE);
                    mScannerResult_Button_rejectEvent.setVisibility(View.GONE);
                    mScannerResult_TextureView_Headline.setVisibility(View.GONE);
                    mScannerResult_TextureView_Description.setText("Das ist der Inhalt vom QR Code: " + "\n" + scanningResult +
                            "\n" + "Das können wir leider nicht als Termin speichern!");

                    Snackbar.make(this.findViewById(R.id.scanner_result_constraintLayout_buttonFrame),
                            "Ups! Falscher QR-Code!",
                            Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
            scanningResult = null;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (intent != null) {

            displayQRResult(scanningResult.getContents());
          } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}