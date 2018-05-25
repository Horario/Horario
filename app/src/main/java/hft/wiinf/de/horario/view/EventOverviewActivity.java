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
import android.widget.Toast;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.model.Event;

public class EventOverviewActivity extends Fragment {

    FloatingActionButton eventOverviewFcMenu, eventOverviewFcQrScan, eventOverviewFcNewEvent, eventOverviewFcTemporaryGetDetails;//TODO: delete last button as soon as detailed view of event exists
    RelativeLayout rLayout_eventOverview_helper;
    ConstraintLayout cLayout_eventOverview_main;
    TextView eventOverview_HiddenIsFloatingMenuOpen;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_overview, container, false);
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        //settings_relativeLayout_helper: in this Layout all other layouts will be uploaded
        fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment(), "EventOverview");
        fr.commit();
        eventOverviewFcMenu = view.findViewById(R.id.eventOverview_floatingActionButtonMenu);
        eventOverviewFcNewEvent = view.findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
        eventOverviewFcQrScan = view.findViewById(R.id.eventOverview_floatingActionButtonScan);
        eventOverviewFcTemporaryGetDetails = view.findViewById(R.id.eventOverview_floatingActionButtonTemporaryGetParticipants);//TODO: delete as soon as detailed view of event exists
        rLayout_eventOverview_helper = view.findViewById(R.id.eventOverview_relativeLayout_helper);
        cLayout_eventOverview_main = view.findViewById(R.id.eventOverview_Layout_main);
        eventOverview_HiddenIsFloatingMenuOpen = view.findViewById(R.id.eventOverviewFabClosed);

        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        eventOverviewFcTemporaryGetDetails.hide();

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
                fr.replace(R.id.eventOverview_relativeLayout_helper, new NewEventFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                closeFABMenu();
                eventOverviewFcMenu.setVisibility(View.GONE);
            }
        });

        //Open new Fragment "QRCodeScan"
        eventOverviewFcQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_relativeLayout_helper, new QRScanFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                closeFABMenu();
                eventOverviewFcMenu.setVisibility(View.GONE);
            }
        });
        //TODO: change Listener as soon as detailed view of event exists
        /*Use OnItemClickListener and get the CreatorEventId in question

        Then, depending on the AcceptedState and event.creator.isItMe == true
        open the right Fragment with the right long variable in the bundle
         */

        eventOverviewFcTemporaryGetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randomFragmentNumber = (int) Math.floor(Math.random() * 3) + 1;
                switch (randomFragmentNumber) {
                    case 1://AcceptedEventDetails (ACCEPTED)
                        AcceptedEventDetailsFragment AEDFrag = new AcceptedEventDetailsFragment();
                        Bundle bundleAED = new Bundle();
                        bundleAED.putLong("creatorEventId", 1);
                        AEDFrag.setArguments(bundleAED);
                        FragmentTransaction fr1 = getFragmentManager().beginTransaction();
                        fr1.replace(R.id.eventOverview_relativeLayout_helper,AEDFrag);
                        fr1.addToBackStack(null);
                        fr1.commit();
                        rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                        closeFABMenu();
                        eventOverviewFcMenu.setVisibility(View.GONE);
                        break;
                    case 2://SavedEventDetails (WAITING)
                        SavedEventDetailsFragment SEDFrag = new SavedEventDetailsFragment();
                        Bundle bundleSED = new Bundle();
                        bundleSED.putLong("creatorEventId", 1);
                        SEDFrag.setArguments(bundleSED);
                        FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                        fr2.replace(R.id.eventOverview_relativeLayout_helper,SEDFrag);
                        fr2.addToBackStack(null);
                        fr2.commit();
                        rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                        closeFABMenu();
                        eventOverviewFcMenu.setVisibility(View.GONE);
                        break;
                    case 3://MyOwnEventDetails (ACCEPTED && event.creator.isItMe == true)
                        MyOwnEventDetailsFragment MYFrag = new MyOwnEventDetailsFragment();
                        Bundle bundleMY = new Bundle();
                        bundleMY.putLong("creatorEventId", 1);
                        MYFrag.setArguments(bundleMY);
                        FragmentTransaction fr3 = getFragmentManager().beginTransaction();
                        fr3.replace(R.id.eventOverview_relativeLayout_helper,MYFrag);
                        fr3.addToBackStack(null);
                        fr3.commit();
                        rLayout_eventOverview_helper.setVisibility(View.VISIBLE);
                        closeFABMenu();
                        eventOverviewFcMenu.setVisibility(View.GONE);
                        break;
                    default:
                        break;

                }

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
        eventOverviewFcTemporaryGetDetails.show();//TODO: delete as soon as detailed view of event exists
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black_24dp);
    }

    //Hide the menu Buttons
    public void closeFABMenu() {
        eventOverview_HiddenIsFloatingMenuOpen.setText("false");
        eventOverviewFcQrScan.hide();
        eventOverviewFcNewEvent.hide();
        eventOverviewFcTemporaryGetDetails.hide();//TODO: delete as soon as detailed view of event exists
        eventOverviewFcMenu.setImageResource(R.drawable.ic_android_black2_24dp);
    }


}
