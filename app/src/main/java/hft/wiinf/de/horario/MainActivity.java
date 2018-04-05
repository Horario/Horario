package hft.wiinf.de.horario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
private Button scnbtn;
private Button genbtn;
    //TODO JavaDoc Konforme Beschreibung in allen Klassen aufschreiben
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scnbtn = (Button)findViewById(R.id.scnbtn);
        final Activity activity = this;
        scnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(MainActivity.this, QRScannerActivity.class));
            }
        });
        genbtn = (Button)findViewById(R.id.genbtn);
        final Activity activity1 =this;
        genbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QRGeneratorActivity.class));
            }
        });

    }
    
}
