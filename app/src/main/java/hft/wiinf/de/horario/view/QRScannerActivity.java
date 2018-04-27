package hft.wiinf.de.horario.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;

import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;


public class QRScannerActivity extends Fragment {
    private static final String TAG = "QRScannerFragmentActivity";

    //The Scanner start with the Call form CalendarActivity directly
    //ToDo Versuchen die Ansicht immernoch zu verbessern ..
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reader, container, false);
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class); //Necessary to use the intern Sensor for Orientation
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Termincode scannen\n" +
                "Halte dein Smartphone vor den QR-Code und \n" +
                "scanne ihn ab, um den Termin zu öffnen");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();

        return view;
    }



    //Use the Scanningresult to put them in den DataBase
    //TODO Der Output und der Abbruch müssen noch ausgearbeitet werden
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


}
