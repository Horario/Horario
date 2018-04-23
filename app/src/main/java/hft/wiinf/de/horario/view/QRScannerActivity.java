package hft.wiinf.de.horario.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.DatabaseHelperController;
import hft.wiinf.de.horario.model.Event;


public class QRScannerActivity extends Fragment {
    private static final String TAG = "QRScannerFragmentActivity";
    private DatabaseHelperController myDb;
    private String qrResult;
    private Button showData_btn;
    private RelativeLayout mRelativeLayout_scanner_result;
    private TextView mTextureView_scanner_result;
    private StringBuffer qrResultModefied;
    private Event mEvent;

    //Neu
    public QRScannerActivity() {
    }

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

        mRelativeLayout_scanner_result = view.findViewById(R.id.scanner_temp_Result);
        mTextureView_scanner_result= view.findViewById(R.id.scanner_temp_textview);
        startScanner();
    }

    public void startScanner(){
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

    @SuppressLint("SetTextI18n")
    private void displayQRResult() {

        if (getActivity() != null && qrResult != null) {
            qrResultModefied = new StringBuffer(qrResult);
            qrResultModefied.replace(0, 111, "");
            qrResultModefied.replace(76, 96, "");
            qrResultModefied.replace(91, 137, "");
            qrResultModefied.replace(47, 48, ":");
            qrResultModefied.replace(69, 70, ":");
            qrResultModefied.replace(30, 31, "");
            qrResultModefied.replace(54, 55, "");
            qrResultModefied.replace(76, 76, "");

            /* Modifiziert Ausgabe des QR Codes umd mit der DB zu testen!
            SUMMARY:Mathe bei Herr Conradt
            DTstart:20180421:124000
            DTEND:20180424:134000
            LOCATION:Labor

            LOCATION = place in der DB
            DTSTART = startTime in der DB
            DTEND = endTime in der DB
            SUMMARY = description in der DB
             */
           // mEvent = new Event();
            //mEvent.setStartTime(java.sql.Time.valueOf(qrResultModefied.substring(47,54)));

            mTextureView_scanner_result.setText(
                    qrResultModefied.subSequence(8, 30)+"\n"+ //Summary
                    qrResultModefied.subSequence(38, 46)+"\n"+ //startDatum
                    qrResultModefied.subSequence(47, 54)+"\n"+ //startUhrzeit
                    qrResultModefied.subSequence(59, 67)+"\n"+ //endDatum
                    qrResultModefied.subSequence(68, 74)+"\n"+ //endUhrzeit
                    qrResultModefied.subSequence(83, qrResultModefied.length())+"\n"); //Ort


            qrResult = null;
        }
    }

    private void qrResultToDatabase(){
        if (getActivity() != null && qrResult != null) {

            mEvent = new Event();
            mEvent.setStartTime(java.sql.Time.valueOf(qrResultModefied.substring(47,54)));
           // mEvent.setStartTime(mTextureView_scanner_result.setText(qrResultModefied.subSequence(47,54)));

            //if(qrResultModefied.split("\n"))
            //qrResultModefied.


            //qrResult=null;
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                qrResult = "Canceld";
            } else {
                qrResult = "Scanned from fragment:" + result.getContents();
            }
            displayQRResult();
        }


        /*//Temp Button for Testing the Database Input
        showData_btn = view.findViewById(R.id.showData_btn);
        viewAll();
        myDb = new DatabaseHelperController(getActivity());
        return view;
    }


    //Use the Scanningresult to put them in den DataBase
    //TODO Der Output und der Abbruch müssen noch ausgearbeitet werden
    /*@SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);

             if (result != null) {
            if (result.getContents() == null) {
                    /*FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //rLayout_main.setVisibility(View.GONE);
                    //rLayout_fragment.setVisibility(View.VISIBLE);*/
    /*             Toast.makeText(getActivity(), "you cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                   /* FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //rLayout_main.setVisibility(View.GONE);
                    //rLayout_fragment.setVisibility(View.VISIBLE);
                    //myDb.insertData(result.getContents());*/
      /*              Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
            else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    */
        //Temp Method to Show the DB entries
     /*   public void viewAll () {
            showData_btn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Cursor res = myDb.getAllData();
                            if (res.getCount() == 0) {
                                showMessage("Error", "Nothing in Database");
                                return;
                            }

                            StringBuffer buffer = new StringBuffer();
                            while (res.moveToNext()) {
                                buffer.append("Id: " + res.getString(0) + "\n");
                                buffer.append("Text: " + res.getString(1) + "\n\n");
                            }
                            //show all data
                            showMessage("Data", buffer.toString());
                        }
                    }
            );
        }

        //ToDo Was macht diese Methode eigentlich genau?
        public void showMessage (String title, String Message){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(Message);
            builder.show();
     */
    }
}
