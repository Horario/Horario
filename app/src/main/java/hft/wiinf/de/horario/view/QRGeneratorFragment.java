package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;

public class QRGeneratorFragment extends Fragment {

    private static final String TAG = "QRGeneratorFragmentActivity";
    private TextView mQRGenerator_textView_description, mQRGenerator_textView_headline;
    private RelativeLayout mQRGenerator_realtiveLayout_textViewFrame;
    private Button mQRGenerator_button_shareWith, mQRGenerator_button_goToWhereComesFrom;
    private ImageView mQRGenerator_imageView_qrCode;
    private BitMatrix mBitmatrix;
    private Bitmap mBitmapOfQRCode;
    private Person mPerson;
    private StringBuffer mQRGenerator_StringBuffer_Result;
    private Event mEvent;

    public QRGeneratorFragment() {
        // Required empty public constructor
    }

    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
    @SuppressLint("LongLogTag")
    public Long eventIdDescription() {
        Bundle qrEventIdBundle = getArguments();
        assert qrEventIdBundle != null;
        Long qrEventIdLongResult = qrEventIdBundle.getLong("eventId");
        return qrEventIdLongResult;
    }

    // Push the User where he/she comes from
    private void goWhereUserComesFrom() {
        Bundle whichFragment = getArguments();
        Objects.requireNonNull(getFragmentManager()).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (Objects.requireNonNull(whichFragment).getString("fragment").equals("EventOverview")) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.eventOverview_frameLayout, new EventOverviewFragment(), "")
                    .commit();
        } else {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.calendar_frameLayout, new CalendarFragment(), "")
                    .commit();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrgenerator, container, false);

        //Initial GUI
        mQRGenerator_button_shareWith = view.findViewById(R.id.qrGenerator_button_shareWith);
        mQRGenerator_button_goToWhereComesFrom = view.findViewById(R.id.qrGenerator_button_goToWhereComesFrom);
        mQRGenerator_imageView_qrCode = view.findViewById(R.id.qrGenerator_imageView_qrCode);
        mQRGenerator_realtiveLayout_textViewFrame = view.findViewById(R.id.qrGenerator_relativeLayout_textViewFrame);
        mQRGenerator_textView_description = view.findViewById(R.id.qrGenerator_textView_description);
        mQRGenerator_textView_headline = view.findViewById(R.id.qrGenerator_textView_headline);
        //mQRGenerator_relativeLayout_buttonFrame = view.findViewById(R.id.qrGenerator_relativeLayout_buttonFrame);

        // Show always Scrollbar on Description TextView
        mQRGenerator_textView_description.setMovementMethod(new ScrollingMovementMethod());

        //Create Event form the DB with the EventId (eventIdResultBundle) to put it in a StringBuffer
        mEvent = EventController.getEventById(eventIdDescription());

        mPerson = PersonController.getPersonWhoIam();

        return view;
    }

    @SuppressLint("SimpleDateFormat")
    public StringBuffer stringBufferGenerator() {
        //Modify the DateFormat form den DB to get a more readable Form for Date and Time disjunct
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        //Splitting String Element is the Pipe Symbol (on the Keyboard ALT Gr + <> Button = |)
        String stringSplitSymbol = " | ";

        // Merge the Data Base Information to one Single StringBuffer with the Format:
        // CreatorID (not EventID!!), StartDate, EndDate, StartTime, EndTime, Repetition, ShortTitle
        // Place, Description, Name and PhoneNumber of EventCreator
        mQRGenerator_StringBuffer_Result = new StringBuffer();
        mQRGenerator_StringBuffer_Result.append(mEvent.getCreatorEventId()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleDateFormat.format(mEvent.getEndDate())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(simpleTimeFormat.format(mEvent.getEndTime())).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getRepetition()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getShortTitle()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getPlace()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mEvent.getDescription()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getName()).append(stringSplitSymbol);
        mQRGenerator_StringBuffer_Result.append(mPerson.getPhoneNumber());

        return mQRGenerator_StringBuffer_Result;

    }

    //Create the QR Code from StringBuffer Data and Show it as a Bitmap
    public void qrBitMapGenerator() {
        //Create a CorrectionLevelHashMap for the QRCode
        // Level of Correction: L = 7%, M = 15%, Q = 25%, H = 30% (max!)
        Map<EncodeHintType, Object> correctionLevel = new HashMap<>();
        correctionLevel.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        //Change the StringBuffer to a String for Output in the ImageView
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            mBitmatrix = multiFormatWriter.encode(String.valueOf(mQRGenerator_StringBuffer_Result),
                    BarcodeFormat.QR_CODE, 200, 200, correctionLevel);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            mBitmapOfQRCode = barcodeEncoder.createBitmap(mBitmatrix);
            mQRGenerator_imageView_qrCode.setImageBitmap(mBitmapOfQRCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        try {
            stringBufferGenerator();
            qrBitMapGenerator();

            //Put StringBuffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName; 10 = PhoneNumber
            String[] eventStringBufferArray = String.valueOf(stringBufferGenerator()).split("\\|");
            String startDate = eventStringBufferArray[1].trim();
            String endDate = eventStringBufferArray[2].trim();
            String startTime = eventStringBufferArray[3].trim();
            String endTime = eventStringBufferArray[4].trim();
            String repetition = eventStringBufferArray[5].toUpperCase().trim();
            String shortTitle = eventStringBufferArray[6].trim();
            String place = eventStringBufferArray[7].trim();
            String description = eventStringBufferArray[8].trim();
            String eventCreatorName = eventStringBufferArray[9].trim();

            // Change the DataBase Repetition Information in a German String for the Repetition Element
            // like "Daily" into "täglich" and so on
            switch (repetition) {
                case "YEARLY":
                    repetition = getString(R.string.yearly);
                    break;
                case "MONTHLY":
                    repetition = getString(R.string.monthly);
                    break;
                case "WEEKLY":
                    repetition = getString(R.string.weekly);
                    break;
                case "DAILY":
                    repetition = getString(R.string.daily);
                    break;
                case "NONE":
                    repetition = "";
                    break;
                default:
                    repetition = getString(R.string.without_repetition);
            }

            // Check the EventCreatorName and is it itself Change the eventCreatorName to "Your Self"
            if (eventCreatorName.equals(mPerson.getName())) {
                eventCreatorName = getString(R.string.yourself);
            }

            // Event shortTitle in Headline with StartDate
            mQRGenerator_textView_headline.setText(shortTitle + ", " + startDate);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
           if (repetition.equals("")) {
                mQRGenerator_textView_description.setText(getString(R.string.time)+ startTime + getString(R.string.until)
                        + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + getString(R.string.organizer) + eventCreatorName  + "\n" + "\n" + getString(R.string.eventDetails)
                        + description);
            } else {
                mQRGenerator_textView_description.setText(getString(R.string.as_of) + startDate
                        + getString(R.string.until) + endDate + "\n"+ getString(R.string.time) + startTime + getString(R.string.until)
                        + endTime + getString(R.string.clock) + "\n" + getString(R.string.place) + place + "\n" + getString(R.string.organizer) + eventCreatorName  + "\n" + "\n" + getString(R.string.eventDetails)
                        + description);
            }
            // In the CatchBlock the User see a SnackBar Information and was pushed where the User Comes From
        } catch (NullPointerException e) {
            Log.d(TAG, "QRGeneratorFragmentActivity:" + e.getMessage());
            mQRGenerator_button_goToWhereComesFrom.setVisibility(View.GONE);
            mQRGenerator_button_shareWith.setVisibility(View.GONE);
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.qrGenerator_relativeLayout_textViewFrame),
                    getString(R.string.ups_an_error),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.back), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goWhereUserComesFrom();
                }
            }).show();

        } catch (ArrayIndexOutOfBoundsException z) {
            //If there is an Exception the Views change to Invisible and SnackBar tell that's anything wrong
            // and Push him back where the User come Frome
            Log.d(TAG, "QRGeneratorFragmentActivity:" + z.getMessage());
            mQRGenerator_textView_headline.setVisibility(View.GONE);
            mQRGenerator_button_shareWith.setVisibility(View.GONE);
            mQRGenerator_button_goToWhereComesFrom.setVisibility(View.GONE);
            mQRGenerator_textView_description.setText(getString(R.string.wrongQRCodeResult) + "\n" + "\n"
                    + mQRGenerator_StringBuffer_Result + "\n" + "\n" + getString(R.string.notAsEventSaveable));

            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.qrGenerator_relativeLayout_textViewFrame),
                    getString(R.string.ups_an_error),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.back), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goWhereUserComesFrom();
                }
            }).show();
        }

        //Push the User Back where he/she comes From
        mQRGenerator_button_goToWhereComesFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goWhereUserComesFrom();
            }
        });

        //Open a Chooser to Share the QR-Code over one of the User Apps
        mQRGenerator_button_shareWith.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                try {
                    File cachePath = new File(Objects.requireNonNull(getContext()).getCacheDir(), "images");
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
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)));
                    }
                } catch (IOException e) {
                    Log.d(TAG, "QRGeneratorFragmentActivity:" + e.getMessage());
                }
            }
        });
    }
}