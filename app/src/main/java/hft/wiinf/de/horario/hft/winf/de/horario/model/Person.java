package hft.wiinf.de.horario.hft.winf.de.horario.model;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.orm.SugarRecord;
//class for a person with can (not) participate at an appointment or be the creator of an appointment
@Table(name="persons")
public class Person extends Model{
    @Column
    String phoneNumber = "";
    @Column()
    String name="";

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
