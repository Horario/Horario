package hft.wiinf.de.horario.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
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
    private Date startTime = new Time(0);
@Column
    private Date endTime = new Time(0);
@Column
    private boolean accepted = false;
@Column
    private List<Person> personAccepted = new LinkedList();
@Column
    private List<Person> personCancelled = new LinkedList<>();
@Column
    private Repetition repetition = Repetition.NONE;
//al dates where the event is repeated
    @Column
    private List<RepetitionDate> repetitionDates = new LinkedList<>();

    public Event(){
    super();
    }

    //TODO Serial Events
    public static List<Event> findEventByTimePeriod (Date startDate, Date endDate){
        List<Model> s = new Select().from(Event.class).where("date < 0").execute();
        return new LinkedList<>();
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
        return personAccepted;
    }

    public void setPersonAccepted(List<Person> personAccepted) {
        this.personAccepted = personAccepted;
    }

    public List<Person> getPersonCancelled() {
        return personCancelled;
    }

    public void setPersonCancelled(List<Person> personCancelled) {
        this.personCancelled = personCancelled;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }

    public List<Date> getRepetitionDates() {
        List<Date> dates = new LinkedList<>();
        for (RepetitionDate repetionDate:this.repetitionDates)
            dates.add(repetionDate.getDate());
        return dates;
    }

    public void setRepetitionDates(List<Date> repetitionDates) {
        repetitionDates.clear();
        for (Date date:repetitionDates)
            repetitionDates.add(date);
    }
}

