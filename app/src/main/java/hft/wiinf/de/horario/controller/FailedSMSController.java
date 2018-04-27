package hft.wiinf.de.horario.controller;

import android.util.Log;

import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.FailedSMS;
import hft.wiinf.de.horario.model.Person;

public class FailedSMSController {

    public static void addFailedSMS(Event event, Person creator) {
        try {
            FailedSMS failedSMS = new FailedSMS(event,creator);
            failedSMS.save();
        } catch (Exception e) {
            Log.d("FailedSMSController", "addFailedSMS:" + e.getMessage());
        }
    }


    public static List<FailedSMS> getAllFailedSMS(){
        return new Select()
                .from(FailedSMS.class)
                .execute();
    }

    public static void deleteFailedSMS(FailedSMS failedSMS){
        failedSMS.delete();
    }
}
