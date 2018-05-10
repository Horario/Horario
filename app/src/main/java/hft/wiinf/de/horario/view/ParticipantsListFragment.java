package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

public class ParticipantsListFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    TelephonyProvider telephonyProvider = new TelephonyProvider(getContext());
    ContactsProvider contactsProvider = new ContactsProvider(getContext());

    public ParticipantsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        String [] dummyParticipants = {
                "Lisa Müller",
                "Benjamin Hück",
                "Lucas Toulon",
                "Florian Rietz",
                "Daniel Zeller",
                "Dennis Rößner",
                "Tanja Fraenz",
                "Christine Weissenberger",
                "Melanie Strauss",
                "Frank Garbe",
                "Henri Unruh",
                "Benedikt Burger",
                "Mario Hermann",
                "Mariam Baramidze"
        };


        List  <String> participantsList = new ArrayList<>(Arrays.asList(dummyParticipants));

        ArrayAdapter<String> participantsListAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.fragment_participants_list, R.id.ParticipantsList, participantsList);


        ListView ParticipantsList = (ListView) view.findViewById(R.id.ParticipantsList);
        ParticipantsList.setAdapter(participantsListAdapter);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Toast toast = Toast.makeText(getContext(), R.string.notificationParticipantsBeingFetched, Toast.LENGTH_SHORT);
                        toast.show();
                        swipeRefresh.setRefreshing(true);
                        refreshConfirmationsAndCancellations();
                        swipeRefresh.setRefreshing(false);
                    }
                }
        );


        return view;
    }

    private void refreshConfirmationsAndCancellations() {
        List<Sms> unreadSMS;
        unreadSMS = getUnreadHorarioSMS();
        parseHorarioSMSAndUpdate(unreadSMS);
        //TODO: update der ListView
    }

    private void parseHorarioSMSAndUpdate(List<Sms> unreadSMS) {
        for (Sms sms : unreadSMS) {
            String[] parsedSMS = sms.body.substring(9).split(",");
            Person person = new Person(sms.address, "");
            String savedContactExisting = null;
            savedContactExisting = lookForSavedContact(sms.address);


            /*Replace name if saved in contacts*/
            if (savedContactExisting == null) {
                person.setName(parsedSMS[2]);
            } else {
                person.setName(savedContactExisting);
            }

            /*Check if acceptance or cancellation*/
            if (parsedSMS[1].equals("1")) {
                person.setAcceptedEvent(EventController.getEventByCreatorEventId(Long.valueOf(parsedSMS[0])));
                PersonController.savePerson(person);
            }else{
                //cancellation: look for possible preceding acceptance. If yes, then change Accepted/Cancelled Event of concerned person. Else just save the person
                List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventByCreatorEventId(Long.valueOf(parsedSMS[0])));
                for (Person personAccepted : allAcceptances){
                    if (personAccepted.getPhoneNumber().substring(personAccepted.getPhoneNumber().indexOf("1")).equals(person.getPhoneNumber().substring(person.getPhoneNumber().indexOf("1")))){
                        personAccepted.setAcceptedEvent(null);
                        personAccepted.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(parsedSMS[0])));
                    }else{
                        person.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(parsedSMS[0])));
                        PersonController.savePerson(person);
                    }
                }
            }
        }
    }

    private String lookForSavedContact(String address) {
        List<Contact> contacts = contactsProvider.getContacts().getList();
        for (Contact contact : contacts) {
            if (contact.phone.substring(contact.phone.indexOf("1")).equals(address.substring(address.indexOf("1")))) {
                return contact.displayName;
            }
        }
        return null;
    }

    private List<Sms> getUnreadHorarioSMS() {
        List<Sms> allInboxSMS = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();
        ArrayList<Sms> unreadHorarioSMS = new ArrayList<Sms>();
        for (Sms sms : allInboxSMS) {
            if (sms.read || !sms.body.substring(0, 8).equals(":Horario:")) {
                //do nothing, keep going
                //TODO: maybe optimize method in the following way: iterate over sms until there is no unread sms left. (getSMS is probably from newest to oldest)
            } else {
                unreadHorarioSMS.add(sms);
            }
        }
        allInboxSMS.clear();
        return unreadHorarioSMS;
    }

}
