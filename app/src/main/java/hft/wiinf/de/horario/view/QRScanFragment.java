package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.Objects;

import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.*;


public class QRScanFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "QRScanFragmentActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    //Counter for the Loop of PermissionChecks
    private int counter = 0;
    private String codeFormat, codeContent;
    private final String noResultErrorMsg = "No scan data received!";


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //The Scanner start with the Call form CalendarActivity directly
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_qrscan, container, false);
        return view;
    }

    @SuppressLint("ResourceType")
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        //Call a Method to start at first a permission Check and if this granted it start the Scanner
        //in FullScreenMode
        showCameraPreview();
    }

    public void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class); //Necessary to use the intern Sensor for Orientation
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scanneroverlayer_qrCodeScan) + "\n" +
                getString(R.string.scanneroverlay_positionYourScanner) + "\n" +
                getString(R.string.scanneroverlay_toShowTheEvent));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void showCameraPreview() {
        //Check if User has permission to start to scan, if not it's start a RequestLoop
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If Permission ist Granted User get a SnackbarMessage and the Scanner Started
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make((Objects.requireNonNull(getActivity()).findViewById(R.id.scanner_result_relativeLayout_buttonFrame)),
                            R.string.requestPermission_thankYou,
                            Snackbar.LENGTH_LONG).show();
                    startScanner();
                } else {
                    //If the User deny the access to the Camera he get two Chance to accept the Request
                    //The Counter count from 0 to 2. If the Counter 2 user is pushed to CalendarActivity
                    //The Default is to push the User to CalendarActivity
                    switch (counter) {
                        case 0:
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    R.string.requestPermission_askForPermission,
                                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.requestPermission_againButton, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    showCameraPreview();
                                }
                            }).show();
                            break;

                        case 1:
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    R.string.requestPermission_lastTryRequest,
                                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.requestPermission_againButton, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    showCameraPreview();
                                }
                            }).show();
                            break;
                        case 2:
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.scanner_result_relativeLayout_buttonFrame),
                                    R.string.requestPermission_userDontLike,
                                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.toCalender), new View.OnClickListener() {
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
                            Intent intent = Objects.requireNonNull(getActivity()).getIntent();
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
            Objects.requireNonNull(parentActivity).scanResultData(codeFormat, codeContent);

        } else {
            // send exception
            Objects.requireNonNull(parentActivity).scanResultData(new NoScanResultExceptionController(noResultErrorMsg));
        }

    }


}

