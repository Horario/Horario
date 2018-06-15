package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * The type Person.
 */
//class for a person with can (not) participate at an appointment or be the creator of an appointment
@Table(name = "persons")
public class Person extends Model {
    /**
     * The Phone number.
     */
    @Column
    String phoneNumber = "";
    /**
     * The Name.
     */
    @Column
    String name = "";
    @Column(name = "event_Accepted")
    private Event acceptedEvent = null;
    @Column(name = "event_canceled")
    private Event canceledEvent = null;
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
     * Instantiates a new Person.
     *
     * @param isItMe      the is it me
     * @param phoneNumber the phone number
     * @param name        the name
     */
//Use this constructor for person that using this specific app (owner)
    public Person(boolean isItMe, String phoneNumber, String name) {
        super();
        this.isItMe = isItMe;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    /**
     * Instantiates a new Person.
     *
     * @param phoneNumber the phone number
     * @param name        the name
     */
//Use this constructor for persons that is not the current(real) user of this specific app
    public Person(String phoneNumber, String name) {
        super();
        this.isItMe = false;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    /**
     * Instantiates a new Person.
     *
     * @param phoneNumber      the phone number
     * @param notificationTime the notification time
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
     * Is it me boolean.
     *
     * @return the boolean
     */
    public boolean isItMe() {
        return isItMe;
    }

    /**
     * Sets it me.
     *
     * @param itMe the it me
     */
    public void setItMe(boolean itMe) {
        this.isItMe = itMe;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
//getter-setter
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets canceled event.
     *
     * @param canceledEvent the canceled event
     */
    public void setCanceledEvent(Event canceledEvent) {
        this.canceledEvent = canceledEvent;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets accepted event.
     *
     * @param acceptedEvent the accepted event
     */
    public void setAcceptedEvent(Event acceptedEvent) {
        this.acceptedEvent = acceptedEvent;
    }

    /**
     * Gets notification time.
     *
     * @return the notification time
     */
    public int getNotificationTime() {
        return notificationTime;
    }

    /**
     * Sets notification time.
     *
     * @param notificationTime the notification time
     */
    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }

    /**
     * Is enable push boolean.
     *
     * @return the boolean
     */
    public boolean isEnablePush() {
        return enablePush;
    }

    /**
     * Sets enable push.
     *
     * @param enablePush the enable push
     */
    public void setEnablePush(boolean enablePush) {
        this.enablePush = enablePush;
    }

    /**
     * Gets start tab.
     *
     * @return the start tab
     */
    public int getStartTab() {
        return startTab;
    }

    /**
     * Sets start tab.
     *
     * @param startTab the start tab
     */
    public void setStartTab(int startTab) {
        this.startTab = startTab;
    }

    /**
     * Gets rejection reason.
     *
     * @return the rejection reason
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Sets rejection reason.
     *
     * @param rejectionReason the rejection reason
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}


