package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;
import java.util.logging.Logger;

import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.NoScanResultExceptionController;
import hft.wiinf.de.horario.controller.ScanResultReceiverController;


public class QRScanFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "QRScanFragmentActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private final String noResultErrorMsg = "No scan data received!";
    //Counter for the Loop of PermissionChecks
    private int counter = 0;
    private String codeFormat, codeContent;

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

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // for each permission check if the user granted/denied them you may want to group the
            // rationale in a single dialog,this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    if (!showRationale) {
                        // user also CHECKED "never ask again" you can either enable some fall back,
                        // disable features of your app or open another dialog explaining again the
                        // permission and directing to the app setting

                        new AlertDialog.Builder(getActivity())
                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                            restartApp();
                                            return true;
                                        }
                                        return false;
                                    }
                                })
                                .setTitle(R.string.accessWith_NeverAskAgain_deny)
                                .setMessage(R.string.requestPermission_accessDenied_withCheckbox)
                                .setPositiveButton(R.string.toCalender, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       restartApp();
                                    }
                                })
                                .create().show();
                    } else if (counter < 1) {
                        // user did NOT check "never ask again" this is a good place to explain the user
                        // why you need the permission and ask if he wants // to accept it (the rationale)
                        new AlertDialog.Builder(getActivity())
                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                            restartApp();
                                            return true;
                                        }
                                        return false;
                                    }
                                })
                                .setTitle(R.string.requestPermission_firstTryRequest)
                                .setMessage(R.string.requestPermission_askForPermission)
                                .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter++;
                                        showCameraPreview();
                                    }
                                })
                                .setNegativeButton(R.string.toCalender, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restartApp();
                                    }
                                })
                                .create().show();
                    } else if (counter == 1) {
                        new AlertDialog.Builder(getActivity())
                                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if(keyCode == KeyEvent.KEYCODE_BACK){
                                            restartApp();
                                            return true;
                                        }
                                        return false;
                                    }
                                })
                                .setTitle(R.string.requestPermission_lastTryRequest)
                                .setMessage(R.string.requestPermission_askForPermission)
                                .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter++;
                                        showCameraPreview();
                                    }
                                })
                                .setNegativeButton(R.string.toCalender, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restartApp();
                                    }
                                })
                                .create().show();
                    } else {
                        restartApp();
                    }
                }else {
                    startScanner();
                }
                }

            }


        }

   // }


    // Restart the App
    private void restartApp(){
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
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

