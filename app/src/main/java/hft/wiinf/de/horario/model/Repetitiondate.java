package hft.wiinf.de.horario.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "repetitiondates")
public class Repetitiondate extends Model {
    @Column
    private Date date = new Date(0);
    @Column(name = "event_id")
    private Event event = new Event();

    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public Event getEventId() {
        return event;
    }

    public void setEvent(@NonNull Event event) {
        this.event = event;
    }


}

