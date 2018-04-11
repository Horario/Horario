package hft.wiinf.de.horario.hft.winf.de.horario.model;

import com.orm.SugarRecord;
//class for a person with can (not) participate at an appointment or be the creator of an appointment
class Person extends SugarRecord<Person>{
    String phoneNumber = "";
    String name="";
}
