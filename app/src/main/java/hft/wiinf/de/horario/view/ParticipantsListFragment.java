package hft.wiinf.de.horario.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.LazyAdapter;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;


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
                        //refreshConfirmationsAndCancellations();
                        Toast toast = Toast.makeText(getContext(), "Funktion wird momentan gewartet", Toast.LENGTH_SHORT);
                        toast.show();
                        adapter.notifyDataSetChanged();
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
        if (checkPermissions()) {
            /*Do the update*/
            Toast toast = Toast.makeText(getContext(), R.string.notificationParticipantsBeingFetched, Toast.LENGTH_SHORT);
            toast.show();
            List<ReceivedHorarioSMS> unreadSMS;
            unreadSMS = getUnreadHorarioSMS(getContext());
            if (unreadSMS.size() > 0) {
                parseHorarioSMSAndUpdate(unreadSMS);
                getParticipants(getEventId());
                adapter.notifyDataSetChanged();
            }
        } else {
            /*Make the user accept*/
            switch (refusalCounter) {
                case 0:
                    refusalCounter++;
                    requestPermissions();
                    break;
                case 1:
                    refusalCounter++;
                    requestPermissions();
                    break;
                default:
                    Snackbar snackbar = Snackbar
                            .make(getView(), R.string.begForAcceptanceReadSMSAndContacts, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.okayIWillAccept, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermissions();
                                }
                            });

                    snackbar.show();
                    break;

            }
        }
    }

    private void parseHorarioSMSAndUpdate(List<ReceivedHorarioSMS> unreadSMS) {
        for (ReceivedHorarioSMS singleUnreadSMS : unreadSMS) {
            Person person = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
            String savedContactExisting = null;
            savedContactExisting = lookForSavedContact(singleUnreadSMS.getPhonenumber(), getContext());

            /*Replace name if saved in contacts*/
            if (savedContactExisting != null) {
                person.setName(savedContactExisting);

            }
            /*Check if acceptance or cancellation*/
            if (singleUnreadSMS.isAcceptance()) {
                person.setAcceptedEvent(EventController.getEventById(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
                PersonController.savePerson(person);
            } else {
                //cancellation: look for possible preceding acceptance. If yes, then delete person and create new. Else just save the person
                List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventById(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
                for (Person personAccepted : allAcceptances) {
                    personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
                    person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                    if (personAccepted.getPhoneNumber().equals(person.getPhoneNumber())) {
                        PersonController.deletePerson(personAccepted);
                        person.setCanceledEvent(EventController.getEventById(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
                        PersonController.savePerson(person);
                    } else {
                        person.setCanceledEvent(EventController.getEventById(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
                        PersonController.savePerson(person);
                    }
                }
            }
        }
    }


    private String lookForSavedContact(String address, Context context) {
        /*Get all the contacts, see if number is identical after "shortifying" it, if identical, replace the name*/
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((c != null ? c.getCount() : 0) > 0) {
            while (c != null && c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = shortifyPhoneNumber(phoneNo);
                        address = shortifyPhoneNumber(address);
                        if (phoneNo.equals(address)) {
                            pCur.close();
                            return name;
                        }
                    }
                    pCur.close();
                }
            }
            c.close();
        }
        if (c != null) {
            c.close();
        }
        return null;
    }

    private String shortifyPhoneNumber(String number) {
        /*Take out all the chars not being numbers and return the numbers after "1" (German mobile number!!!)*/
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("+", "");
        number = number.replace("-", "");
        number = number.replace(" ", "");
        number = number.substring(number.indexOf("1"));
        return number;
    }

    private List<ReceivedHorarioSMS> getUnreadHorarioSMS(Context context) {
        /*Get all the SMS, for each SMS, check if is in Inbox, check the date received (for knowing which ones are new), check if contains Horario prefix, then split the content*/
        ArrayList<ReceivedHorarioSMS> unreadHorarioSMS = new ArrayList<ReceivedHorarioSMS>();
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        Date lastReadDate = readLastReadDate();
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    if (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE))) == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                        if ((c.getColumnIndexOrThrow(Telephony.Sms.READ)) == 7) {
                            Date smsDate = new Date(Long.parseLong(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE))));
                            if (smsDate.after(lastReadDate)) {
                                if (c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).substring(0, 9).equals(":Horario:")) {
                                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                                    String[] parsedSMS = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)).substring(9).split(",");
                                    if (parsedSMS[1].equalsIgnoreCase("1")) {
                                        unreadHorarioSMS.add(new ReceivedHorarioSMS(number, true, Integer.parseInt(parsedSMS[0]), null, parsedSMS[2]));
                                    } else {
                                        unreadHorarioSMS.add(new ReceivedHorarioSMS(number, false, Integer.parseInt(parsedSMS[0]), parsedSMS[3], parsedSMS[2]));
                                    }
                                }
                            }
                        }
                    }
                    c.moveToNext();
                }
            }
            Long date = System.currentTimeMillis();
            c.close();
            saveReadDate(String.valueOf(date));
            if (unreadHorarioSMS.size() > 0) {
                Toast toast = Toast.makeText(getContext(), R.string.textMessagesUpdated, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getContext(), R.string.noNewTextMessages, Toast.LENGTH_SHORT);
                toast.show();
            }
            return unreadHorarioSMS;
        } else {
            Toast toast = Toast.makeText(getContext(), R.string.noTextMessagesAvailable, Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
    }

    private Date readLastReadDate() {
        FileInputStream inputStream;
        Date lastReadDate = new Date(0);
        try {
            inputStream = getContext().openFileInput("lastReadDate.txt");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            String text = null;
            while ((line = bufferedReader.readLine()) != null) {
                text = line;
            }
            if (text != null) {
                long dateInMS = Long.parseLong(text);
                lastReadDate = new Date(dateInMS);
            } else {
                lastReadDate = new Date(System.currentTimeMillis()); //theoretically never called but necessary in case.
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastReadDate;
    }

    private boolean checkPermissions() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
        int contacts = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }

    private void requestPermissions() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
        int contacts = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), 1);
        }
    }

    private void saveReadDate(String date) {
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput("lastReadDate.txt", Context.MODE_PRIVATE);
            outputStream.write(date.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }


}
/*Zwischenablage wegen Schreibfaulheit etc


 */