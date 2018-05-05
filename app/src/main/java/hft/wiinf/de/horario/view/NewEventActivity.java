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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.model.Repetitiondate;

//TODO Kommentieren und Java Doc Info Schreiben
public class NewEventActivity extends Fragment {
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    Calendar endOfRepetition = Calendar.getInstance();
    private EditText editText_description, edittext_shortTitle, edittext_room, edittext_date, edittext_startTime, editText_endTime, editText_endOfRepetition;
    private TextView textView_endofRepetiton, textView_repetition;
    private Spinner spinner_repetition;
    private CheckBox checkBox_serialEvent;
    private Button button_save;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_event, container, false);
        return view;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        edittext_shortTitle = view.findViewById(R.id.newEvent_textView_shortTitle);
        editText_description = view.findViewById(R.id.newEvent_editText_description);
        edittext_room = view.findViewById(R.id.newEvent_textEdit_room);
        edittext_date = view.findViewById(R.id.newEvent_editText_Date);
        edittext_startTime = view.findViewById(R.id.newEvent_editText_startTime);
        editText_endTime = view.findViewById(R.id.newEvent_textEdit_endTime);
        checkBox_serialEvent = view.findViewById(R.id.newEvent_checkBox_SerialEvent);
        spinner_repetition = view.findViewById(R.id.newEvent_spinner_repetition);
        editText_endOfRepetition = view.findViewById(R.id.newEvent_textEdit_endOfRepetition);
        textView_endofRepetiton = view.findViewById(R.id.newEvent_textView_endOfRepetiton);
        spinner_repetition = view.findViewById(R.id.newEvent_spinner_repetition);
        textView_repetition = view.findViewById(R.id.newEvent_textView_repetition);
        button_save = view.findViewById(R.id.newEvent_button_save);
        edittext_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });
        edittext_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartTime();
            }
        });
        editText_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndTime();
            }
        });
        checkBox_serialEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSerialEvent();
            }
        });
        ArrayAdapter repetitionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.event_repetitions, android.R.layout.simple_spinner_item);
        repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_repetition.setAdapter(repetitionAdapter);
        textView_endofRepetiton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndOfRepetition();
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void checkSerialEvent() {
        boolean isSerialEvent = ((CheckBox) getView().findViewById(R.id.newEvent_checkBox_SerialEvent)).isChecked();
        if (isSerialEvent) {
            textView_endofRepetiton.setVisibility(View.VISIBLE);
            editText_endOfRepetition.setVisibility(View.VISIBLE);
            spinner_repetition.setVisibility(View.VISIBLE);
            textView_repetition.setVisibility(View.VISIBLE);
        } else {
            textView_endofRepetiton.setVisibility(View.GONE);
            editText_endOfRepetition.setVisibility(View.GONE);
            spinner_repetition.setVisibility(View.GONE);
            textView_repetition.setVisibility(View.GONE);
        }
    }

    public void getDate() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                startTime.set(year, month, dayOfMonth);
                endTime.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                edittext_date.setText(format.format(startTime.getTime()));
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
                edittext_startTime.setText(format.format(startTime.getTime()));
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, startTime.get(Calendar.HOUR), startTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndTime() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTime.set(Calendar.MINUTE, minute);
                endOfRepetition.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endOfRepetition.set(Calendar.MINUTE, minute);
                DateFormat format = new SimpleDateFormat("HH:mm");
                editText_endTime.setText(format.format(endTime.getTime()));
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, endTime.get(Calendar.HOUR), endTime.get(Calendar.MINUTE), true);
        dialog.show();
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
        if (checkValidity()) {
            Event event = new Event(PersonController.getPersonWhoIam());
            event.setAccepted(AcceptedState.ACCEPTED);
            event.setDescription(editText_description.getText().toString());
            event.setStartTime(startTime.getTime());
            event.setEndTime(endTime.getTime());
            event.setShortTitle(edittext_shortTitle.getText().toString());
            event.setRepetition(getRepetition());
            event.setPlace(edittext_room.getText().toString());
            if (event.getRepetition() != Repetition.NONE) {
                event.setEndDate(endOfRepetition.getTime());
                EventController.saveSerialevent(event);
            }
            EventController.saveEvent(event);
        }
    }

    private boolean checkValidity() {
        if (editText_description.getText().toString().equals("") || edittext_shortTitle.getText().toString().equals("") || edittext_date.getText().toString().equals("") || edittext_startTime.getText().toString().equals("") || editText_endTime.getText().toString().equals("") || edittext_room.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_LONG);
            return false;
        }
        if (endTime.before(startTime)) {
            Toast.makeText(getContext(), R.string.endTime_before_startTime, Toast.LENGTH_LONG);
            return false;
        }
        if (getRepetition() != Repetition.NONE && endOfRepetition.before(endTime)) {
            Toast.makeText(getContext(), R.string.endOfRepetition_before_endTime, Toast.LENGTH_LONG);
            return false;
        }
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