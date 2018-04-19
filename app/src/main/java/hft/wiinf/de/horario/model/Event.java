package hft.wiinf.de.horario.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


// Class for both standard and serial event
//start and enddate are both dates, not as in the gui one date and two times

@Table(name="events")
public class Event extends Model {
    @Column
    private Person creator = new Person();
    @Column
    private String description;
    @Column
    private String place;
    @Column
    private Date startTime = new Time(0);
    @Column
    private Date endTime = new Time(0);
    @Column
    private boolean accepted = false;

    @Column
    private Repetition repetition = Repetition.NONE;

    private List<Repetitiondate> repetitiondates = new LinkedList<>();


    //get all events that start between the start and enddate (both including) or serial events that have a repetition there
    public static List<Event> findEventByTimePeriod(Date startDate, Date endDate) {
        List<Event> events = new Select().from(Event.class).leftJoin(Repetitiondate.class).on("events.id=repetitiondates.event_id").where("starttime between ? AND ?", startDate.getTime(), endDate.getTime()).or("date BETWEEN ? AND ?", startDate.getTime(), endDate.getTime()).execute();
        return events;
    }

//getter-setter

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
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


    public List<Person> getPersonAccepted() {
        return getMany(Person.class, "event_accepted");
    }

    public void setPersonAccepted(List<Person> personAccepted) {
        if (getId() != null && this.getPersonAccepted() != null && this.getPersonAccepted().size() > 0)
            new Delete().from(Person.class).where("event_accepted=?", getId());
        for (Person p : personAccepted) {
            p.setAcceptedEvent(getId());
            p.save();
        }
    }


    public List<Person> getPersonCancelled() {
        return getMany(Person.class, "event_canceled");
    }

    public void setPersonCancelled(List<Person> personCancelled) {
        new Delete().from(Person.class).where("event_canceled=?", getId());
        for (Person p : personCancelled) {
            p.setAcceptedEvent(getId());
            p.save();
        }
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }

    public List<Repetitiondate> getRepetitionDates() {
        return getMany(Repetitiondate.class, "event_id");
    }

    public void setRepetitionDates(List<Repetitiondate> repetitionDates) {
        if (getId() == null || getId() > 0)
            this.save();
        if (this.repetitiondates != null && this.repetitiondates.size() > 1)
            new Delete().from(Repetitiondate.class).where("event_id=?", getId());
        for (Repetitiondate date : repetitionDates) {
            date.setEventId(getId());
            date.save();
        }

    }

}

