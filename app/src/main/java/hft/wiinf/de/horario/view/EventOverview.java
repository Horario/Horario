package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hft.wiinf.de.horario.R;

public class EventOverview extends Activity {

    ListView overviewLvList; //TODO Format der ListView ändern um MockUps zu entsprechen
    TextView overviewTvMonth;
    DateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.GERMAN);
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_overview);

        overviewLvList = findViewById(R.id.overviewLvList);
        overviewTvMonth = findViewById(R.id.overviewTvMonth);

        overviewTvMonth.setText(monthFormat.format(CalendarActivity.selectedMonth));
        overviewLvList.setAdapter(iterateOverMonth());
    }

    public ArrayAdapter iterateOverMonth(){
        ArrayList<String> eventArray = new ArrayList<>();
        Date day = CalendarActivity.selectedMonth;
        int endDate = CalendarActivity.selectedMonth.getMonth();
        while (day.getMonth() <= endDate){
            eventArray.add(df.format(day));
            day.setTime(day.getTime() + 86400000);
            //TODO Termine aus der DB wählen die am jeweiligen Tag stattfinden
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventArray);
        return adapter;
    }
}
