package hft.wiinf.de.horario.view;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.Event;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private ImageView mQRGenImageViewResult;
    private TextView mQRGenTextViewInput;
    private RelativeLayout mRelativeLayout_QRGenResult;
    private Button mButtonQRCreation;
    private BitMatrix mBitmatrix;
    private Bitmap mBitmapOfQRCode;
    private StringBuffer mDataBaseStringBufferResult;
    private Event mEvent;
    private Event mEvent2;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);

        //Initial GUI
        mButtonQRCreation = view.findViewById(R.id.createQRCode);
        mQRGenImageViewResult= view.findViewById(R.id.generator_imageView_qrResult);
        mRelativeLayout_QRGenResult = view.findViewById(R.id.qr_main);
        mQRGenTextViewInput = view.findViewById(R.id.generator_textView_text_output);

        //Erstellen von zwei Dummydaten
        Date startDate = new Date();
        Date endDate = new Date();

mEvent = EventController.getEventById(getArguments().getLong("eventId"));


       /* mEvent2 = new Event();
        mEvent.setDescription("Mathe mit Hr. Conradt");
        mEvent.setPlace("1/208");
        mEvent.setStartTime(startDate);
        mEvent.setEndTime(endDate);
        EventController.saveEvent(mEvent2);*/
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //Modify the Dateformat form den DB to get a more readable Form
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss");

        // Merge the Data Base Informations to one Single StringBuffer
        mDataBaseStringBufferResult = new StringBuffer();
        mDataBaseStringBufferResult.append(mEvent.getDescription()+"\n");
        mDataBaseStringBufferResult.append(simpleDateFormat.format(mEvent.getStartTime())+"\n");
        mDataBaseStringBufferResult.append(simpleDateFormat.format(mEvent.getEndTime())+"\n");
        mDataBaseStringBufferResult.append(mEvent.getPlace()+"\n");



        //Create a QR Code and Show it in the ImageView.
        //ToDo Imput nicht fester String sondern muss aus DB kommen. Dazu müssen die Daten mit toString und StringBuffer zusammengeführt werden.
        mButtonQRCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    //Change the StringBuffer to a String for Output in the LayoutView
                    mBitmatrix = multiFormatWriter.encode(String.valueOf(mDataBaseStringBufferResult), BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    mBitmapOfQRCode = barcodeEncoder.createBitmap(mBitmatrix);
                    mQRGenImageViewResult.setImageBitmap(mBitmapOfQRCode);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        mQRGenTextViewInput.setText(mDataBaseStringBufferResult);
    }

}
