package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.Date;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.model.Event;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private ImageView mQRGenImageViewResult;
    private TextView mQRGenTextViewInput;
    private RelativeLayout mRelativeLayout_QRGenResult;
    private Button mButtonQRCreation;
    private BitMatrix mBitmatrix;
    private String raum;
    private Event mEvent;
    //private Event mEvent;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceType")
    public void onViewCreated(final View view, Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);
        mButtonQRCreation = view.findViewById(R.id.createQRCode);
        raum = "SUMMARY:Mathe bei Herr Conradt\n" +
                "DTstart:20180421T124000\n" +
                "DTEND:20180424T134000\n" +
                "LOCATION:Labor";

        mEvent = new Event();
        Date d = new Date();
        mEvent.setStartTime(d);
        mEvent.setEndTime(d);
        mEvent.setDescription("test1");
        mEvent.save();



        //Create a QR Code and Show it in the ImageView.
        //ToDo Imput nicht fester String sondern muss aus DB kommen. Dazu müssen die Daten mit toString und StringBuffer zusammengeführt werden.
        mButtonQRCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    mBitmatrix = multiFormatWriter.encode(raum, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(mBitmatrix);
                    mQRGenImageViewResult.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        //Dummydaten für die DB zum Testen des QR Code Generators ... geht noch nicht

        Calendar endZeit = Calendar.getInstance();
        endZeit.add(Calendar.HOUR, 1);
        Calendar startZeit = Calendar.getInstance();


        mQRGenImageViewResult= view.findViewById(R.id.generator_temp_imageView_result);
        mRelativeLayout_QRGenResult = view.findViewById(R.id.qr_main);
        mQRGenTextViewInput = view.findViewById(R.id.generator_temp_imput);

        mQRGenTextViewInput.setText(raum);

        return view;
    }



}
