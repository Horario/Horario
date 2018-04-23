package hft.wiinf.de.horario.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.controller.RepetitiondateController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.model.Repetitiondate;

//TODO Kommentieren und Java Doc Info Schreiben
public class NewEventActivity extends Fragment {
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    Calendar endOfRepetition = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_event, container, false);
        return view;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.newEvent_editText_Date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });
        getView().findViewById(R.id.newEvent_editText_startTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartTime();
            }
        });
        getView().findViewById(R.id.newEvent_textEdit_endTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndTime();
            }
        });

        getView().findViewById(R.id.newEvent_checkBox_SerialEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSerialEvent();
            }
        });
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.event_repetitions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) getView().findViewById(R.id.newEvent_spinner_repetition)).setAdapter(adapter);
        getView().findViewById(R.id.newEvent_textEdit_endOfRepetition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndOfRepetition();
            }
        });
        getView().findViewById(R.id.newEvent_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void checkSerialEvent() {
        boolean isSerialEvent = ((CheckBox) getView().findViewById(R.id.newEvent_checkBox_SerialEvent)).isChecked();
        if (isSerialEvent) {
            getView().findViewById(R.id.newEvent_textEdit_endOfRepetition).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.newEvent_textView_endOfRepetiton).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.newEvent_spinner_repetition).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.newEvent_textView_repetition).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.newEvent_textEdit_endOfRepetition).setVisibility(View.GONE);
            getView().findViewById(R.id.newEvent_textView_endOfRepetiton).setVisibility(View.GONE);
            getView().findViewById(R.id.newEvent_spinner_repetition).setVisibility(View.GONE);
            getView().findViewById(R.id.newEvent_textView_repetition).setVisibility(View.GONE);
        }
    }

    public void getDate() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                startTime.set(year, month, dayOfMonth);
                endTime.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                ((EditText) getView().findViewById(R.id.newEvent_editText_Date)).setText(format.format(startTime.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), listener, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void getStartTime() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);

                DateFormat format = new SimpleDateFormat("HH:mm");
                ((EditText) (getView().findViewById(R.id.newEvent_editText_startTime))).setText(format.format(startTime.getTime()));
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, startTime.get(Calendar.HOUR), startTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndTime() {
        Toast.makeText(this.getContext(), "START", Toast.LENGTH_LONG);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTime.set(Calendar.MINUTE, minute);
                endOfRepetition.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endOfRepetition.set(Calendar.MINUTE, minute);
                DateFormat format = new SimpleDateFormat("HH:mm");
                ((EditText) getView().findViewById(R.id.newEvent_textEdit_endTime)).setText(format.format(endTime.getTime()));
            }
        };
        System.out.println("BEFORE");
        Toast.makeText(this.getContext(), "before Dialog", Toast.LENGTH_LONG);
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, endTime.get(Calendar.HOUR), endTime.get(Calendar.MINUTE), true);
        Toast.makeText(this.getContext(), "before Show", Toast.LENGTH_LONG);
        dialog.show();
        Toast.makeText(this.getContext(), "shown", Toast.LENGTH_LONG);
    }

    public void getEndOfRepetition() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                endOfRepetition.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                ((EditText) getView().findViewById(R.id.newEvent_textEdit_endOfRepetition)).setText(format.format(endOfRepetition.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), listener, endOfRepetition.get(Calendar.YEAR), endOfRepetition.get(Calendar.MONTH), endOfRepetition.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    public void saveEvent() {
        Event event = new Event(PersonController.getPersonWhoIam());
        event.setAccepted(true);
        event.setDescription(((EditText) getView().findViewById(R.id.newEvent_editText_description)).getText().toString());
        event.setStartTime(startTime.getTime());
        event.setEndTime(endTime.getTime());
        event.setRepetition(getRepetition());
        if (event.getRepetition() != Repetition.NONE)
            RepetitiondateController.saveRepetitiondates(setRepetitionDate(event), event);
        event.setPlace(((EditText) getView().findViewById(R.id.newEvent_textEdit_room)).getText().toString());
        EventController.saveEvent(event);

    }

    private Repetition getRepetition() {
        if (!((CheckBox) getView().findViewById(R.id.newEvent_checkBox_SerialEvent)).isChecked()) {
            return Repetition.NONE;
        }
        switch ((((int) ((Spinner) getView().findViewById(R.id.newEvent_spinner_repetition)).getSelectedItemPosition()))) {
            case 0:
                return Repetition.YEARLY;
            case 1:
                return Repetition.MONTHLY;
            case 2:
                return Repetition.WEEKLY;
            default:
                return Repetition.DAILY;

        }
    }

    private List<Repetitiondate> setRepetitionDate(Event event) {
        int fieldNumber;
        switch (event.getRepetition()) {
            case DAILY:
                fieldNumber = Calendar.DAY_OF_MONTH;
                break;
            case WEEKLY:
                fieldNumber = Calendar.WEEK_OF_YEAR;
                break;
            case MONTHLY:
                fieldNumber = Calendar.MONTH;
                break;
            default:
                fieldNumber = Calendar.YEAR;
        }
        Calendar calendar = startTime;
        List<Repetitiondate> date = new LinkedList<>();
        calendar.add(fieldNumber, 1);
        while (calendar.before(endOfRepetition)) {
            calendar.add(fieldNumber, 1);
            date.add(new Repetitiondate(calendar.getTime()));
        }
        return date;
    }
}