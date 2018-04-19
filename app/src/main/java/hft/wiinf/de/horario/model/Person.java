package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

//class for a person with can (not) participate at an appointment or be the creator of an appointment
@Table(name="persons")
public class Person extends Model{
    @Column
    String phoneNumber = "";
    @Column
    String name="";
    @Column(name = "event_Accepted")
    private Long acceptedEvent = null;
    @Column(name = "event_canceled")
    private Long canceledEvent = null;

    //getter-setter
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    void setCanceledEvent(Long canceledEvent) {
        this.canceledEvent = canceledEvent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setAcceptedEvent(Long acceptedEvent) {
        this.acceptedEvent = acceptedEvent;
    }


}
