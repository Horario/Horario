package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hft.wiinf.de.horario.R;

public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        FragmentTransaction fr = getFragmentManager().beginTransaction();
        //settings_relativeLayout_helper: in this Layout all other layouts will be uploaded
        fr.replace(R.id.calendar_frameLayout, new CalendarFragment(), "Calendar");
        fr.commit();

        return view;
    }
}