package hft.wiinf.de.horario.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.util.Date;

import hft.wiinf.de.horario.R;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView calendarCvCalendar;
    //ListView calendarLvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarCvCalendar = findViewById(R.id.calendarCvCalendar);

        calendarCvCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Toast.makeText(CalendarActivity.this, dateClicked.toString(), Toast.LENGTH_SHORT).show(); //TODO just a placeholder, need to be deleted
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });







    }
}
