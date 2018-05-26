package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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

import static android.Manifest.permission.READ_SMS;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSettingsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "SettingFragmentActivity";
    EditText editTextUsername,editText_PhoneNumber;
    Person person;
    Spinner spinner_pushMinutes;
    Switch switch_enablePush;
    TextView textView_minutesBefore, textView_reminder;
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    private int counter = 0;

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
        person = PersonController.getPersonWhoIam();
        if (person == null)
            person = new Person(true, "", "");
        editTextUsername = view.findViewById(R.id.settings_settings_editText_username);
        textView_minutesBefore = view.findViewById(R.id.settings_settings_textView_minutesBefore);
        textView_reminder = view.findViewById(R.id.settings_settings_textView_reminder);
        switch_enablePush = view.findViewById(R.id.settings_settings_Switch_allowPush);
        spinner_pushMinutes = view.findViewById(R.id.settings_settings_spinner_minutes);
        editText_PhoneNumber = view.findViewById(R.id.settings_settings_editText_phoneNumber);
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
        editText_PhoneNumber.setText(person.getPhoneNumber());

        //Make EditText-Field editable
        editTextUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextUsername.setFocusable(true);
                editTextUsername.setFocusableInTouchMode(true);
                return false;
            }
        });
        //Make EditText-Field editable
        editText_PhoneNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText_PhoneNumber.setFocusable(true);
                editText_PhoneNumber.setFocusableInTouchMode(true);
                return false;
            }
        });
        switch_enablePush.setChecked(person.isEnablePush());
        pushNotificationVisibility();
        //Everything that needs to happen after Username was written in the EditText-Field
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //on click: read out the textfield, ask for phone number and close the keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString();
                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(inputText);
                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    person.setName(inputText);
                    concatenateAndSavePersonData();
                    editTextUsername.setFocusableInTouchMode(false);
                    editTextUsername.setFocusable(false);
                    return false;
                } else {
                    //if the user name is not valid show a toast
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    editTextUsername.setText(person.getName());
                    return true;
                }
            }
        });
        //Everything that needs to happen after phone number was written in the EditText-Field
        editText_PhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString().replaceAll(" ", "");
                //on click: read out the textfield, save the personand close the keyboard
                //regex: perhaps + then numbers
                if (actionId == EditorInfo.IME_ACTION_DONE && inputText.matches("\\+?[0-9]+")) {
                    person.setPhoneNumber(editText_PhoneNumber.getText().toString().replaceAll(" ", ""));
                    editText_PhoneNumber.setText(person.getPhoneNumber());
                    PersonController.savePerson(person);
                      editText_PhoneNumber.setFocusable(false);
                      editText_PhoneNumber.setFocusableInTouchMode(false);
                    Toast.makeText(getContext(), R.string.phoneNumberSaved, Toast.LENGTH_SHORT).show();
                } else {
                    //show a toast if the number doe not atart with 0 or +
                    Toast.makeText(view.getContext(), R.string.wrongNumberFormat, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
// set the choice posibilities of the push minutes dropdown
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
                        Toast.makeText(getContext(), getString(R.string.pushMinutesSet, minutes), Toast.LENGTH_SHORT).show();
                        startAlarmForAllEvents();
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

    // method to read the phone number of the user
    public void concatenateAndSavePersonData() {

        if (person.getPhoneNumber() == null || person.getPhoneNumber().equalsIgnoreCase("")) {
            if (checkSelfPermission(getActivity(), READ_SMS) != PackageManager.PERMISSION_GRANTED)
                requestPermission();
            else {
                //if permission is granted read the phone number
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                String phoneNumber = telephonyManager.getLine1Number();
                if (phoneNumber != null)
                    phoneNumber.replaceAll(" ", "");
                person.setPhoneNumber(phoneNumber);
                //if the number could not been read, open a dialog
                if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("(00|0|\\+)[1-9][0-9]+"))
                    Toast.makeText(getContext(),R.string.PhoneNumberCouldNotBeenRead,Toast.LENGTH_SHORT);
                else {
                    PersonController.addPersonMe(person);
                    Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            PersonController.addPersonMe(person);
            Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();

        }
    }

    //request permission send sms
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SEND_SMS);
    }

    //react on accept or deny of permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            //check if the user granted/denied them you may want to group the
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS);
                if (!showRationale) {
                    // user also CHECKED "never ask again" - show dialog
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.accessWith_NeverAskAgain_deny)
                            .setMessage(R.string.sendSMS_accessDenied_withCheckbox)
                            .setPositiveButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editText_PhoneNumber.requestFocusFromTouch();
                                }
                            })
                            .create().show();
                } else if (counter < 1) {
                    // user did NOT check "never ask again" this is a good place to explain the user
                    // why you need the permission and ask if he wants // to accept it (the rationale)
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.requestPermission_firstTryRequest)
                            .setMessage(R.string.phoneNumber_explanation)
                            .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    counter++;
                                    concatenateAndSavePersonData();
                                }
                            })
                            .setNegativeButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editText_PhoneNumber.requestFocusFromTouch();
                                }
                            })
                            .create().show();
                } else if (counter == 1) {
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.sendSMS_lastTry)
                            .setMessage(R.string.phoneNumber_explanation)
                            .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    counter++;
                                    concatenateAndSavePersonData();
                                }
                            })
                            .setNegativeButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editText_PhoneNumber.requestFocusFromTouch();
                                }
                            })
                            .create().show();
                } else {
                    editText_PhoneNumber.requestFocusFromTouch();
                }
            } else {
                concatenateAndSavePersonData();
            }

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




