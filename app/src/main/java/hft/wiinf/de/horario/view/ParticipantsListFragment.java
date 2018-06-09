package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.zip.Inflater;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;


public class ParticipantsListFragment extends Fragment {
    public static String TAG = "ParticipantsListFragment";
    SwipeRefreshLayout swipeRefresh;
    TextView textViewEventData;
    static ListView participantsListView;
    static Context context = null;
    static Event selectedEvent;
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
        context = this.getActivity();
        textViewEventData = view.findViewById(R.id.textViewEventData);
        setSelectedEvent(EventController.getEventById(getEventId()));
        participantsListView = (ListView) view.findViewById(R.id.ParticipantsList);
        textViewEventData.setText(selectedEvent.getShortTitle() + " " + dayFormat.format(selectedEvent.getStartTime()));
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        update();


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
        participantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Participant participant = (Participant) parent.getItemAtPosition(position);
                if (participant.getExcuse().equals("NOEXCUSE")) {
                    //do nothing, person has accepted
                } else {
                    //get rejection reason data, then show pop up
                    String[] reasonData = participant.getExcuse().split("!");
                    String reason = participant.getName().substring(2) + " hat wegen " + reasonData[0] + " abgesagt";
                    String note = "Notiz zur Absage: " + reasonData[1];
                    //create popup with reason and note.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(reason);
                    builder.setMessage(note);
                    // Add the button
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    builder.create();
                    builder.show();

                }
            }
        });
        return view;
    }

    public void update() {
        participantsListView.setAdapter(iterateOverParticipants(selectedEvent));
    }

    private void refreshConfirmationsAndCancellations() {
        update();
        Toast.makeText(getContext(), R.string.participantsUpdated, Toast.LENGTH_SHORT).show();
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public ArrayAdapter iterateOverParticipants(Event event) {
        final ArrayList<Participant> participantsArray = new ArrayList<>();
        /*Look into the DB and get all the participants of an event, then place them in an array with a prefix depending on the acceptance*/
        participantsArray.clear();
        List<Person> allAcceptances = PersonController.getEventAcceptedPersons(event);
        for (Person personAccepted : allAcceptances) {
            String nameToSave = "Y:" + personAccepted.getName();
            participantsArray.add(new Participant(nameToSave));
        }
        List<Person> allCancellations = PersonController.getEventCancelledPersons(event);
        for (Person personCancelled : allCancellations) {
            String nameToSave = "N:" + personCancelled.getName();
            participantsArray.add(new Participant(nameToSave, personCancelled.getRejectionReason()));
        }

        final ArrayAdapter adapter = new ArrayAdapter(context, R.layout.list_row, participantsArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater mInflater = getLayoutInflater();
                View item = mInflater.inflate(R.layout.list_row, null);
                TextView participant = (TextView) item.findViewById(R.id.participantName); // participant
                ImageView presenceIndicator = (ImageView) item.findViewById(R.id.presenceIndicator); //redOrGreenLight
                String participantRow;
                participantRow = participantsArray.get(position).getName();
                // Setting all values in listview
                if (participantRow.charAt(0) == 'Y') {
                    participant.setText(participantRow.substring(2));
                    presenceIndicator.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
                } else {
                    participant.setText(participantRow.substring(2));
                    presenceIndicator.setImageResource(R.drawable.ic_fiber_manual_record_red_24dp);
                }

                return item;
            }
        };
        return adapter;
    }
}

class Participant {
    private String name;
    private String excuse;

    Participant(String name, String excuse) {
        this.name = name;
        this.excuse = excuse;
    }

    Participant(String name) {
        this.name = name;
        this.excuse = "NOEXCUSE";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExcuse() {
        return excuse;
    }

    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }
}
/*Zwischenablage wegen Schreibfaulheit etc
 */