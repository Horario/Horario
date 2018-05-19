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

    FloatingActionButton eventOverviewFcMenu, eventOverviewFcQrScan, eventOverviewFcNewEvent;
    ConstraintLayout cLayout_eventOverview_main;
    TextView eventOverview_HiddenIsFloatingMenuOpen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_overview, container, false);

        eventOverviewFcMenu = view.findViewById(R.id.eventOverview_floatingActionButtonMenu);
        eventOverviewFcNewEvent = view.findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
        eventOverviewFcQrScan = view.findViewById(R.id.eventOverview_floatingActionButtonScan);
        cLayout_eventOverview_main = view.findViewById(R.id.eventOverview_Layout_main);
        eventOverview_HiddenIsFloatingMenuOpen = view.findViewById(R.id.eventOverviewFabClosed);

        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();

        eventOverviewFcMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventOverview_HiddenIsFloatingMenuOpen.getText().equals("false")) {
                    showFABMenu();
                    eventOverview_HiddenIsFloatingMenuOpen.setText("true");
                } else {
                    closeFABMenu();
                    eventOverview_HiddenIsFloatingMenuOpen.setText("false");
                }
            }
        });

        //Open new Fragment "NewEvent"
        eventOverviewFcNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_Layout_main, new NewEventFragment(), "NewEvent");
                fr.addToBackStack("NewEvent");
                fr.commit();
                closeFABMenu();
            }
        });

        //Open new Fragment "QRCodeScan"
        eventOverviewFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_Layout_main, new QRScanFragment(), "QrScan");
                fr.addToBackStack("QrScan");
                fr.commit();
                closeFABMenu();
            }
        });

        cLayout_eventOverview_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        return view;
    }

    //Show the menu Buttons
    public void showFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("true");
        eventOverviewFcQrScan.show();
        eventOverviewFcNewEvent.show();
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black_24dp);
    }

    //Hide the menu Buttons
    public void closeFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("false");
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black2_24dp);
    }
}
