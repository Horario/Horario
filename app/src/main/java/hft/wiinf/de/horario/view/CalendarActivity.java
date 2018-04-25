package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hft.wiinf.de.horario.R;

//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";
    RelativeLayout newFragment_relativLayout, calendar_relativeLayout_main;
    TextView calendarHeadline_textView;

    //Temp Method to Change on Frame CaledarActivity with QRScannerActivity Fragments
    //ToDo Diese Methode muss später auf den Floatingbutten gebunden werden dazu brauch es auch eine Anpassung der XML stattfinden. -> Es muss ein neuer Container erstellt werden in den dann die das Fragment geladen wird. Zielsetzung bis ende der Weoche sollte das gehen!
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        //TestButtons
        Button scnbtn = view.findViewById(R.id.gotoscanner);
        Button genbtn = view.findViewById(R.id.gotogenerator);
        //Change onClick the Fragment CalendarActivity with the QRScannerActivity
        scnbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new QRScannerActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    calendar_relativeLayout_main.setVisibility(View.GONE);
                    calendarHeadline_textView.setVisibility(View.GONE);
                    newFragment_relativLayout.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Log.d(TAG, "CalendarActivity:" + e.getMessage());
                }
            }
        });

        genbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.newFragment, new QRGeneratorActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    calendar_relativeLayout_main.setVisibility(View.GONE);
                    calendarHeadline_textView.setVisibility(View.GONE);
                    newFragment_relativLayout.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Log.d(TAG, "CalendarActivity:" + e.getMessage());
                }
            }
        });

        return view;
    }
    //Method will be called directly after View is created
    public void onViewCreated(final View view, Bundle saveInstanceStage) {
        newFragment_relativLayout = view.findViewById(R.id.newFragment);
        calendar_relativeLayout_main = view.findViewById(R.id.calendar_relativeLayout_main);
        calendarHeadline_textView = view.findViewById(R.id.calendarheadline_textview);
    }
}
