package hft.wiinf.de.horario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class QRScannerActivity extends AppCompatActivity
{
    DatabaseHelper myDb;

    private Button scanner_btn;
    private Button showData_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scanner_btn = (Button) findViewById(R.id.scanner_btn);
        showData_btn = (Button) findViewById(R.id.showData_btn);
        myDb = new DatabaseHelper(this);
        final Activity activity = this;
        scanner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setOrientationLocked(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        viewAll();
    }
    //TODO Der Output und der Abbruch müssen noch ausgearbeitet werden
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "you cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else
            {
                myDb.insertData(result.getContents());
                Toast.makeText(this, "added Data", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void viewAll()
    {
        showData_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res =myDb.getAllData();
                        if(res.getCount() == 0)
                        {
                            showMessage("Error", "Nothing in Database");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()){
                            buffer.append("Id: " + res.getString(0) + "\n");
                            buffer.append("Text: " + res.getString(1) + "\n\n");
                        }
                        //show all data

                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
