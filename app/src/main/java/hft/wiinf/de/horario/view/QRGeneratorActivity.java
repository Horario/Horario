package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import hft.wiinf.de.horario.R;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    private ImageView mQRGenImageViewResult;
    private TextView mQRGenTextViewInput;
    private RelativeLayout mRelativeLayout_QRGenResult;
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

        //Dummydaten für die DB zum Testen des QR Code Generators ... geht noch nicht

        Calendar endZeit = Calendar.getInstance();
        endZeit.add(Calendar.HOUR, 1);
        Calendar startZeit = Calendar.getInstance();
        String raum = "Aula";

        mQRGenImageViewResult= view.findViewById(R.id.generator_temp_imageView_result);
        mRelativeLayout_QRGenResult = view.findViewById(R.id.qr_main);
        mQRGenTextViewInput = view.findViewById(R.id.generator_temp_imput);

        mQRGenTextViewInput.setText(raum);

        return view;
    }



}
