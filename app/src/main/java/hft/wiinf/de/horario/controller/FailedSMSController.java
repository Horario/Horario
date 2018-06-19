package hft.wiinf.de.horario.controller;

import android.util.Log;
import com.activeandroid.query.Delete;
import hft.wiinf.de.horario.model.FailedSMS;

/**
 * This class will do all Database related actions which are required for a Failed SMS
 *  - add a FailedSMS to the Database
 *  - delete the FailedSMS from the database after the SMS was sent successfully
 */
public class FailedSMSController {

    /**
     * This static Method can be called from anywhere in this application.
     * It will save a FailedSMS in the Database if there are no exceptions like a NullPointer.
     *
     * @param failedSMS -> Object which Failed to sent
     * @throws Exception if there is a error which needs to be caught
     */
    public static void addFailedSMS(FailedSMS failedSMS) {
        try {
            failedSMS.save();
        } catch (Exception e) {
            Log.d("FailedSMSController", "addFailedSMS:" + e.getMessage());
        }
    }

    /**
     * This static Method can be called from anywhere in this application.
     * It will delete the FailedSMS after it was sent successfully.
     * @param message describes the message which needed to be sent
     * @param creatorID is a reference to the EventID in the Database of the creator
     * @param phoneNo to send the SMS to the right person/device
     */
    public static void deleteFailedSMS(String message, long creatorID, String phoneNo) {
        try {
            new Delete()
                    .from(FailedSMS.class)
                    .where("message = ? AND creatorID = ? AND phoneNo = ?", message, creatorID, phoneNo)
                    .execute();
        } catch (Exception e) {
            Log.d("FailedSMSController", "deleteFailedSMS:" + e.getMessage());
        }
    }
}
