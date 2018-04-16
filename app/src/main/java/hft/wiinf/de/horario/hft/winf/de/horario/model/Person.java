package hft.wiinf.de.horario.hft.winf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

//class for a person with can (not) participate at an appointment or be the creator of an appointment
@Table(name = "persons")
public class Person extends Model {

    @Column (name = "phoneNumber")
    String phoneNumber = "";
    @Column(name = "name")
    String name = "";
    @Column (name = "isItMe")
    boolean isItMe = false;

    //Use this constructor for person that using this specific app (owner)
    public Person(boolean isItMe, String phoneNumber, String name) {
        super();
        this.isItMe = isItMe;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    //Use this constructor for persons that is not the current(real) user of this specific app
    public Person(String phoneNumber, String name) {
        super();
        this.isItMe = false;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public Person() {
        super();
    }

    public boolean isItMe() {
        return isItMe;
    }

    public void setItMe(boolean itMe) {
        this.isItMe = itMe;
    }

    //getter-setter
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
