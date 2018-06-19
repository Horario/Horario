package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * The type Failed sms.
 */
@Table(name = "FailedSMS")
public class FailedSMS extends Model {

    /**
     * The Message what should be sent.
     */
    @Column(name = "message")
    String message = "";
    /**
     * The Phone no of the target
     */
    @Column(name = "phoneNo")
    String phoneNo = "";
    /**
     * The Creator id.
     */
    @Column(name = "creatorID")
    long creatorID;
    /**
     * weather event is accepted or not
     */
    @Column(name = "accepted")
    boolean accepted;

    /**
     * Instantiates a new Failed sms.
     */
    public FailedSMS() {
    }

    /**
     * Instantiates a new Failed sms.
     *
     * @param message   the message
     * @param phoneNo   the phone no
     * @param creatorID the creator id
     * @param accepted  the accepted
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