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
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private ImageView mQRGenImageViewResult; //ToDo ImageView ist Temp Info und muss noch entfernt werden.
    private TextView mQRGenerator_textView_descrition;
    private TextView mQRGenerator_textView_headline;
    private RelativeLayout mRelativeLayout_QRGenResult;
    private Button mQRGenerator_button_start_sharingFragment, mQRGenerator_button_start_eventFeedbackFragment;
    private BitMatrix mBitmatrix;
    private Bitmap mBitmapOfQRCode;
    private Person mPerson;
    private StringBuffer mQRGenerator_StringBuffer_headline, mQRGenerator_StringBuffer_description, mQRGenerator_Stringbuffer_bla;
    private Event mEvent;
    private Event mEvent2;
    private Event mEvent3;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);

        //Initial GUI
        mQRGenerator_button_start_sharingFragment = view.findViewById(R.id.generator_button_start_shareFragment);
        mQRGenerator_button_start_eventFeedbackFragment = view.findViewById(R.id.generator_button_show_eventReject);
        mQRGenerator_textView_descrition = view.findViewById(R.id.generator_textView_text_output);
        mQRGenerator_textView_headline = view.findViewById(R.id.generator_textView_Headline);
        mQRGenImageViewResult= view.findViewById(R.id.generator_imageView_qrResult);
        mRelativeLayout_QRGenResult = view.findViewById(R.id.generator_texView_frame);

        //Erstellen von drei Dummydaten
        //ToDo Dummydaten löschen
        Date startDate = new Date();
        Date endDate = new Date();

        mEvent = new Event();
        mEvent.setDescription("Wir machen Experimente im Labor 3");
        mEvent.setPlace("Labor 3");
        mEvent.setStartTime(startDate);
        mEvent.setEndTime(endDate);
        EventController.saveEvent(mEvent);

        mEvent2 = new Event();
        mEvent2.setDescription("Mathe mit Hr. Conradt");
        mEvent2.setPlace("1/208");
        mEvent2.setStartTime(startDate);
        mEvent2.setEndTime(endDate);
        EventController.saveEvent(mEvent2);

        mEvent3 = new Event();
        mEvent3.setDescription("Quell des Wissens kann ein DataLake sein");
        mEvent3.setPlace("Hallenbad");
        mEvent3.setStartTime(startDate);
        mEvent3.setEndTime(endDate);
        EventController.saveEvent(mEvent3);

        mPerson = PersonController.getPersonWhoIam();

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //Modify the Dateformat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = "|"; //

        // Merge the Data Base Informations to one Single StringBuffer with the Format:
        // Creator, StartDate, EndDate, StartTime, EndTime, Place, Description
        // Headline first
        mQRGenerator_StringBuffer_headline = new StringBuffer();
        mQRGenerator_StringBuffer_headline.append(simpleDateFormat.format(mEvent.getStartTime())+"\n");

        // Description Window
        //ToDo Verfeinerung durch IF abfrage ob es sich um ein Serientermin handelt dann kann demensprechen einen andere View eingefügt werden
        mQRGenerator_StringBuffer_description = new StringBuffer();
        mQRGenerator_StringBuffer_description.append("Am "+"\n");
        mQRGenerator_StringBuffer_description.append(simpleDateFormat.format(mEvent.getStartTime())+"\n" + " von " + "\n");
        mQRGenerator_StringBuffer_description.append(simpleTimeFormat.format(mEvent.getStartTime())+" bis ");
        mQRGenerator_StringBuffer_description.append(simpleTimeFormat.format(mEvent.getEndTime())+" im Raum ");
        mQRGenerator_StringBuffer_description.append(mEvent.getPlace()+"\n"+" Findet: ");
        mQRGenerator_StringBuffer_description.append(mEvent.getDescription()+stringSplitSymbol+" statt."+"\n");
        mQRGenerator_StringBuffer_description.append("Orangisator ist: ");
        mQRGenerator_StringBuffer_description.append(mPerson.getName());

        //Create a QR Code and Show it in the ImageView.
        //ToDo Imput nicht fester String sondern muss aus DB kommen. Dazu müssen die Daten mit toString und StringBuffer zusammengeführt werden.
        mQRGenerator_button_start_sharingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    //Change the StringBuffer to a String for Output in the LayoutView
                    mBitmatrix = multiFormatWriter.encode(String.valueOf(mQRGenerator_StringBuffer_description), BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    mBitmapOfQRCode = barcodeEncoder.createBitmap(mBitmatrix);
                    mQRGenImageViewResult.setImageBitmap(mBitmapOfQRCode);


                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        mQRGenerator_textView_headline.setText(mQRGenerator_StringBuffer_headline);
        mQRGenerator_textView_descrition.setText(mQRGenerator_StringBuffer_description);
    }

}
