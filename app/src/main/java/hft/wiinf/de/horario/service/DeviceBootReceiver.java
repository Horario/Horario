package hft.wiinf.de.horario.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Will be executed after Device has finished reboot
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Check if there is person added with isItMe = true
            if (PersonController.getPersonWhoIam() != null) {
                Person notificationPerson = PersonController.getPersonWhoIam();
                //Only set Alarm if Person wants to receive them
                if (notificationPerson.isEnablePush()) {
                    NotificationController.startAlarmForAllEvents(context);
                }
            }
        }
    }
}