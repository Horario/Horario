package hft.wiinf.de.horario.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
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

import java.io.File;

import hft.wiinf.de.horario.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRSharingActivity extends Fragment {
    private static final String TAG = "QRSharingFragmentActivity";
    private RelativeLayout mQRSharing_relativeLayout_buttonFrame, mQRSharing_reativeLayout_textViewFrame, mQRSharing_relativeLayout_calendarActivity;
    private TextView mQRSharing_textView_headline, mQRSharing_textView_description;
    private Button mQRSharing_button_shareWith, mQRSharing_button_showInCalendar;
    private ImageView mQRSharing_imageView_qrCode;
    private BitMatrix mBitmatrix;
    private Bitmap mBitmapOfQRCode;

    public QRSharingActivity() {
        // Required empty public constructor
    }

    //Create the QR Code from StringBuffer Data and Show it as a Bitmap
    public void qrBitMapGenerator() {
        //Change the StringBuffer to a String for Output in the ImageView
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            mBitmatrix = multiFormatWriter.encode(eventStringResultDescription(), BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            mBitmapOfQRCode = barcodeEncoder.createBitmap(mBitmatrix);
            mQRSharing_imageView_qrCode.setImageBitmap(mBitmapOfQRCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    // Get the EventDescriptionResult (StringBuffer) from the QRGeneratorActivityFragment
    public String eventStringResultDescription() {
        Bundle qrDescription = getArguments();
        String qrStringBufferResult = qrDescription.getString("qrStringBufferDescription");
        return qrStringBufferResult;
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_qrsharing, container, false);

        //GUI initial
        mQRSharing_relativeLayout_buttonFrame = view.findViewById(R.id.qrShare_relativLayout_buttonFrame);
        mQRSharing_reativeLayout_textViewFrame = view.findViewById(R.id.qrShare_relativLayout_textViewFrame);
        mQRSharing_relativeLayout_calendarActivity = view.findViewById(R.id.qrSharing_relativLayout_calendarActivity);
        mQRSharing_textView_headline = view.findViewById(R.id.qrSharing_textView_headline);
        mQRSharing_textView_description = view.findViewById(R.id.qrSharing_textView_description);
        mQRSharing_button_shareWith = view.findViewById(R.id.qrSharing_button_shareWith);
        mQRSharing_button_showInCalendar = view.findViewById(R.id.qrSharing_button_showInCalendar);
        mQRSharing_imageView_qrCode = view.findViewById(R.id.qrSharing_imageView_qrCode);

        //Start the QR Code GeneratorMethod and Show all Even Informations
        qrBitMapGenerator();

        //Put StringBufffer in Array
        //Index: 0 = StartDate; 1 = StartTime; 2= EndTime; 3=Descriptoin; 4=Location; 5=EventCreator
        try {
            String[] eventStringBufferResultAsArray = eventStringResultDescription().split("\\| ");
            String startDate = eventStringBufferResultAsArray[0];
            String startTime = eventStringBufferResultAsArray[1];
            String endTime = eventStringBufferResultAsArray[2];
            String description = eventStringBufferResultAsArray[4];
            String location = eventStringBufferResultAsArray[3];
            String eventCreator = eventStringBufferResultAsArray[5];
            mQRSharing_textView_description.setText(startDate + "\n" + location + "\n" + eventCreator);
            mQRSharing_textView_headline.setText(description);
        } catch (NullPointerException e) {
            Log.d(TAG, "QRSharingFragmentActivity:" + e.getMessage());
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //Button to Move to the Calendar View
        mQRSharing_button_showInCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.qrSharing_relativLayout_calendarActivity, new CalendarActivity());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mQRSharing_relativeLayout_buttonFrame.setVisibility(View.GONE);
                mQRSharing_imageView_qrCode.setVisibility(View.GONE);
                mQRSharing_reativeLayout_textViewFrame.setVisibility(View.GONE);
                mQRSharing_relativeLayout_calendarActivity.setVisibility(View.VISIBLE);


            }
        });





        mQRSharing_button_shareWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    File imagePath = new File(getContext().getCacheDir(), "Horario");
                    File newFile = new File(imagePath, "Horario.png");
                    Uri contentUri = FileProvider.getUriForFile(getContext(), "hft.wiinf.de.horario.fileprovider", newFile);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.setType("image/jpeg");
                    getContext().startActivity(Intent.createChooser(shareIntent, "Teilen via..."));

                }catch (NullPointerException e) {
                    e.printStackTrace();}


            }
        });


    }
}
