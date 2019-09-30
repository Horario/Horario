package hft.wiinf.de.horario.service;

import android.app.Application;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.InvitationController;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.controller.ScanResultReceiverController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Invitation;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;

/**
 * The type Sms receiver extends a {@link BroadcastReceiver} and reacts each time the phone of the user receives an SMS.
 * The SMS itself is checked against typical RegEx in SQL Injections.
 * After that, the SMS is parsed, for details, look in the methods.
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
     * The SMS is relevant if the first and last characters are equal to ":Horario: or :HorarioInvitation:".
     * If it is relevant, it is put into an {@link ArrayList} of {@link ReceivedHorarioSMS}
     *
     * @param context, the {@link Context}
     * @param intent,  the {@link Intent}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
        SmsMessage[] receivedSMSArray;
        ArrayList<ReceivedHorarioSMS> unreadHorarioSMS = new ArrayList<ReceivedHorarioSMS>();
        boolean isSMSValidAndParseable = false;
        if (bundle != null) {

            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            receivedSMSArray = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < receivedSMSArray.length; i++) {
                // Convert Object array
                receivedSMSArray[i] = SmsMessage.createFromPdu((byte[]) pdus[i], "3gpp");
            }
            List<String> previousMessages = new ArrayList<>();
            for (int i = 0; i < receivedSMSArray.length; i++) {
                /*collect all the Horario SMS*/
                String message = receivedSMSArray[i].getMessageBody();
                if (message.length() > 9 && message.substring(0, 9).equals(":Horario:") && message.substring(message.length() - 9).equals(":Horario:")) {
                    String number = (receivedSMSArray[i].getOriginatingAddress());
                    String[] parsedSMS = message.substring(9, message.length() -9).split(",");
                    if (!checkForResponseRegexOk(parsedSMS)) {
                        Log.d("Corrupt SMS Occurence!", message);
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
                    isSMSValidAndParseable = true;
                }else if(message.length() > 9 && message.substring(0, 19).equals(":HorarioInvitation:")){
                    previousMessages.add(message);
                    StringBuilder fullmessageBuilder = new StringBuilder();
                    for(String previousMessage : previousMessages){
                        fullmessageBuilder.append(previousMessage);
                    }
                    if(checkForInvitationRegexOk(fullmessageBuilder.toString())){
                        Invitation newInvitation = new Invitation(fullmessageBuilder.toString().replaceAll(":HorarioInvitation:", ""), new Date());
                        String eventDateTimeString  = newInvitation.getStartTime() + " " + newInvitation.getStartDate();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        try {
                            Date eventDateTime = format.parse(eventDateTimeString);
                            if(eventDateTime.after(newInvitation.getDateReceived())){

                                if(!InvitationController.alreadyInvited(newInvitation) && !InvitationController.eventAlreadySaved(newInvitation)) {
                                    InvitationController.saveInvitation(newInvitation);
                                    NotificationController.sendInvitationNotification(context, newInvitation);
                                }else{
                                    Log.d("louis", "already invited");
                                }
                            }else{
                                Log.d("louis", "received expired invitation");
                                Log.d("louis", eventDateTimeString + " " + newInvitation.getDateReceived());
                            }
                        }catch(ParseException e){
                            e.printStackTrace();
                        }

                    }
                }else if(previousMessages.size() != 0){
                    previousMessages.add(message);
                    StringBuilder fullmessageBuilder = new StringBuilder();
                    for(String previousMessage : previousMessages){
                        fullmessageBuilder.append(previousMessage);
                    }
                    if(checkForInvitationRegexOk(fullmessageBuilder.toString())){
                        Invitation newInvitation = new Invitation(fullmessageBuilder.toString().replaceAll(":HorarioInvitation:", ""), new Date());
                        String eventDateTimeString  = newInvitation.getStartTime() + " " + newInvitation.getStartDate();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        try {
                            Date eventDateTime = format.parse(eventDateTimeString);
                            if(eventDateTime.after(newInvitation.getDateReceived())){
                                if(!InvitationController.alreadyInvited(newInvitation)&& !InvitationController.eventAlreadySaved(newInvitation)) {
                                    InvitationController.saveInvitation(newInvitation);
                                    NotificationController.sendInvitationNotification(context, newInvitation);
                                }
                            }else{
                                Log.d("louis", "received expired invitation" + "test");

                            }
                        }catch(ParseException e){
                            e.printStackTrace();
                        }

                        previousMessages.clear();
                    }
                }
            }
            if (isSMSValidAndParseable) {
                parseHorarioSMSAndUpdate(unreadHorarioSMS, context);
            }

        }
    }

    private boolean checkForInvitationRegexOk(String message){
        if(!message.matches(":HorarioInvitation:.*:HorarioInvitation:")){
            return false;
        }
        message = message.replaceAll(":HorarioInvitation:", "");
        String[] splitMessage = message.split(" \\| ");
        if(splitMessage.length == 11) {
            //check if id is valid
            if(!splitMessage[0].matches("^[^0\\D]\\d*$")){
                Log.d("", splitMessage[0]);
                return false;
            }
            //check if startDate is valid
            if(!splitMessage[1].matches("^(?:(?:31(\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")){
                Log.d("louis", splitMessage[1]);
                return false;
            }
            // check if endDate is valid. Pattern only matches valid dates in DD.MM.YYYY format and
            // includes a check for leap years so it correctly matches 29.02.YYYY
            if(!splitMessage[2].matches("^(?:(?:31(\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")){
                Log.d("louis", splitMessage[2]);
                return false;
            }
            if(!splitMessage[3].matches("^([01]\\d|2[0-3]):([0-5]\\d)$")){
                Log.d("louis", splitMessage[3]);
                return false;
            }
            if(!splitMessage[4].matches("^([01]\\d|2[0-3]):([0-5]\\d)$")){
                Log.d("louis", splitMessage[4]);
                return false;
            }
            if(!splitMessage[5].matches("^[^\\s|][^|]*$")){
                Log.d("louis", splitMessage[5]);
                return false;
            }
            if(!splitMessage[6].matches("^[^\\s|][^|]*$")){
                Log.d("louis", splitMessage[6]);
                return false;
            }
            if(!splitMessage[7].matches("^[^\\s|][^|]*$")){
                Log.d("louis", splitMessage[7]);
                return false;
            }
            if(!splitMessage[8].matches("^[^\\s|][^|]*$")){
                Log.d("louis", splitMessage[8]);
                return false;
            }
            if(!splitMessage[9].matches("^[^\\s|][^|]*$")){
                Log.d("louis", splitMessage[9]);
                return false;
            }
            if(!splitMessage[10].matches("^\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\\d{1,14}$")){
                Log.d("louis", splitMessage[10]);
                return false;
            }
            return true;
        }
        return false;
    }
    /**
     * Takes the parameter and checks for eventual SQL Injections and other syntax problems relevant for the functionality of the app.
     *
     * @param smsTextSplitted, an {@link java.util.Arrays} of {@link String}
     * @return {@code true} if the SMS in question is valid and ready for the next method.
     */
    private boolean checkForResponseRegexOk(String[] smsTextSplitted) {
        // RegEx: NO SQL Injections allowed PLUS check if SMS is valid
        // smsTextSplitted[0]= CreatorEventId, should be only number greater than 0
        // smsTextSplitted[1]= boolean for acceptance, should be only 0 or 1
        // smsTextSplitted[2]= String for name, only Chars and points
        // smsTextSplitted[3]= Excuse asString, needs to be splitted again by "!" and checked on two strings
        if (smsTextSplitted.length == 3 || smsTextSplitted.length == 4) {
            boolean isAcceptance = smsTextSplitted.length == 3;

            //Make Patterns
            Pattern pattern_onlyGreatherThan0 = Pattern.compile("^[^0\\D]\\d*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern pattern_only0Or1 = Pattern.compile("([01])", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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
     * In case of a double acceptance or double rejection (QR-Code is scanned twice) then the person is not added.
     * For more informations, look into the code comments.
     * @param unreadSMS, a {@link List} of {@link ReceivedHorarioSMS} to parse
     * @param context,   the {@link Context}
     */
    private void parseHorarioSMSAndUpdate(List<ReceivedHorarioSMS> unreadSMS, Context context) {
        for (ReceivedHorarioSMS singleUnreadSMS : unreadSMS) {
            Person person = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
            String savedContactExisting;
            savedContactExisting = lookForSavedContact(singleUnreadSMS.getPhonenumber(), context);

            /*Replace name if saved in contacts*/
            if (savedContactExisting != null) {
                person.setName(savedContactExisting);
            } else {
                person.setName(person.getName() + " (" + singleUnreadSMS.getPhonenumber() + ")");
            }
            Long eventIdInSMS = Long.valueOf(singleUnreadSMS.getCreatorEventId());
            if (!EventController.checkIfEventIsInDatabaseThroughId(eventIdInSMS)) {
                addNotification(context, 1, person.getName(), singleUnreadSMS.isAcceptance());
                break;
            }
            //Deletes the invited placeholder person
            PersonController.deleteInvitedPerson(singleUnreadSMS.getPhonenumber(),String.valueOf(eventIdInSMS));
            //Check if is SerialEvent or not
            if (isSerialEvent(eventIdInSMS)) {
                boolean hasAcceptedEarlier = false;
                boolean hasRejectedEarlier = false;
                List<Event> myEvents = EventController.getMyEventsByCreatorEventId(eventIdInSMS);
                //Is it an acceptance? Then look
                if (singleUnreadSMS.isAcceptance()) {
                    //acceptance: look for possible preceding rejections. If yes, then delete person and create new. Else just save the person
                    //do this for each event of the event series
                    for (Event event : myEvents) {
                        Person personA = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
                        String savedContactExistingSerial;
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
                    //cancellation: look for possible preceding acceptance. If yes, then delete person and create new. Else just save the person
                    //do this for each event of the event series
                    for (Event event : myEvents) {
                        Person personB = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
                        String savedContactExistingSerial;
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
                //it is a single event
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
                return x != null;

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
     * Creates a notification with the text in case of a mismatch between the creatorEventId in the SMS received and the DB.
     *
     *
     * @param context, a {@link Context}
     * @param id,      some {@link int} required
     * @param person,  a {@link String} of the name of the person in question
     */
    private void addNotification(Context context, int id, String person, boolean isAcceptance) {
        String contentText;
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
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channel_id, title, importance);
            // Configure the notification channel.
            mChannel.setDescription(contentText);
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

            builder.setContentIntent(contentIntent);
            manager.notify(id, builder.build());
        } else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(title)
                            .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText));

            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(id, builder.build());
        }
    }
}
