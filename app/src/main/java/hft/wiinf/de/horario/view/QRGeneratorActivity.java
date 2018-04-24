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
import java.text.SimpleDateFormat;
import java.util.Date;

import hft.wiinf.de.horario.R;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private ImageView mQRGenImageViewResult;
    private TextView mQRGenTextViewInput;
    private RelativeLayout mRelativeLayout_QRGenResult;
    private Button mButtonQRCreation;
    private BitMatrix mBitmatrix;
    private Bitmap mBitmapOfQRCode;
    private StringBuffer mDataBaseStringBuffer;
    private String mDataBaseStringResult;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceType")
    public void onViewCreated(final View view, Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);

        //Initial GUI
        mButtonQRCreation = view.findViewById(R.id.createQRCode);
        mQRGenImageViewResult= view.findViewById(R.id.generator_temp_imageView_result);
        mRelativeLayout_QRGenResult = view.findViewById(R.id.qr_main);
        mQRGenTextViewInput = view.findViewById(R.id.generator_temp_imput);

        //Dummydaten für die DB zum Testen des QR Code Generators ... geht noch nicht
        String place = "Labor";
        Date startDatum = new Date();
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDatum);
        Date endDatum = new Date();
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDatum);
        String description = "Experimentelle genetische Versuche mit Überresten von Hr. Albert Einstein. ";

        // Merge the Data Base Informations to One String
        mDataBaseStringBuffer = new StringBuffer();
        mDataBaseStringBuffer.append(place+"\n"+description+"\n"+startDate+"\n"+endDate);
        mDataBaseStringResult = mDataBaseStringBuffer.toString();


        //Create a QR Code and Show it in the ImageView.
        //ToDo Imput nicht fester String sondern muss aus DB kommen. Dazu müssen die Daten mit toString und StringBuffer zusammengeführt werden.
        mButtonQRCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    mBitmatrix = multiFormatWriter.encode(mDataBaseStringResult, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    mBitmapOfQRCode = barcodeEncoder.createBitmap(mBitmatrix);
                    mQRGenImageViewResult.setImageBitmap(mBitmapOfQRCode);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        mQRGenTextViewInput.setText(mDataBaseStringResult);
        return view;
    }



}
