package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hft.wiinf.de.horario.R;

public class EventOverviewActivity extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_overview, container, false);

        FragmentTransaction fr = getFragmentManager().beginTransaction();
        //settings_relativeLayout_helper: in this Layout all other layouts will be uploaded
        fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment(),"EventOverview");
        fr.commit();

        return view;
    }
}
