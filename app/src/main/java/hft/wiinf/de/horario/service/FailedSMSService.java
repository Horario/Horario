package hft.wiinf.de.horario.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.TabActivity;
import hft.wiinf.de.horario.controller.FailedSMSController;
import hft.wiinf.de.horario.model.FailedSMS;
import hft.wiinf.de.horario.utility.BundleUtlity;

public class FailedSMSService extends JobService {

    Bundle sms;

    @Override
    public boolean onStartJob(JobParameters params) {
        sms = BundleUtlity.toBundle(params.getExtras());
        FailedSMS failedSMS = new FailedSMS(sms.getString("message"),sms.getString("phoneNo"),sms.getInt("creatorID"),sms.getBoolean("accepted"));
        sendSMS(failedSMS);
        FailedSMSController.deleteFailedSMS(failedSMS.getMessage(),failedSMS.getCreatorID(),failedSMS.getPhoneNo());
        jobFinished(params, false);
        addNotification(sms.getString("phoneNo"), sms.getString("message"));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void sendSMS(FailedSMS failedSMS) {
        try {
            String msg = ":Horario:" + failedSMS.getCreatorID() + "," + failedSMS.isAccepted() + "," + failedSMS.getMessage();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(failedSMS.getPhoneNo(), null, msg, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotification(String phoneNo, String message) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "")
                        .setSmallIcon(R.drawable.ic_android_black2_24dp)
                        .setContentTitle("SMS erfolgreich gesendet!")
                        .setContentText(phoneNo + " " + message);

        Intent notificationIntent = new Intent(this, TabActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}

