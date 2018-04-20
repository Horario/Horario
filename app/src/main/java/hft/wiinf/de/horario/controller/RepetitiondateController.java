package hft.wiinf.de.horario.controller;

import com.activeandroid.query.Select;

import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Repetitiondate;

public class RepetitiondateController {
    public static void saveRepetitiondates(List<Repetitiondate> repetitiondates, Event event) {
        if (event.getId() != null || event.getId() < 0)
            event.save();
        for (Repetitiondate date : repetitiondates) {
            date.setEvent(event);
            date.save();
        }
    }

    public static void deleteRepetitiondates(List<Repetitiondate> repetitiondates, Event event) {
        for (Repetitiondate date : repetitiondates) {
            date.setEvent(event);
            date.save();
        }
    }

    public static List<Repetitiondate> getRepetitiondatesByEventId(Event event) {
        return new Select().from(Repetitiondate.class).where("event_id=?", event.getId()).execute();
    }
}
}
