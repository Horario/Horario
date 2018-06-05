package hft.wiinf.de.horario.service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.ReceivedHorarioSMS;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
        SmsMessage[] receivedSMSArray = null;
        ArrayList<ReceivedHorarioSMS> unreadHorarioSMS = new ArrayList<ReceivedHorarioSMS>();

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
                    if(!checkForRegexOk(receivedSMSArray[i])){
                        break;
                        //Log.d("REGEXoccurence!", receivedSMSArray[i].getMessageBody().toString());
                    }
                    String number = (receivedSMSArray[i].getOriginatingAddress());
                    String[] parsedSMS = receivedSMSArray[i].getMessageBody().toString().substring(9).split(",");
                    if (parsedSMS[1].equalsIgnoreCase("1")) {
                        unreadHorarioSMS.add(new ReceivedHorarioSMS(number, true, Integer.parseInt(parsedSMS[0]), null, parsedSMS[2]));
                    } else {
                        unreadHorarioSMS.add(new ReceivedHorarioSMS(number, false, Integer.parseInt(parsedSMS[0]), parsedSMS[3], parsedSMS[2]));
                    }
                }
            }
            parseHorarioSMSAndUpdate(unreadHorarioSMS, context);

        }
    }

    private boolean checkForRegexOk(SmsMessage smsMessage) {
        //TODO
//        smsMessage.getMessageBody().toString().charAt(2);
        return true;
    }

    private void parseHorarioSMSAndUpdate(List<ReceivedHorarioSMS> unreadSMS, Context context) {
        for (ReceivedHorarioSMS singleUnreadSMS : unreadSMS) {
            Person person = new Person(singleUnreadSMS.getPhonenumber(), singleUnreadSMS.getName());
            String savedContactExisting = null;
            savedContactExisting = lookForSavedContact(singleUnreadSMS.getPhonenumber(),context);

            /*Replace name if saved in contacts*/
            if (savedContactExisting != null) {
                person.setName(savedContactExisting);
            }else{
                person.setName(person.getName() + " (" + singleUnreadSMS.getPhonenumber() + ")");
            }
            Long eventIdInSMS = Long.valueOf(singleUnreadSMS.getCreatorEventId());
//            if(EventController.checkIfEventIsInDatabaseThroughId(eventIdInSMS)){
//                //continue
//            }else{
//                AlertDialog.Builder builder = new AlertDialog.Builder();
//                builder.setTitle("Ups!");
//                builder.setMessage("Horario hat festgestellt, dass Du eine Benachrichtigung zu einem Termin bekommen hast, der nicht mehr existiert." +
//                        "Vermutlich hast Du Horario neu installiert, bitte benachrichtige doch folgende Person, ob BABLLABAABL");
//                // Add the button
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User clicked OK button
//                    }
//                });
//                builder.create();
//                builder.show();
//            }
            /*Check if acceptance or cancellation*/
            boolean hasAcceptedEarlier = false;
            if (singleUnreadSMS.isAcceptance()) {
                person.setAcceptedEvent(EventController.getEventById(eventIdInSMS));
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
                        person.setRejectionReason(singleUnreadSMS.getExcuse());
                        PersonController.savePerson(person);
                        hasAcceptedEarlier = true;
                    }

                }
                if (!hasAcceptedEarlier){
                    person.setCanceledEvent(EventController.getEventById(Long.valueOf(singleUnreadSMS.getCreatorEventId())));
                    person.setRejectionReason(singleUnreadSMS.getExcuse());
                    PersonController.savePerson(person);
                }
            }
        }
    }

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
}
