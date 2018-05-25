package hft.wiinf.de.horario.view;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.service.NotificationReceiver;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSettingsFragment extends Fragment {
    private static final String TAG = "SettingFragmentActivity";
    EditText editTextUsername;
    Person person;
    Spinner spinner_pushMinutes;
    Switch switch_enablePush;
    TextView textView_minutesBefore, textView_reminder;

    public SettingsSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_settings, container, false);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //if the user is in the db read the user from db, else create a new one
        try {
            person = PersonController.getPersonWhoIam();
            if (person == null)
                //TODO: read real phone number
                person = new Person(true, "007", "");
        } catch (NullPointerException e) {
            Log.d(TAG, "SettingsActivity:" + e.getMessage());
        }
        editTextUsername = view.findViewById(R.id.settings_settings_editText_username);
        textView_minutesBefore = view.findViewById(R.id.settings_settings_textView_minutesBefore);
        textView_reminder = view.findViewById(R.id.settings_settings_textView_reminder);
        switch_enablePush = view.findViewById(R.id.settings_settings_Switch_allowPush);
        spinner_pushMinutes = view.findViewById(R.id.settings_settings_spinner_minutes);
        switch_enablePush.setChecked(person.isEnablePush());
        //save a change of the switch in the db and change visibility of the minutes spinner and textview
        switch_enablePush.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch_enablePush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        person.setEnablePush(isChecked);
                        pushNotificationVisibility();
                        PersonController.savePerson(person);
                        if (!isChecked) {
                            Toast.makeText(getContext(), R.string.pushDisabled, Toast.LENGTH_SHORT).show();
                            deleteAllAlarms();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.pushMinutesSet, person.getNotificationTime()), Toast.LENGTH_SHORT).show();
                            startAlarmForAllEvents();
                        }
                    }
                });
                return false;
            }
        });


        spinner_pushMinutes.setSelection(getItemPosition());


        // set the user name of the person (empty string if no person set)
        editTextUsername.setText(person.getName());

        //Make EditText-Field editable
        editTextUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextUsername.setFocusable(true);
                editTextUsername.setFocusableInTouchMode(true);
                return false;
            }
        });
        switch_enablePush.setChecked(person.isEnablePush());
        pushNotificationVisibility();
        //Everything that needs to happen after Username was written in the EditText-Field
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(inputText);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    //ToDo: get correct phoneNumber
                    person.setName(inputText);
                    PersonController.addPersonMe(person);
                    Toast toast = Toast.makeText(view.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    editTextUsername.setFocusable(false);
                    editTextUsername.setFocusableInTouchMode(false);
                } else {
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    editTextUsername.setText(person.getName());
                    return true;
                }
                return false;
            }
        });

        // set the choice possibilities of the push minutes dropdown
        ArrayAdapter minutesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.push_times, android.R.layout.simple_spinner_item);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pushMinutes.setAdapter(minutesAdapter);
        //set the choice selection - if there is something in db saved
        spinner_pushMinutes.setSelection(getItemPosition());
        //if something is selected of the spinner, update the person
        spinner_pushMinutes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinner_pushMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String s = (String) parent.getItemAtPosition(position);
                        int minutes = Integer.parseInt(s);
                        person.setNotificationTime(minutes);
                        PersonController.savePerson(person);
                        startAlarmForAllEvents();
                        Toast.makeText(getContext(), getString(R.string.pushMinutesSet, minutes), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });
                return false;
            }
        });
    }

    //if the switch is not selected dont show the minutes textview and textedit
    private void pushNotificationVisibility() {
        if (person.isEnablePush()) {
            textView_reminder.setVisibility(View.VISIBLE);
            textView_minutesBefore.setVisibility(View.VISIBLE);
            spinner_pushMinutes.setVisibility(View.VISIBLE);
        } else {
            textView_reminder.setVisibility(View.GONE);
            textView_minutesBefore.setVisibility(View.GONE);
            spinner_pushMinutes.setVisibility(View.GONE);
        }
    }

    //return the correct item position based of the saved pushminutes
    private int getItemPosition() {
        switch (person.getNotificationTime()) {
            case 0:
                return 0;
            case 5:
                return 1;
            case 15:
                return 2;
            case 30:
                return 3;
            case 60:
                return 4;
            case 90:
                return 5;
            case 120:
                return 6;
            default:
                return 0;

        }
    }

    public void deleteAllAlarms() {
        //Get all events that are in the future to set the alarm
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        for (Event event : allEvents) {
            Intent alarmIntent = new Intent(getContext(), NotificationReceiver.class);

            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), event.getId().intValue(), alarmIntent, 0);

            //Set AlarmManager --> NotificationReceiver will be called
            AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }
    }

    public void startAlarmForAllEvents() {
        //Get all events that are in the future to set the alarm
        List<Event> allEvents = EventController.findMyAcceptedEventsInTheFuture();
        for (Event event : allEvents) {
            Intent alarmIntent = new Intent(getContext(), NotificationReceiver.class);

            //Get startTime an convert into a Calender to use it
            Date date = event.getStartTime();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            //Put extra Data which is needed for the Notification
            alarmIntent.putExtra("Event", event.getDescription());
            alarmIntent.putExtra("Hour", calendar.get(Calendar.HOUR_OF_DAY));
            if (calendar.get(Calendar.MINUTE) <= 10) {
                alarmIntent.putExtra("Minute", "0" + String.valueOf(calendar.get(Calendar.MINUTE)));
            } else {
                alarmIntent.putExtra("Minute", String.valueOf(calendar.get(Calendar.MINUTE)));
            }
            alarmIntent.putExtra("ID", event.getId().intValue());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), event.getId().intValue(), alarmIntent, 0);

            //Set AlarmManager --> NotificationReceiver will be called
            AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calcNotificationTime(calendar, person), pendingIntent);
        }
    }

    public long calcNotificationTime(Calendar cal, Person person) {
        cal.add(Calendar.MINUTE, ((-1) * person.getNotificationTime()));
        return cal.getTimeInMillis();
    }
}