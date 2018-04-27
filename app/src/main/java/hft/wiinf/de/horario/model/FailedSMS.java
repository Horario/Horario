package hft.wiinf.de.horario.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="FailedSMS")
public class FailedSMS extends Model{

    @Column(name="event")
    private Event event = null;
    @Column(name="creator")
    private Person creator = null;

    public FailedSMS(){

    }

    public FailedSMS(Event event, Person creator){
        this.event = event;
        this.creator = creator;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }
}
