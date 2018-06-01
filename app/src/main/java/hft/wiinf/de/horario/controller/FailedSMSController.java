package hft.wiinf.de.horario.controller;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.model.FailedSMS;

public class FailedSMSController {

    public static void addFailedSMS(FailedSMS failedSMS) {
        try {
            failedSMS.save();
        } catch (Exception e) {
            Log.d("FailedSMSController", "addFailedSMS:" + e.getMessage());
        }
    }

    public static List<FailedSMS> getAllFailedSMS() {
        return new Select()
                .from(FailedSMS.class)
                .execute();
    }

    public static void deleteFailedSMS(String message, long creatorID, String phoneNo) {
        try{
            new Delete()
                    .from(FailedSMS.class)
                    .where("message = ? AND creatorID = ? AND phoneNo = ?",message,creatorID,phoneNo)
                    .execute();
        }catch(Exception e){
            Log.d("FailedSMSController", "deleteFailedSMS:" + e.getMessage());
        }
    }

    public static void deleteFailedSMS(FailedSMS failedSMS) {
        try {
            failedSMS.delete();
        } catch (Exception e) {
            Log.d("FailedSMSController", "deleteFailedSMS(Object):" + e.getMessage());
        }

    }
}