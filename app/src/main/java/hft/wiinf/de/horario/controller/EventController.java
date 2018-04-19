package hft.wiinf.de.horario.controller;

import hft.wiinf.de.horario.model.Event;

public class EventController {

    public static void addEvent(Event event){
        event.save();
    }
}
