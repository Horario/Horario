package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * class for a person with can (not) participate at an appointment or be the creator of an appointment
 * NOTE: 1. event-person is an 1:n relation-ship (accepted or rejected event), therfore the same person could be serversl times in the database.
 * 2. because of 1 only accepted event or cacelled event could be set - one person could not cacel AND acept an event
 * 3. enable push, notification time, start tab should only be read out for the app user
 */
//
@Table(name = "persons")
public class Person extends Model {
    @Column
    private String phoneNumber = "";
    @Column
    private String name = "";
    @Column(name = "event_Accepted")
    private Event acceptedEvent = null;
    @Column(name = "event_canceled")
    private Event canceledEvent = null;
    @Column(name = "event_pending")
    private Event pendingEvent = null;
    @Column
    private boolean isItMe = false;
    @Column
    private boolean enablePush = true;
    @Column
    private int notificationTime = 15;
    @Column
    private int startTab = 1;
    @Column
    private String rejectionReason = "";

    /**
     * Instantiates a new Person. Use this constructor for person that using this specific app (owner)
     *
     * @param isItMe      if the person is the app user
     * @param phoneNumber the phone number of the user
     * @param name        the name of ther user
     */
    public Person(boolean isItMe, String phoneNumber, String name) {
        super();
        this.isItMe = isItMe;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    /**
     * Instantiates a new Person. Use this constructor for persons that is not the current(real) user of this specific app
     *
     * @param phoneNumber the phone number of the user
     * @param name        the name of the user
     */
    public Person(String phoneNumber, String name) {
        super();
        this.isItMe = false;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    /**
     * Instantiates a new Person.
     *
     * @param phoneNumber      the phone number of the user
     * @param notificationTime the notification time in minutes before the event
     */
    public Person(String phoneNumber, int notificationTime) {
        super();
        this.phoneNumber = phoneNumber;
        this.notificationTime = notificationTime;
    }

    /**
     * Instantiates a new Person.
     */
    public Person() {
        super();
    }

    /**
     * get If the person is the app user
     *
     * @return If the person is the app user
     */
    public boolean isItMe() {
        return isItMe;
    }

    /**
     * set If the person is the app user
     *
     * @param itMe If the person is the app user
     */
    public void setItMe(boolean itMe) {
        this.isItMe = itMe;
    }

    /**
     * Gets the phone number of the user. the number is only a string, and no specific format - this should be ensured by the application
     *
     * @return the phone number of the user
     */
    //getter-setter
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * sets the phone number of the user. The number is only a string, and no specific format - this should be ensured by the application
     *
     * @param phoneNumber the phone number of the user. The number is only a string, and no specific format - this should be ensured by the application
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the event the person has cacelled
     *
     * @param canceledEvent the event the person has cacelled
     */
    public void setCanceledEvent(Event canceledEvent) {
        this.canceledEvent = canceledEvent;
    }

    /**
     * Gets the name of the user
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the event the user has accepted.
     *
     * @param acceptedEvent the accepted event
     */
    public void setAcceptedEvent(Event acceptedEvent) {
        this.acceptedEvent = acceptedEvent;
    }

    /**
     * Gets notification time of this user.
     *
     * @return the notification time in minutes before the event
     */
    public int getNotificationTime() {
        return notificationTime;
    }

    /**
     * Sets notification time.
     *
     * @param notificationTime the notification time in minutes before the event
     */
    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }

    /**
     * If push is enabled
     *
     * @return if push is enabled
     */
    public boolean isEnablePush() {
        return enablePush;
    }

    /**
     * Set if push is enabled
     *
     * @param enablePush if push is enabled
     */
    public void setEnablePush(boolean enablePush) {
        this.enablePush = enablePush;
    }

    /**
     * Gets the start tab of the user.
     *
     * @return the start tab of the user (0 based from left (0) to right (2))
     */
    public int getStartTab() {
        return startTab;
    }

    /**
     * Sets the start tab of the user.
     *
     * @param startTab the start tab of the user (0 based from left (0) to right (2)
     */
    public void setStartTab(int startTab) {
        this.startTab = startTab;
    }

    /**
     * Gets rejection reason.
     *
     * @return the rejection reason of the user
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Sets rejection reason of the user (should only be set if the user rejected an event).
     *
     * @param rejectionReason the rejection reason
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setPendingEvent(Event pendingEvent) {
        this.pendingEvent = pendingEvent;
    }
}


