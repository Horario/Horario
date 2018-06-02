package hft.wiinf.de.horario.model;


import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


// Class for both standard and serial event
//start and endtime are both dates, not as in the gui one date and two times

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

    //create a new event with a creator
    public Event(Person creator) {
        this.creator = creator;
    }

    public Event() {
        super();
    }

    //getter-setter

    public Person getCreator() {
        return creator;
    }

    public void setCreator(@NonNull Person creator) {
        this.creator = creator;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(@NonNull String place) {
        this.place = place;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(@NonNull Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }


    public AcceptedState getAccepted() {
        return accepted;
    }

    public void setAccepted(AcceptedState accepted) {
        this.accepted = accepted;
    }

    public Event getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Event startEvent) {
        this.startEvent = startEvent;
    }

    public long getCreatorEventId() {
        return creatorEventId;
    }

    public void setCreatorEventId(long creatorEventId) {
        this.creatorEventId = creatorEventId;
    }


}



