package hft.wiinf.de.horario.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import java.io.FileOutputStream;
import java.io.IOException;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.controller.PersonController;

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
    private Person mPerson;

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
        mQRSharing_relativeLayout_buttonFrame = view.findViewById(R.id.qrSharing_relativeLayout_buttonFrame);
        mQRSharing_reativeLayout_textViewFrame = view.findViewById(R.id.qrSharing_relativeLayout_textViewFrame);
        mQRSharing_relativeLayout_calendarActivity = view.findViewById(R.id.qrSharing_relativeLayout_calendarActivity);
        mQRSharing_textView_headline = view.findViewById(R.id.qrSharing_textView_headline);
        mQRSharing_textView_description = view.findViewById(R.id.qrSharing_textView_description);
        mQRSharing_button_shareWith = view.findViewById(R.id.qrSharing_button_shareWith);
        mQRSharing_button_showInCalendar = view.findViewById(R.id.qrSharing_button_showInCalendar);
        mQRSharing_imageView_qrCode = view.findViewById(R.id.qrSharing_imageView_qrCode);

        //Start the QR Code GeneratorMethod and Show all Even Informations
        qrBitMapGenerator();

        //Put StringBufffer in an Array an split the Values to new String Variables
        //Index: 0 = StartDate; 1 = StartTime; 2= EndTime; 3=Descriptoin; 4=Location; 5=EventCreator
        try {
            String[] eventStringBufferResultAsArray = eventStringResultDescription().split("\\| ");
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
            String startDate = eventStringBufferResultAsArray[1].trim();
            String endDate = eventStringBufferResultAsArray[2].trim();
            String startTime = eventStringBufferResultAsArray[3].trim();
            String endTime = eventStringBufferResultAsArray[4].trim();
            String repetition = eventStringBufferResultAsArray[5].toUpperCase().trim();
            String shortTitle = eventStringBufferResultAsArray[6].trim();
            String place = eventStringBufferResultAsArray[7].trim();
            String eventCreatorName = eventStringBufferResultAsArray[9].trim();


            mQRSharing_textView_description.setText(startDate + "\n" + place + "\n" + eventCreatorName);

            // Change the DataBase Repetition Information in a German String for the Repetition Element
            // like "Daily" into "täglich" and so on
            switch (repetition) {
                case "YEARLY":
                    repetition = "jährlich";
                    break;
                case "MONTHLY":
                    repetition = "monatlich";
                    break;
                case "WEEKLY":
                    repetition = "wöchentlich";
                    break;
                case "DAILY":
                    repetition = "täglich";
                    break;
                case "NONE":
                    repetition = "";
                    break;
                default:
                    repetition = "ohne Wiederholung";
            }

            // Check the EventCreatorName and is it itself Change the eventCreaterName to "Your Self"
            mPerson = PersonController.getPersonWhoIam();
            if(eventCreatorName.equals(mPerson.getName())) {
                eventCreatorName = "Du selber";
            }else{

            }

            // Event shortTitel in Headline with StartDate
            mQRSharing_textView_headline.setText(shortTitle);

            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                mQRSharing_textView_description.setText(startDate+ "\n" + "Raum: "+place + "\n"
                        +"Organisator: " +eventCreatorName);
            } else {
                mQRSharing_textView_description.setText(startDate+ " - " +endDate+"\n"
                        +repetition+ "\n" + place +"\n"+"Organisator: "+ eventCreatorName);
            }

        } catch (NullPointerException e) {
            Log.d(TAG, "QRSharingFragmentActivity:" + e.getMessage());
            mQRSharing_button_showInCalendar.setVisibility(View.GONE);
            mQRSharing_button_shareWith.setVisibility(View.GONE);
            mQRSharing_textView_headline.setVisibility(View.GONE);
            mQRSharing_textView_description.setVisibility(View.GONE);
            mQRSharing_imageView_qrCode.setVisibility(View.GONE);

            Snackbar.make(getActivity().findViewById(R.id.generator_button_frame),
                    "Ups! Fehler Aufgetreten!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mQRSharing_relativeLayout_buttonFrame.setVisibility(View.GONE);
                    mQRSharing_relativeLayout_calendarActivity.setVisibility(View.VISIBLE);
                }
            }).show();

        } catch (ArrayIndexOutOfBoundsException z){
            //If there an Exeption the Views are Invisible and Snackbar tell that's anything wrong
            // and Push him back to the CalendarActivity
            Log.d(TAG, "QRGeneratorFragmentActivity:" + z.getMessage());
            mQRSharing_button_showInCalendar.setVisibility(View.GONE);
            mQRSharing_button_shareWith.setVisibility(View.GONE);
            mQRSharing_textView_headline.setVisibility(View.GONE);
            mQRSharing_textView_description.setVisibility(View.GONE);
            mQRSharing_imageView_qrCode.setVisibility(View.GONE);
            mQRSharing_textView_description.setText("Das ist der Inhalt vom QR Code: "+"\n"+eventStringResultDescription()+
                    "\n"+"Das können wir leider nicht als Termin speichern!");

            Snackbar.make(getActivity().findViewById(R.id.generator_button_frame),
                    "Ups! Falscher QR-Code!",
                    Snackbar.LENGTH_INDEFINITE).setAction("Zum Kalender", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.generator_realtivLayout_show_qrSharingFragment, new CalendarActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    mQRSharing_relativeLayout_buttonFrame.setVisibility(View.GONE);
                    mQRSharing_relativeLayout_calendarActivity.setVisibility(View.VISIBLE);
                }
            }).show();
        }


        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //Button to Move to the Calendar View
        mQRSharing_button_showInCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.qrSharing_relativeLayout_calendarActivity, new CalendarActivity());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mQRSharing_relativeLayout_buttonFrame.setVisibility(View.GONE);
                mQRSharing_imageView_qrCode.setVisibility(View.GONE);
                mQRSharing_reativeLayout_textViewFrame.setVisibility(View.GONE);
                mQRSharing_relativeLayout_calendarActivity.setVisibility(View.VISIBLE);
            }
        });

        //Open a Chooser to Share the QR-Code over one of the User Apps
        mQRSharing_button_shareWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File cachePath = new File(getContext().getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    mBitmapOfQRCode.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    File imagePath = new File(getContext().getCacheDir(), "images");
                    File newFile = new File(imagePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(getContext(), "hft.wiinf.de.horario.fileprovider", newFile);

                    if (contentUri != null) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                        shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(Intent.createChooser(shareIntent, "Teilen via ... "));
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
