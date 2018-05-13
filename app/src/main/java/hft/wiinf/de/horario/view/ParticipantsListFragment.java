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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;

import static android.content.Context.MODE_PRIVATE;


public class ParticipantsListFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> participants;
    ArrayAdapter<String> arrayAdapter;
    //  private static final String DATE_STORAGE_FILE = "lastReadDate.txt";

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
        //TODO: replace this line with the other one
        getParticipants();
        // getParticipants(creatorEventId);

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
//TODO: add parameter: (long creatorEventId)
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

        //following block is the official code, doesnt work yet because of the missing events
        //and missing parameter for fragment : creatorEventId

//        List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventByCreatorEventId(creatorEventId));
//        for (Person personAccepted : allAcceptances) {
//            participants.add(personAccepted.getName());
//        }
    }


    private void refreshConfirmationsAndCancellations() {
        if (checkAndRequestPermissions()) {
            List<ReceivedHorarioSMS> unreadSMS;
            unreadSMS = getUnreadHorarioSMS(getContext());
            if (unreadSMS.size() > 0) {
                parseHorarioSMSAndUpdate(unreadSMS);
                arrayAdapter.notifyDataSetChanged();
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
            //TODO: uncomment as soon as Event DB is filled is available
//            /*Check if acceptance or cancellation*/
//            if (singleUnreadSMS.isAcceptance()) {
//                person.setAcceptedEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                PersonController.savePerson(person);
//            } else {
//                //cancellation: look for possible preceding acceptance. If yes, then delete person and create new. Else just save the person
//                List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                for (Person personAccepted : allAcceptances) {
//                    personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
//                    person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
//                    if (personAccepted.getPhoneNumber().equals(person.getPhoneNumber())) {
//                        PersonController.deletePerson(personAccepted);
//                        person.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                        PersonController.savePerson(person);
//                    } else {
//                        person.setCanceledEvent(EventController.getEventByCreatorEventId(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
//                        PersonController.savePerson(person);
//                    }
//                }
//            }
        }
    }


    private String lookForSavedContact(String address, Context context) {
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
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("+", "");
        number = number.replace("-", "");
        number = number.replace(" ", "");
        number = number.substring(number.indexOf("1"));
        return number;
    }

    private List<ReceivedHorarioSMS> getUnreadHorarioSMS(Context context) {
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
                lastReadDate = new Date(0);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastReadDate;
    }

    private boolean checkAndRequestPermissions() {
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
            return false;
        }
        return true;
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


}
/*Zwischenablage wegen Schreibfaulheit etc

 */