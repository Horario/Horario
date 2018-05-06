package hft.wiinf.de.horario.view;


import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import hft.wiinf.de.horario.CaptureActivityPortrait;
import hft.wiinf.de.horario.DatabaseHelper;
import hft.wiinf.de.horario.R;


public class QRScannerActivity extends Fragment {
    private static final String TAG = "QRScannerFragmentActivity";
    private DatabaseHelper myDb;

    private Button showData_btn;

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
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

        //Temp Button for Testing the Database Input
        showData_btn = view.findViewById(R.id.showData_btn);
        viewAll();
        myDb = new DatabaseHelper(getActivity());
        return view;
    }



    //Use the Scanningresult to put them in den DataBase
    //TODO Der Output und der Abbruch müssen noch ausgearbeitet werden
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "you cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                myDb.insertData(result.getContents());
                Toast.makeText(getActivity(), "added Data", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Temp Method to Show the DB entries
    public void viewAll() {
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
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
