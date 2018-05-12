package hft.wiinf.de.horario.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;


public class ParticipantsListFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> participants;
    ArrayAdapter<String> arrayAdapter;

    public ParticipantsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        ListView participantsListView = (ListView) view.findViewById(R.id.ParticipantsList);

        participants = new ArrayList<String>();
        getParticipants();

        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, participants);
        // Set The Adapter
        participantsListView.setAdapter(arrayAdapter);

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

    private void getParticipants() {
        //TODO: replace method body with functional code

        participants.add("Lucas Toulon");
        participants.add("Florian Rietz");
        participants.add("Daniel Zeller");
        participants.add("Dennis Rößner");
        participants.add("Tanja Fraenz");
        participants.add("Christine Weissenberger");
        participants.add("Melanie Strauss");
        participants.add("Frank Garbe");
        participants.add("Henri Unruh");
        participants.add("Benedikt Burger");
        participants.add("Mario Hermann");
        participants.add("Mariam Baramidze");
    }


    private void refreshConfirmationsAndCancellations() {
        if (checkAndRequestPermissions()) {
            List<ReceivedHorarioSMS> unreadSMS;
            unreadSMS = getUnreadHorarioSMS(getContext());
//        if (unreadSMS.size() > 0) {
//            parseHorarioSMSAndUpdate(unreadSMS);
//            arrayAdapter.notifyDataSetChanged();
            //       }
        }
    }

//    private void parseHorarioSMSAndUpdate(List<ReceivedHorarioSMS> unreadSMS) {
//
//            for (ReceivedHorarioSMS singleUnreadSMS : unreadSMS) {
//                Person person = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
//                String savedContactExisting = null;
//                savedContactExisting = lookForSavedContact(singleUnreadSMS.getPhonenumber(), getContext());
//
//
//                /*Replace name if saved in contacts*/
//                if (savedContactExisting != null) {
//                    person.setName(savedContactExisting);
//                }
//
//                /*Check if acceptance or cancellation*/
//                if (singleUnreadSMS.isAcceptance()) {
//                    person.setAcceptedEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                    PersonController.savePerson(person);
//                } else {
//                    //cancellation: look for possible preceding acceptance. If yes, then delete person and create new. Else just save the person
//                    List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                    for (Person personAccepted : allAcceptances) {
//                        if (personAccepted.getPhoneNumber().substring(personAccepted.getPhoneNumber().indexOf("1")).equals(person.getPhoneNumber().substring(person.getPhoneNumber().indexOf("1")))) {
//                            PersonController.deletePerson(personAccepted);
//                            person.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                            PersonController.savePerson(person);
//                        } else {
//                            person.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                            PersonController.savePerson(person);
//                        }
//                    }
//                }
//            }
//        }


//    private String lookForSavedContact(String address, Context context) {
//        ContentResolver cr = context.getContentResolver();
//        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        if ((c != null ? c.getCount() : 0) > 0) {
//            while (c != null && c.moveToNext()) {
//                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        if (phoneNo.substring(phoneNo.indexOf("1")).equals(address.substring(address.indexOf("1")))) {
//                            pCur.close();
//                            return name;
//                        }
//                    }
//                    pCur.close();
//                }
//            }
//            c.close();
//        }
//        if (c != null) {
//            c.close();
//        }
//        return null;
//    }

    private List<ReceivedHorarioSMS> getUnreadHorarioSMS(Context context) {
        ArrayList<ReceivedHorarioSMS> unreadHorarioSMS = new ArrayList<ReceivedHorarioSMS>();
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            Toast toast = Toast.makeText(getContext(), c.getCount() + " neue Nachrichten", Toast.LENGTH_SHORT);
            toast.show();
//            if (c.moveToFirst()) {
//                for (int j = 0; j < totalSMS; j++) {
//                    if (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE))) == Telephony.Sms.MESSAGE_TYPE_INBOX) {
//                        if (!getString(c.getColumnIndexOrThrow(Telephony.Sms.READ)).equalsIgnoreCase("read")) {
//                            if (c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).substring(0, 8).equals(":Horario:")) {
//                                String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
//                                String[] parsedSMS = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).substring(9).split(",");
//                                if (parsedSMS[1].equalsIgnoreCase("1")) {
//                                    unreadHorarioSMS.add(new ReceivedHorarioSMS(number, true, Integer.parseInt(parsedSMS[0]), null, parsedSMS[2]));
//                                } else {
//                                    unreadHorarioSMS.add(new ReceivedHorarioSMS(number, false, Integer.parseInt(parsedSMS[0]), parsedSMS[3], parsedSMS[2]));
//                                }
//                            }
//                        }
//                    }
//                    c.moveToNext();
//                }
//            }

            c.close();
            return null;

        } else {
            Toast toast = Toast.makeText(getContext(), R.string.noNewTextMessages, Toast.LENGTH_SHORT);
            toast.show();
        }
        return unreadHorarioSMS;
    }

    private boolean checkAndRequestPermissions() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

}