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
    @Column
    private Person creator;
    @Column
    private String description = "";
    @Column
    private int creatorEventId;
    @Column
    private String place = "";
    @Column
    private Date startTime = new Date();
    @Column
    private Date endTime = new Date();
    @Column
    private boolean accepted;
    public Event(Person creator) {
        this.creator = creator;
    }

    public Event() {
        super();
    }

    public Event(Person creator, String description, String place,int creatorEventId, Date startTime, Date endTime, boolean accepted){
        this.creator = creator;
        this.description = description;
        this.place = place;
        this.startTime = startTime;
        this.endTime = endTime;
        this.accepted = accepted;
        this.creatorEventId = creatorEventId;
    }



//getter-setter

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Person getCreator() {
        return creator;
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

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getCreatorEventId() {
        return creatorEventId;
    }

    public void setCreatorEventId(int creatorEventId) {
        this.creatorEventId = creatorEventId;
    }
}




