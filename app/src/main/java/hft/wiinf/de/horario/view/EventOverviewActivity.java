package hft.wiinf.de.horario.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;

public class EventOverviewActivity extends AppCompatActivity {

    ListView overviewLvList;
    TextView overviewTvMonth;
    Button overviewBtNext;
    Button overviewBtPrevious;
    Date selectedMonth = new Date(CalendarActivity.selectedMonth.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize variables
        setContentView(R.layout.activity_event_overview);
        overviewLvList = findViewById(R.id.overviewTvList);
        overviewTvMonth = findViewById(R.id.overviewTvMonth);
        overviewBtNext = findViewById(R.id.overviewBtNext);
        overviewBtPrevious = findViewById(R.id.overviewBtPrevious);

        update();


        overviewBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMonth.setMonth(selectedMonth.getMonth()+1);
                update();
            }
        });

        overviewBtPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMonth.setMonth(selectedMonth.getMonth()-1);
                update();
            }
        });
    }

    public void update(){
        overviewTvMonth.setText(CalendarActivity.monthFormat.format(selectedMonth));
        overviewLvList.setAdapter(iterateOverMonth(selectedMonth));
    }

    //get all events for the selected month and save them in a adapter
    public ArrayAdapter iterateOverMonth(Date date){ //TODO create own Adapter
        ArrayList<String> eventArray = new ArrayList<>();
        Date day = new Date(date.getTime());
        int endDate = date.getMonth();
        while (day.getMonth() <= endDate){
            Calendar endOfDay = Calendar.getInstance();
            endOfDay.setTime(day);
            endOfDay.add(Calendar.DAY_OF_MONTH, 1);
            List<hft.wiinf.de.horario.model.Event> eventList = EventController.findEventsByTimePeriod(day, endOfDay.getTime());
            if (eventList.size()>0){
                eventArray.add(CalendarActivity.dayFormat.format(day));
            }
            for (int i = 0; i<eventList.size(); i++){
                eventArray.add(eventList.get(i).getDescription());
            }
            //TODO was wenn keine Termine in diesem Monat sind, irgendeine Message anzeigen? Abhandeln über eventArray size
            day.setTime(endOfDay.getTimeInMillis());
        }
        if(eventArray.size() < 1){ //when no events this month do stuff
            eventArray.add("Du hast keine Termine diesen Monat");
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, eventArray);
        return adapter;
    }
}
