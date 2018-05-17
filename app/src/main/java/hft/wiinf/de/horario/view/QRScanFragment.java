package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.*;


public class QRScanFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "QRScanFragmentActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private RelativeLayout mScannerResult_RelativeLayout_Main, mScannerResult_RelativeLayout_ButtonFrame, mScannerResult_RelativeLayout_goTo_CalendarFragment;
    private TextView mScannerResult_TextureView_Description;
    private int counter = 0;

    private String codeFormat, codeContent;
    private final String noResultErrorMsg = "No scan data received!";


    @Override
    public void onActivityCreated(Bundle savednstanceState) {
        super.onActivityCreated(savednstanceState);
    }

    //The Scanner start with the Call form CalendarActivity directly
    //ToDo Versuchen die Ansicht immernoch zu verbessern ..
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_qrscan, container, false);
        return view;
    }

    @SuppressLint("ResourceType")
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //Eigentlich startet es mit der Camerazugriffsberechtigung
        showCameraPreview();
    }

    public void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
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
                                    Intent intent = getActivity().getIntent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    getActivity().overridePendingTransition(0, 0);
                                    getActivity().finish();

                                    getActivity().overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            }).show();
                            break;
                        default:
                            Intent intent = getActivity().getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);
                    }
                }
            }
        }
    }

    //Check the Scanner Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        ScanResultReceiverController parentActivity = (ScanResultReceiverController) this.getActivity();

        if (scanningResult != null) {
            //we have a result
            codeContent = scanningResult.getContents();
            codeFormat = scanningResult.getFormatName();
            // send received data
            parentActivity.scanResultData(codeFormat, codeContent);

        } else {
            // send exception
            parentActivity.scanResultData(new NoScanResultExceptionController(noResultErrorMsg));
        }

    }


}

