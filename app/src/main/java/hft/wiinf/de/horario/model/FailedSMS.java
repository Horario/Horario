package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * an sms that could not been send.
 */
@Table(name = "FailedSMS")
public class FailedSMS extends Model {

    /**
     * The Message of the sms.
     */
    @Column(name = "message")
    private String message = "";
    /**
     * The Phone no the sms should be send to.
     */
    @Column(name = "phoneNo")
    private String phoneNo = "";
    /**
     * The id of the creator.
     */
    @Column(name = "creatorID")
    private long creatorID;
    /**
     * of the event was accepted by the sender.
     */
    @Column(name = "accepted")
    private boolean accepted;

    /**
     * Instantiates a new Failed sms.
     */
    public FailedSMS() {
    }

    /**
     * Instantiates a new Failed sms.
     *
     * @param message   the message of the sms
     * @param phoneNo   the phone no the sms should be send to
     * @param creatorID the id of the creator
     * @param accepted  tif the event was accepted
     */
    public FailedSMS(String message, String phoneNo, long creatorID, boolean accepted) {
        this.message = message;
        this.phoneNo = phoneNo;
        this.creatorID = creatorID;
        this.accepted = accepted;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets phone no.
     *
     * @return the phone no
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * Sets phone no.
     *
     * @param phoneNo the phone no
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /**
     * Gets creator id.
     *
     * @return the creator id
     */
    public long getCreatorID() {
        return creatorID;
    }

    /**
     * Sets creator id.
     *
     * @param creatorID the creator id
     */
    public void setCreatorID(long creatorID) {
        this.creatorID = creatorID;
    }

    /**
     * Is accepted boolean.
     *
     * @return the boolean
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Sets accepted.
     *
     * @param accepted the accepted
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}