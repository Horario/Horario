package hft.wiinf.de.horario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity
{
    private Button scanner_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scanner_btn = (Button) findViewById(R.id.scanner_btn);
        final Activity activity = this;
        scanner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }
    //TODO Der Output und der Abbruch müssen noch ausgearbeitet werden
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null)
            {
            Toast.makeText(this, "you cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else
            {
            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
    else
        {
        super.onActivityResult(requestCode, resultCode, data);
    }
}}
