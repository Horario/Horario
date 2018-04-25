package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import hft.wiinf.de.horario.R;

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
/*
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
        Event event = new Event(PersonController.getPersonWhoIam());
        event.setAccepted(true);
        event.setDescription(((EditText) getView().findViewById(R.id.newEvent_editText_description)).getText().toString());
        event.setStartTime(startTime.getTime());
        event.setEndTime(endTime.getTime());
        event.setPlace(((EditText) getView().findViewById(R.id.newEvent_textEdit_room)).getText().toString());
        Repetition repetition = getRepetition();
        if (repetition == Repetition.NONE)
            EventController.saveEvent(event);
        else {
            EventController.saveSerialevent(event, repetition, endOfRepetition);
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
    */
}