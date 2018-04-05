package hft.wiinf.de.horario;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorActivity extends AppCompatActivity {
EditText gen_text;
Button gen_btn;
ImageView qr_image;
String text2Qr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);
        gen_text = (EditText)findViewById(R.id.gen_text);
        gen_btn = (Button)findViewById(R.id.gen_btn);
        qr_image = (ImageView)findViewById(R.id.qr_image);

        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text2Qr = gen_text.getText().toString().trim();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qr_image.setImageBitmap(bitmap);
                                    }
            catch (WriterException e){
                    e.printStackTrace();

            }
            }
        });
    }
}
