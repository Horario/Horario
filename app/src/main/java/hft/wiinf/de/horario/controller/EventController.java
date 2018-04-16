package hft.wiinf.de.horario.controller;

import com.activeandroid.query.Select;
import hft.wiinf.de.horario.hft.winf.de.horario.model.Event;

public class EventController {

    public static void addEvent(Event event){
        event.save();
    }
}
