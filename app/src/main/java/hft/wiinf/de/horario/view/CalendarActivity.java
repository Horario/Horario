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
import hft.wiinf.de.horario.R;


//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";
    private RelativeLayout rLayout_fragment, rLayout_main;


    //Temp Method to Change on Frame CaledarActivity with QRScannerActivity Fragments
    //ToDo Diese Methode muss später auf den Floatingbutten gebunden werden dazu brauch es auch eine Anpassung der XML stattfinden. -> Es muss ein neuer Container erstellt werden in den dann die das Fragment geladen wird. Zielsetzung bis ende der Weoche sollte das gehen!
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_calendar, container, false);
        Button scnbtn = (Button) v.findViewById(R.id.calendar_temp_button_gotoscanner);

        //Change onClick the Fragment CalendarActivity with the QRScannerActivity
        scnbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.calendar_relativeLayout_container_for_newFragment, new QRScannerActivity());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    rLayout_main.setVisibility(View.GONE);
                    rLayout_fragment.setVisibility(View.VISIBLE);
                }catch (NullPointerException e){
                Log.d(TAG, "CalendarActivity:" + e.getMessage());
            }}
        });


        return v;
    }

    //Method will be called directly after View is created
    public void onViewCreated(final View v, Bundle saveInstanceStage) {
        rLayout_fragment = (RelativeLayout) v.findViewById(R.id.calendar_relativeLayout_container_for_newFragment);
        rLayout_main = (RelativeLayout) v.findViewById(R.id.calendar_relativeLayout_main);
    }

}