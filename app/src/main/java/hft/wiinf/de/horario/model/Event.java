package hft.wiinf.de.horario.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


// Class for both standard and serial event
//start and endtime are both dates, not as in the gui one date and two times

/**
 * The type Event.
 */
@Table(name = "events")
public class Event extends Model {
    //the columns of an event
    @Column
    private Person creator;
    @Column
    private String shortTitle = "";
    @Column
    private String description = "";
    @Column
    private String place = "";
    @Column
    private Date startTime = new Date();
    @Column
    private Date endTime = new Date();
    @Column
    private Repetition repetition = Repetition.NONE;
    @Column
    private Date endDate = new Date();
    @Column
    private AcceptedState accepted;
    @Column
    //the first event of an repeating / serial event
    private Event startEvent = null;
    // the id of the event in the db of the creator
    @Column
    private long creatorEventId = -1;

    /**
     * Instantiates a new Event.
     *
     * @param creator the creator
     */
//create a new event with a creator
    public Event(Person creator) {
        this.creator = creator;
    }

    /**
     * Instantiates a new Event.
     */
    public Event() {
        super();
    }

    //getter-setter

    /**
     * Gets creator.
     *
     * @return the creator
     */
    public Person getCreator() {
        return creator;
    }

    /**
     * Sets creator.
     *
     * @param creator the creator
     */
    public void setCreator(@NonNull Person creator) {
        this.creator = creator;
    }

    /**
     * Gets short title.
     *
     * @return the short title
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Sets short title.
     *
     * @param shortTitle the short title
     */
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(@NonNull String place) {
        this.place = place;
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(@NonNull Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(@NonNull Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets repetition.
     *
     * @return the repetition
     */
    public Repetition getRepetition() {
        return repetition;
    }

    /**
     * Sets repetition.
     *
     * @param repetition the repetition
     */
    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }


    /**
     * Gets accepted.
     *
     * @return the accepted
     */
    public AcceptedState getAccepted() {
        return accepted;
    }

    /**
     * Sets accepted.
     *
     * @param accepted the accepted
     */
    public void setAccepted(AcceptedState accepted) {
        this.accepted = accepted;
    }

    /**
     * Gets start event.
     *
     * @return the start event
     */
    public Event getStartEvent() {
        return startEvent;
    }

    /**
     * Sets start event.
     *
     * @param startEvent the start event
     */
    public void setStartEvent(Event startEvent) {
        this.startEvent = startEvent;
    }

    /**
     * Gets creator event id.
     *
     * @return the creator event id
     */
    public long getCreatorEventId() {
        return creatorEventId;
    }

    /**
     * Sets creator event id.
     *
     * @param creatorEventId the creator event id
     */
    public void setCreatorEventId(long creatorEventId) {
        this.creatorEventId = creatorEventId;
    }


}



