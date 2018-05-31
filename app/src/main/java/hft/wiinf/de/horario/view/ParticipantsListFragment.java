package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.LazyAdapter;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;


public class ParticipantsListFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> participants;
    LazyAdapter adapter;
    TextView textViewEventData;
    int refusalCounter = 0;
    Event selectedEvent;
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
    public ParticipantsListFragment() {
        // Required empty public constructor
    }


    // Get the EventIdResultBundle (Long) from the newEventActivity to Start later a DB Request
    @SuppressLint("LongLogTag")
    public Long getEventId() {
        Bundle MYEventIdBundle = getArguments();
        Long MYEventIdLongResult = MYEventIdBundle.getLong("EventId");
        return MYEventIdLongResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);
        textViewEventData = view.findViewById(R.id.textViewEventData);
        setSelectedEvent(EventController.getEventById(getEventId()));
        ListView participantsListView = (ListView) view.findViewById(R.id.ParticipantsList);
        participants = new ArrayList<String>();
        getParticipants(getEventId());
        // Set The Adapter
        adapter = new LazyAdapter(this.getActivity(), participants);
        participantsListView.setAdapter(adapter);
        textViewEventData.setText(selectedEvent.getShortTitle() + " " + dayFormat.format(selectedEvent.getStartTime()));

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefresh.setRefreshing(true);
                        refreshConfirmationsAndCancellations();
                        swipeRefresh.setRefreshing(false);
                    }
                }
        );
        return view;
    }

    private void getParticipants(long eventId) {
/*Look into the DB and get all the participants of an event, then place them in an array with a prefix depending on the acceptance*/
        participants.clear();
        List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventById(eventId));
        for (Person personAccepted : allAcceptances) {
            String nameToSave = "Y:" + personAccepted.getName();
            participants.add(nameToSave);
        }
        List<Person> allCancellations = PersonController.getEventCancelledPersons(EventController.getEventById(eventId));
        for (Person personCancelled : allCancellations) {
            String nameToSave = "N:" + personCancelled.getName();
            participants.add(nameToSave);
        }
    }


    private void refreshConfirmationsAndCancellations() {
        Toast.makeText(getContext(), R.string.participantsUpdated, Toast.LENGTH_SHORT).show();
                getParticipants(getEventId());
                adapter.notifyDataSetChanged();

    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

}
/*Zwischenablage wegen Schreibfaulheit etc


 */