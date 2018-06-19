package hft.wiinf.de.horario.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;

/**
 * The type Sms receiver extends a {@link BroadcastReceiver} and reacts each time the phone of the user receives an SMS.
 */
public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    /**
     * Instantiates a new Sms receiver.
     */
    public SmsReceiver() {
    }

    /**
     * Checks if the SMS in question is relevant for the app and continues working on it.
     *
     * @param context, the {@link Context}
     * @param intent,  the {@link Intent}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
        SmsMessage[] receivedSMSArray = null;
        ArrayList<ReceivedHorarioSMS> unreadHorarioSMS = new ArrayList<ReceivedHorarioSMS>();
        String isSMSValidAndParseable = null;
        if (bundle != null) {

            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            receivedSMSArray = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < receivedSMSArray.length; i++) {
                // Convert Object array
                receivedSMSArray[i] = SmsMessage.createFromPdu((byte[]) pdus[i], "3gpp");
            }
            for (int i = 0; i < receivedSMSArray.length; i++) {
                /*collect all the Horario SMS*/
                if (receivedSMSArray[i].getMessageBody().toString().substring(0, 9).equals(":Horario:")) {

                    String number = (receivedSMSArray[i].getOriginatingAddress());
                    String[] parsedSMS = receivedSMSArray[i].getMessageBody().toString().substring(9).split(",");
                    if (!checkForRegexOk(parsedSMS)) {
                        Log.d("Corrupt SMS Occurence!", receivedSMSArray[i].getMessageBody().toString());
                        isSMSValidAndParseable = "No";
                        break;
                    }
                    if (parsedSMS[1].equalsIgnoreCase("1")) {
                        if (parsedSMS.length == 3) {
                            unreadHorarioSMS.add(new ReceivedHorarioSMS(number, true, Integer.parseInt(parsedSMS[0]), null, parsedSMS[2]));
                        }
                    } else {
                        if (parsedSMS.length == 4) {
                            unreadHorarioSMS.add(new ReceivedHorarioSMS(number, false, Integer.parseInt(parsedSMS[0]), parsedSMS[3], parsedSMS[2]));
                        }
                    }
                    isSMSValidAndParseable = "Yes";
                }
            }
            if (isSMSValidAndParseable != null && isSMSValidAndParseable.equals("Yes")) {
                parseHorarioSMSAndUpdate(unreadHorarioSMS, context);
            }

        }
    }

    /**
     * Takes the parameter and checks for eventual SQL Injections and other syntax problems relevant for the functionality of the app.
     *
     * @param smsTextSplitted, an {@link java.util.Arrays} of {@link String}
     * @return {@code true} if the SMS in question is valid and ready for the next method.
     */
    private boolean checkForRegexOk(String[] smsTextSplitted) {
        // RegEx: NO SQL Injections allowed PLUS check if SMS is valid
        // smsTextSplitted[0]= CreatorEventId, should be only number greater than 0
        // smsTextSplitted[1]= boolean for acceptance, should be only 0 or 1
        // smsTextSplitted[2]= String for name, only Chars and points
        // smsTextSplitted[3]= Excuse asString, needs to be splitted again by "!" and checked on two strings
        if (smsTextSplitted.length == 3 || smsTextSplitted.length == 4) {
            boolean isAcceptance = true;
            if (smsTextSplitted.length == 3) {
                isAcceptance = true;
            } else {
                isAcceptance = false;
            }
            //Make Patterns
            Pattern pattern_onlyGreatherThan0 = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern pattern_only0Or1 = Pattern.compile("(0|1)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern pattern_onlyAlphanumsAndPointsAndWhitespaces = Pattern.compile("(\\w|\\s|\\.)*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            //Make the matchers
            Matcher m_pattern_onlyGreatherThan0 = pattern_onlyGreatherThan0.matcher(smsTextSplitted[0]);
            Matcher m_pattern_only0Or1 = pattern_only0Or1.matcher(smsTextSplitted[1]);
            Matcher m_pattern_onlyAlphanumsAndPointsAndWhitespaces = pattern_onlyAlphanumsAndPointsAndWhitespaces.matcher(smsTextSplitted[2]);
            Matcher m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionReason = null;
            Matcher m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionNote = null;
            try {
                //Do only if it is a rejection of an event
                String[] excuseSplitted = smsTextSplitted[3].split("!");
                if (excuseSplitted.length == 2) {
                    m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionReason = pattern_onlyAlphanumsAndPointsAndWhitespaces.matcher(excuseSplitted[0]);
                    m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionNote = pattern_onlyAlphanumsAndPointsAndWhitespaces.matcher(excuseSplitted[1]);

                } else {
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                //SMS is Acceptance, no need to split
            }

            if (!m_pattern_onlyGreatherThan0.matches()) {
                Log.d("SMSRECEIVER", "Unvalid Id Part");
                return false;
            }
            if (!m_pattern_only0Or1.matches()) {
                Log.d("SMSRECEIVER", "Unvalid Acceptance boolean part");
                return false;
            }
            if (!m_pattern_onlyAlphanumsAndPointsAndWhitespaces.matches()) {
                Log.d("SMSRECEIVER", "Unvalid Alphanum/Dot/Whitespace sequence in name of participant");
                return false;
            }
            if (!isAcceptance) {
                if (!m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionReason.matches()) {
                    Log.d("SMSRECEIVER", "REASONUnvalid Alphanum/Dot/Whitespace sequence in name of participant");
                    return false;
                }
                if (!m_pattern_onlyAlphanumsAndPointsAndWhitespacesRejectionNote.matches()) {
                    Log.d("SMSRECEIVER", "NOTEUnvalid Alphanum/Dot/Whitespace sequence in name of participant");
                    return false;
                }
            }
            return true;
        } else {
            //SMS is not splitted correctly -> wrong syntax therefore corrupt SMS
            return false;
        }
    }

    /**
     * Iterates through the {@link List} and verifies possible entries in the database before saving the person and the accepted/rejected events
     *
     * @param unreadSMS, a {@link List} of {@link ReceivedHorarioSMS} to parse
     * @param context,   the {@link Context}
     */
    private void parseHorarioSMSAndUpdate(List<ReceivedHorarioSMS> unreadSMS, Context context) {
        for (ReceivedHorarioSMS singleUnreadSMS : unreadSMS) {
            Person person = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
            String savedContactExisting = null;
            savedContactExisting = lookForSavedContact(singleUnreadSMS.getPhonenumber(), context);

            /*Replace name if saved in contacts*/
            if (savedContactExisting != null) {
                person.setName(savedContactExisting);
            } else {
                person.setName(person.getName() + " (" + singleUnreadSMS.getPhonenumber() + ")");
            }
            Long eventIdInSMS = Long.valueOf(singleUnreadSMS.getCreatorEventId());
            if (EventController.checkIfEventIsInDatabaseThroughId(eventIdInSMS)) {
                //continue
            } else {
                // Create an explicit intent for an Activity in your app
                addNotification(context, 1, person.getName(), singleUnreadSMS.isAcceptance());
                break;
            }
            //Check if is SerialEvent or not
            if (isSerialEvent(eventIdInSMS)) {
                boolean hasAcceptedEarlier = false;
                boolean hasRejectedEarlier = false;
                List<Event> myEvents = EventController.getMyEventsByCreatorEventId(eventIdInSMS);
                //Is it an acceptance? Then look
                if (singleUnreadSMS.isAcceptance()) {
                    for (Event event : myEvents) {
                        Person personA = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
                        String savedContactExistingSerial = null;
                        savedContactExistingSerial = lookForSavedContact(singleUnreadSMS.getPhonenumber(), context);

                        /*Replace name if saved in contacts*/
                        if (savedContactExistingSerial != null) {
                            personA.setName(savedContactExistingSerial);
                        } else {
                            personA.setName(personA.getName() + " (" + singleUnreadSMS.getPhonenumber() + ")");
                        }
                        List<Person> allRejections = PersonController.getEventCancelledPersons(event);
                        List<Person> allAcceptances = PersonController.getEventAcceptedPersons(event);
                        for (Person personRejected : allRejections) {
                            personRejected.setPhoneNumber(shortifyPhoneNumber(personRejected.getPhoneNumber()));
                            personA.setPhoneNumber(shortifyPhoneNumber(personA.getPhoneNumber()));
                            if (personRejected.getPhoneNumber().equals(personA.getPhoneNumber())) {
                                PersonController.deletePerson(personRejected);
                                personA.setAcceptedEvent(event);
                                PersonController.savePerson(personA);
                                hasRejectedEarlier = true;
                            }
                        }
                        for (Person personAccepted : allAcceptances) {
                            personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
                            personA.setPhoneNumber(shortifyPhoneNumber(personA.getPhoneNumber()));
                            if (personAccepted.getPhoneNumber().equals(personA.getPhoneNumber())) {
                                hasAcceptedEarlier = true;
                            }
                        }
                        if (!hasRejectedEarlier && !hasAcceptedEarlier) {
                            personA.setPhoneNumber(shortifyPhoneNumber(personA.getPhoneNumber()));
                            personA.setAcceptedEvent(event);
                            PersonController.savePerson(personA);
                        }


                    }
                } else {
                    for (Event event : myEvents) {
                        Person personB = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
                        String savedContactExistingSerial = null;
                        savedContactExistingSerial = lookForSavedContact(singleUnreadSMS.getPhonenumber(), context);

                        /*Replace name if saved in contacts*/
                        if (savedContactExistingSerial != null) {
                            personB.setName(savedContactExistingSerial);
                        } else {
                            personB.setName(personB.getName() + " (" + singleUnreadSMS.getPhonenumber() + ")");
                        }
                        List<Person> allAcceptances = PersonController.getEventAcceptedPersons(event);
                        List<Person> allRejections = PersonController.getEventCancelledPersons(event);
                        for (Person personAccepted : allAcceptances) {
                            personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
                            personB.setPhoneNumber(shortifyPhoneNumber(personB.getPhoneNumber()));
                            if (personAccepted.getPhoneNumber().equals(personB.getPhoneNumber())) {
                                PersonController.deletePerson(personAccepted);
                                personB.setCanceledEvent(event);
                                personB.setRejectionReason(singleUnreadSMS.getExcuse());
                                PersonController.savePerson(personB);
                                hasAcceptedEarlier = true;
                            }
                        }
                        for (Person personRejected : allRejections) {
                            personRejected.setPhoneNumber(shortifyPhoneNumber(personRejected.getPhoneNumber()));
                            personB.setPhoneNumber(shortifyPhoneNumber(personB.getPhoneNumber()));
                            if (personRejected.getPhoneNumber().equals(personB.getPhoneNumber())) {
                                hasRejectedEarlier = true;
                            }
                        }
                        if (!hasAcceptedEarlier && !hasRejectedEarlier) {
                            personB.setPhoneNumber(shortifyPhoneNumber(personB.getPhoneNumber()));
                            personB.setCanceledEvent(event);
                            personB.setRejectionReason(singleUnreadSMS.getExcuse());
                            PersonController.savePerson(personB);
                        }
                    }
                }
            } else {
                /*Check if acceptance or cancellation*/
                boolean hasAcceptedEarlier = false;
                boolean hasRejectedEarlier = false;
                if (singleUnreadSMS.isAcceptance()) {
                    //acceptance: look for possible preceding rejection. If yes, then delete person and create new. Else just save the person
                    List<Person> allRejections = PersonController.getEventCancelledPersons(EventController.getEventById(eventIdInSMS));
                    List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventById(eventIdInSMS));
                    for (Person personRejected : allRejections) {
                        personRejected.setPhoneNumber(shortifyPhoneNumber(personRejected.getPhoneNumber()));
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        if (personRejected.getPhoneNumber().equals(person.getPhoneNumber())) {
                            PersonController.deletePerson(personRejected);
                            person.setAcceptedEvent(EventController.getEventById(eventIdInSMS));
                            PersonController.savePerson(person);
                            hasRejectedEarlier = true;
                        }

                    }
                    for (Person personAccepted : allAcceptances) {
                        personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        if (personAccepted.getPhoneNumber().equals(person.getPhoneNumber())) {
                            hasAcceptedEarlier = true;
                        }
                    }
                    if (!hasRejectedEarlier && !hasAcceptedEarlier) {
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        person.setAcceptedEvent(EventController.getEventById(eventIdInSMS));
                        PersonController.savePerson(person);
                    }
                } else {
                    //cancellation: look for possible preceding acceptance. If yes, then delete person and create new. Else just save the person
                    List<Person> allAcceptances = PersonController.getEventAcceptedPersons(EventController.getEventById(eventIdInSMS));
                    List<Person> allRejections = PersonController.getEventCancelledPersons(EventController.getEventById(eventIdInSMS));
                    for (Person personAccepted : allAcceptances) {
                        personAccepted.setPhoneNumber(shortifyPhoneNumber(personAccepted.getPhoneNumber()));
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        if (personAccepted.getPhoneNumber().equals(person.getPhoneNumber())) {
                            PersonController.deletePerson(personAccepted);
                            person.setCanceledEvent(EventController.getEventById(eventIdInSMS));
                            person.setRejectionReason(singleUnreadSMS.getExcuse());
                            PersonController.savePerson(person);
                            hasAcceptedEarlier = true;
                        }

                    }
                    for (Person personRejected : allRejections) {
                        personRejected.setPhoneNumber(shortifyPhoneNumber(personRejected.getPhoneNumber()));
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        if (personRejected.getPhoneNumber().equals(person.getPhoneNumber())) {
                            hasRejectedEarlier = true;
                        }
                    }
                    if (!hasAcceptedEarlier && !hasRejectedEarlier) {
                        person.setPhoneNumber(shortifyPhoneNumber(person.getPhoneNumber()));
                        person.setCanceledEvent(EventController.getEventById(eventIdInSMS));
                        person.setRejectionReason(singleUnreadSMS.getExcuse());
                        PersonController.savePerson(person);
                    }
                }
            }

        }
    }

    /**
     * looks for an eventual startEvent in the database
     *
     * @param eventIdInSMS, the {@link Long} number of the event
     * @return {@code true} if it is a serial {@link Event}
     */
    private boolean isSerialEvent(Long eventIdInSMS) {
        try {
            Event x = EventController.getEventById(eventIdInSMS).getStartEvent();
            if (x != null) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * replaces all the symbols in a phone number
     *
     * @param number, a {@link String}
     * @return a {@link String} of the shorter number
     */
    private String shortifyPhoneNumber(String number) {
        /*Take out all the chars not being numbers and return the numbers after "1" (German mobile number!!!)*/
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("+", "");
        number = number.replace("-", "");
        number = number.replace(" ", "");
        try {
            number = number.substring(number.indexOf("1"));
            Log.d("SHORTIFY Nummer", number);
            return number;
        } catch (StringIndexOutOfBoundsException variablenname) {
            // Ausländische Nummer

        }
        return "100000000";
    }

    /**
     * Get all the contacts, see if number is identical after "shortifying" it, if identical, replace the name
     *
     * @param address, a {@link String} of the number
     * @param context, the {@link Context}
     * @return a {@link String} of the renamed contact
     */
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

    /**
     * Creates a notification with the text
     *
     * @param context, a {@link Context}
     * @param id,      some {@link int} required
     * @param person,  a {@link String} of the name of the person in question
     */
    private void addNotification(Context context, int id, String person, boolean isAcceptance) {
        String contentText = "";
        if (isAcceptance) {
            contentText = "Horario hat festgestellt, dass Du eine Benachrichtigung zu einem Termin bekommen hast, der nicht mehr vorhanden ist." +
                    "Vermutlich hast Du Horario neu installiert, bitte kontaktiere doch folgende Person, um ihren zuletzt zugesagten Termin zu überprüfen: " +
                    person;
        } else {
            contentText = "Horario hat festgestellt, dass Du eine Benachrichtigung zu einem Termin bekommen hast, der nicht mehr vorhanden ist." +
                    "Vermutlich hast Du Horario neu installiert, bitte kontaktiere doch folgende Person, um ihren zuletzt abgesagten Termin zu überprüfen: " +
                    person;
        }
        String title = "Ups!";
        Intent notificationIntent = new Intent(context, TabActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 26) {
            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String channel_id = String.valueOf(id);
            //The user-visible name of the channel.
            CharSequence name = title;
            // The user-visible description of the channel.
            String description = contentText;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channel_id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(false);
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, channel_id)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(title)
                            .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText));
            ;

            builder.setContentIntent(contentIntent);
            manager.notify(id, builder.build());
        } else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(title)
                            .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText));
            ;
            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(id, builder.build());
        }
    }
}
