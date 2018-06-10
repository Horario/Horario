
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

    private boolean checkForRegexOk(String[] smsTextSplitted) {
        //RegEx: NO SQL Injections allowed PLUS check if SMS is valid
//        smsTextSplitted[0]= CreatorEventId, should be only number greater than 0
//        smsTextSplitted[1]= boolean for acceptance, should be only 0 or 1
//        smsTextSplitted[2]= String for name, only Chars and points
//        smsTextSplitted[3]= Excuse asString, needs to be splitted again by "!" and checked on two strings
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
                addNotification(context, 1, person.getName());
                break;
            }
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
                if (!hasAcceptedEarlier) {
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

    private void addNotification(Context context, int id, String person) {
        String contentText = "Horario hat festgestellt, dass Du eine Benachrichtigung zu einem Termin bekommen hast, der nicht mehr vorhanden ist." +
                "Vermutlich hast Du Horario neu installiert, bitte kontaktiere doch folgende Person, um ihren zuletzt zugesagten Termin zu überprüfen: " +
                person;
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
                            .setSmallIcon(R.drawable.ic_android_black2_24dp)
                            .setContentTitle(title)
                            .setContentText(contentText).setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText));
            ;

            builder.setContentIntent(contentIntent);
            manager.notify(id, builder.build());
        } else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "")
                            .setSmallIcon(R.drawable.ic_android_black2_24dp)
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
