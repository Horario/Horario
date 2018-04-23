package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.model.Event;

public class QRGeneratorActivity extends Fragment {
    private static final String TAG = "QRGeneratorFragmentActivity";
    String test;
    private ImageView mQRGenImageViewResult;
    private TextView mQRGenTextViewInput;
    private RelativeLayout mRelativeLayout_QRGenResult;
    private Event mEvent;

    public QRGeneratorActivity() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrgenerator, container, false);


        mEvent = new Event();
        //mEvent

        test = "Hallo es geht mir gut!";
    mQRGenImageViewResult= view.findViewById(R.id.generator_temp_imageView_result);
    mRelativeLayout_QRGenResult = view.findViewById(R.id.qr_main);
    mQRGenTextViewInput = view.findViewById(R.id.generator_temp_imput);

    mQRGenTextViewInput.setText(test);
        return view;
    }



}
