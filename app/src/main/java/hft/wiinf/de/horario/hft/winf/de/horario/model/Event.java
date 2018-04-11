package hft.wiinf.de.horario.hft.winf.de.horario.model;


import com.orm.SugarRecord;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


// Class for both standard and serial event

public class Event extends SugarRecord<Event> {

    private Person creator = new Person();

    private String description;

    private Date start = new Date();

    private Date end = new Date();

    private boolean accepted = false;

    private List<Person> personAccepted = new LinkedList();

    private List<Person> personCancelled = new LinkedList<>();

    private Repetition repetition = Repetition.NONE;

    private Date endDate = null;

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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
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
}

