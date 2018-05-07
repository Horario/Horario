package hft.wiinf.de.horario.view;

import java.util.ArrayList;

public class OverviewListItems {
    private String date;
    private ArrayList<Appointment> list = new ArrayList<>();

    public OverviewListItems(String date) {
        this.date = date;
    }

    public void createAppointment(String description, String time, String colour){
        Appointment appointment = new Appointment(description, time, colour);
        list.add(appointment);
    }

    public ArrayList<String> toStringList(){
        ArrayList<String> stringList = new ArrayList<>();
        for (int i = 0; i<list.size(); i++){
            stringList.add(list.get(i).getDescription() + " " + list.get(i).getTime());
        }
        return stringList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Appointment> getList() {
        return list;
    }

    public void setList(ArrayList<Appointment> list) {
        this.list = list;
    }
}

class Appointment{
    private String description;
    private String time;
    private String colour;

    public Appointment(String description, String time, String colour) {
        this.description = description;
        this.time = time;
        this.colour = colour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
