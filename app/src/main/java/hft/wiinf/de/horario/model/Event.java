package hft.wiinf.de.horario.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


// Class for both standard and serial event
//start and endtime are both dates, not as in the gui one date and two times

/**
 * represents an calendar event (both serial and one time).
 * a serial event is reperesented as a collection of events in the database, the persons who accepted or denied the event are only stoered at the first event of the collection; all other events refer to the start event via the startEvent attribute
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
     * Instantiates a new Event with a creator.
     *
     * @param creator the creator of the event
     */
    public Event(Person creator) {
        this.creator = creator;
    }

    /**
     * Instantiates a new Event ONLY USED FOR INTERNAL REASONS, saving in DB.
     */
    public Event() {
        super();
    }

    //getter-setter

    /**
     * Gets the creator of the event.
     *
     * @return the creator
     */
    public Person getCreator() {
        return creator;
    }

    /**
     * Sets creator.
     *
     * @param creator the creator of the event, should not be null
     */
    public void setCreator(@NonNull Person creator) {
        this.creator = creator;
    }

    /**
     * Gets the short title of the event.
     *
     * @return the short title
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Sets short title.
     *
     * @param shortTitle the short title of the event
     */
    public void setShortTitle(@NonNull String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * Gets description.
     *
     * @return the long description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description of the event (should not be null)
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    /**
     * Gets place.
     *
     * @return the place of the event
     */
    public String getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place of the event (not null)
     */
    public void setPlace(@NonNull String place) {
        this.place = place;
    }

    /**
     * Gets start time.
     *
     * @return the start time of the event
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time of the event (not null)
     *                  it is concatenated of the start date and the start time of the gui
     */
    public void setStartTime(@NonNull Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets end time.
     *
     * @return the end time of the event  it is concatenated of the date and the end time of the gui
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time of the event  it is concatenated of the date and the end time of the gui (not null)
     */
    public void setEndTime(@NonNull Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets end date.
     *
     * @return the end date of a repeating event (null if it is a single event)
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date of a repeating event (null if it is a single event)
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets repetition.
     *
     * @return the repetition of the event (none to yearly)
     */
    public Repetition getRepetition() {
        return repetition;
    }

    /**
     * Sets repetition.
     *
     * @param repetition the repetition of the event (none to yearly)
     */
    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }


    /**
     * Gets accepted.
     *
     * @return the accepted state of the event
     */
    public AcceptedState getAccepted() {
        return accepted;
    }

    /**
     * Sets accepted.
     *
     * @param accepted the accepted state of the event
     */
    public void setAccepted(AcceptedState accepted) {
        this.accepted = accepted;
    }

    /**
     * Gets start event.
     *
     * @return the start event, if the event is part of a serial event and not the start event itself
     */
    public Event getStartEvent() {
        return startEvent;
    }

    /**
     * Sets start event.
     *
     * @param startEvent the start event, if the event is part of a serial event and not the start event itself
     */
    public void setStartEvent(Event startEvent) {
        this.startEvent = startEvent;
    }

    /**
     * Gets creator event id.
     *
     * @return the event id at the creators table; used for accepting and rejecting
     */
    public long getCreatorEventId() {
        return creatorEventId;
    }

    /**
     * Sets creator event id.
     *
     * @param creatorEventId the event id at the creators table; used for accepting and rejecting
     */
    public void setCreatorEventId(long creatorEventId) {
        this.creatorEventId = creatorEventId;
    }


}



