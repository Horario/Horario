package hft.wiinf.de.horario.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


@Table(name = "repetitiondates")
public class Repetitiondate extends Model {
    @Column
    private Date date;
    @Column(name = "event_id")
    private Long eventId = null;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repetitiondate)) return false;
        if (!super.equals(o)) return false;

        Repetitiondate that = (Repetitiondate) o;

        if (!date.equals(that.date)) return false;
        return eventId.equals(that.eventId);
    }
}

