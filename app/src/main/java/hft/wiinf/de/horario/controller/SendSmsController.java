package hft.wiinf.de.horario.controller;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.FailedSMS;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.service.FailedSMSService;
import hft.wiinf.de.horario.utility.BundleUtility;

/**
 * Controller to use send SMS functions on your own. Every method can  be called with the right parameters.
 * Try to send a SMS and if it is failed schedule a job to resend it a specified time.
 */
public class SendSmsController extends BroadcastReceiver {

    public static final String SENT = "SMS_SENT";
    public static String sms_phoneNo, sms_msg, sms_eventShortDesc;
    public static boolean sms_acc;
    public static long sms_creatorID;
    public Context cont;
    public static Event mEvent;
    public static boolean lastSentInvitation;

    public void sendInvitationSMS(Context context, Event event, String sms_recipient_no){
                mEvent = event;
                cont = context;
                String stringSplitSymbol = " | ";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
                StringBuffer messageStringBuffer = new StringBuffer();
                messageStringBuffer.append(":HorarioInvitation:");
                messageStringBuffer.append(mEvent.getCreatorEventId()).append(stringSplitSymbol);
                messageStringBuffer.append(simpleDateFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
                messageStringBuffer.append(simpleDateFormat.format(mEvent.getEndDate())).append(stringSplitSymbol);
                messageStringBuffer.append(simpleTimeFormat.format(mEvent.getStartTime())).append(stringSplitSymbol);
                messageStringBuffer.append(simpleTimeFormat.format(mEvent.getEndTime())).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getRepetition()).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getShortTitle()).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getPlace()).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getDescription()).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getCreator().getName()).append(stringSplitSymbol);
                messageStringBuffer.append(mEvent.getCreator().getPhoneNumber());
                messageStringBuffer.append(":HorarioInvitation:");

                String message = messageStringBuffer.toString();
                Log.d("louis", message);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    if(message.length() > 160) {
                        ArrayList<String> list = smsManager.divideMessage(message);
                        smsManager.sendMultipartTextMessage(sms_recipient_no,null,list,null,null);
                    }else {
                        smsManager.sendTextMessage(sms_recipient_no, null, message, null, null);
                    }
                    Person invitedPerson = new Person(sms_recipient_no,"unknown");
                    invitedPerson.setPendingEvent(event);
                    invitedPerson.save();
                    Toast.makeText(context,"Einladung wurde verschickt",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.d("louis", e.getMessage());
                }

    }

    /**
     * try to send the SMS.
     * It will be check if the device is able to send SMS at all --> if not a ToastMessage will be displayed but the user
     * can use all functions except the sms part (creator will not get an answer but event will be saved)
     * Format of SMS:
     *  - Accepted: ":Horario:sms_creatorEventID,1,username"
     *  - Rejected: ":Horario:sms_creatorEventID,0,username,rejectMessage"
     *
     * @param context of the active fragment/activity
     * @param sms_phoneNumber from the targetDevice
     * @param sms_rejectMessage optional: could be " " --> why is the user unable to participate. Special Format: Category!personal Message Example: "Ill!Iam really sick"
     * @param sms_accepted boolean if the event was accepted = true or rejected = false
     * @param sms_creatorEventId is a reference to the EventID in the Database of the creator to find the right Event later
     * @param eventShortDesc the short description of the event
     */
    public void sendSMS(final Context context, String sms_phoneNumber, String sms_rejectMessage, boolean sms_accepted, long sms_creatorEventId, String eventShortDesc) {
        //Check if device is able to send SMS at all
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            //If not give a short explanation, but do not disable any functions except sending sms
            Toast.makeText(context, context.getString(R.string.sms_notAbleToSend), Toast.LENGTH_LONG).show();
        } else {
            sms_phoneNo = sms_phoneNumber;
            sms_msg = sms_rejectMessage;
            sms_acc = sms_accepted;
            sms_creatorID = sms_creatorEventId;
            sms_eventShortDesc = eventShortDesc;
            cont = context;

            String msg;
            Person personMe = PersonController.getPersonWhoIam();
            if (sms_accepted) {
                msg = ":Horario:" + sms_creatorEventId + ",1," + personMe.getName() +":Horario:";
            } else {
                msg = ":Horario:" + sms_creatorEventId + ",0," + personMe.getName() + "," + sms_rejectMessage + ":Horario:";
            }

            try {
                PendingIntent sentPI = PendingIntent.getBroadcast(cont, 0, new Intent(SENT), 0);

                final SendSmsController smsUtils = new SendSmsController();
                //register for sending and delivery
                cont.registerReceiver(smsUtils, new IntentFilter(SendSmsController.SENT));

                SmsManager sms = SmsManager.getDefault();
                Log.d("louis", "sending sms");
                sms.sendTextMessage(sms_phoneNumber, null, msg, sentPI, null);

                //we unregister in 10 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cont.unregisterReceiver(smsUtils);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, 10000);
            } catch (Exception e) {
                Toast.makeText(cont, cont.getString(R.string.sms_exception), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * If the SMS failed start a job to schedule them again.
     */
    public void startJobSendSMS() {
        //Save just to be sure not to forget it
        FailedSMS failedSMS = new FailedSMS(sms_msg, sms_phoneNo, sms_creatorID, sms_acc);
        saveFailedSMS(failedSMS);

        Bundle sms = new Bundle();
        sms.putString("phoneNo", sms_phoneNo);
        sms.putString("message", sms_msg);
        sms.putLong("creatorID", sms_creatorID);
        sms.putBoolean("accepted", sms_acc);
        sms.putString("eventShortDesc", sms_eventShortDesc);
        sms.putInt("id", failedSMS.getId().intValue());

        PersistableBundle persBund = BundleUtility.toPersistableBundle(sms);
        JobScheduler jobScheduler = (JobScheduler) cont.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //Job will be scheduled for later and with setPersisted it will be alive after a device reboot as well
        jobScheduler.schedule(new JobInfo.Builder(failedSMS.getId().intValue(), new ComponentName(cont, FailedSMSService.class))
                .setExtras(persBund)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build());
    }

    /**
     * failedSMS will be saved in the DB
     * @param failedSMS with all it defined params
     */
    public void saveFailedSMS(FailedSMS failedSMS) {
        FailedSMSController.addFailedSMS(failedSMS);
    }

    /**
     * Do not call this directly!
     * service will send a message to the receiver weather the sending process was successful or not
     * If the action was not succesful the startJobSendSMS Method will be called
     * @param context of the active fragment/activity
     * @param intent which is used in the registerReceiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SENT)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    Toast.makeText(context, context.getString(R.string.sms_sent), Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                    startJobSendSMS();
                    Toast.makeText(context, context.getString(R.string.sms_fail), Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    startJobSendSMS();
                    Toast.makeText(context, context.getString(R.string.sms_fail), Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    startJobSendSMS();
                    Toast.makeText(context, context.getString(R.string.sms_fail), Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    startJobSendSMS();
                    Toast.makeText(context, context.getString(R.string.sms_fail), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}


