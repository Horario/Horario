package hft.wiinf.de.horario.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;

//TODO Kommentieren und Java Doc Info Schreiben
public class NewEventFragment extends Fragment {

    // calendar objects to save the startTime / end Time / endOfRepetition, default: values - today
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    Calendar endOfRepetition = Calendar.getInstance();
    // elements of the gui
    private EditText editText_description, edittext_shortTitle, edittext_room, edittext_date, edittext_startTime, editText_endTime, edittext_userName, editText_endOfRepetition;
    private TextView textView_endofRepetiton;
    private Spinner spinner_repetition;
    private CheckBox checkBox_serialEvent;
    private Button button_save;
    //person object of the user, to get the user name
    private Person me;
    int counter = 0;
    private int PERMISSION_REQUEST_READ_PHONE_STATE = 0;

    @Nullable
    @Override
    //create the view
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        return view;
    }


    public void onViewCreated(final View view, Bundle savedInstanceState) {
        // set the second and millisecond of the calendar objects to 0 as (dates and) times are only compared by hour and minute, seconds dont matter
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        endTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.MILLISECOND, 0);
        endOfRepetition.set(Calendar.SECOND, 0);
        endOfRepetition.set(Calendar.MILLISECOND, 0);
        // get / initialize  the needed gui objects as fields of the class
        edittext_shortTitle = view.findViewById(R.id.newEvent_textEdit_shortTitle);
        editText_description = view.findViewById(R.id.newEvent_editText_description);
        edittext_room = view.findViewById(R.id.newEvent_textEdit_room);
        edittext_date = view.findViewById(R.id.newEvent_editText_Date);
        edittext_startTime = view.findViewById(R.id.newEvent_editText_startTime);
        editText_endTime = view.findViewById(R.id.newEvent_textEdit_endTime);
        edittext_userName = view.findViewById(R.id.unewEvent_textEdit_userName);
        checkBox_serialEvent = view.findViewById(R.id.newEvent_checkBox_SerialEvent);
        spinner_repetition = view.findViewById(R.id.newEvent_spinner_repetition);
        editText_endOfRepetition = view.findViewById(R.id.newEvent_textEdit_endOfRepetition);
        textView_endofRepetiton = view.findViewById(R.id.newEvent_textView_endOfRepetiton);

        button_save = view.findViewById(R.id.newEvent_button_save);

        //field description: multi-line
        editText_description.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText_description.setRawInputType(InputType.TYPE_CLASS_TEXT);

        // when the keyboard is closed after the text edit room, there should be no focus
        edittext_room.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    edittext_room.clearFocus();
                    return true;

                }
                return false;
            }
        });
        //for each fields with a date: 1. don't open keyboard on focus, when it gets focus or the user clicks on the field: open date/time picker and save the date
        edittext_date.setShowSoftInputOnFocus(false);
        edittext_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getDate();
            }
        });
        edittext_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });
        edittext_startTime.setShowSoftInputOnFocus(false);
        edittext_startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getStartTime();
            }
        });
        edittext_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartTime();
            }
        });
        editText_endTime.setShowSoftInputOnFocus(false);
        editText_endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getEndTime();
            }
        });
        editText_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndTime();
            }
        });
        edittext_userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    edittext_userName.clearFocus();
                    return true;

                }
                return false;
            }
        });
        // on click on serial event checkbox change visibility of the repetition and repetition end field,
        checkBox_serialEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSerialEvent();
            }
        });
        // sets the choice possibilities of the repetition spinner (set in string resource-file as array event-repetition)
        ArrayAdapter repetitionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.event_repetitions, android.R.layout.simple_spinner_item);
        //set the appearance of one choice possibility
        repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_repetition.setAdapter(repetitionAdapter);
        //set weekly selected until the user selects something different
        spinner_repetition.setSelection(2);
        //don't open keyboard on focus,
        editText_endOfRepetition.setShowSoftInputOnFocus(false);
        // when it gets focus or the user clicks on the field: open date/time picker and save the date
        editText_endOfRepetition.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    getEndOfRepetition();
            }
        });
        editText_endOfRepetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndOfRepetition();
            }
        });
        // when the keyboard is closed after the text edit room, there should be no focus
        editText_endOfRepetition.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    editText_endOfRepetition.clearFocus();
                    return true;

                }
                return false;
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickSave();
                EventOverviewFragment.update();
                CalendarFragment.updateCompactCalendar();
            }
        });

        if (getArguments() != null) {
            Long eventId = getArguments().getLong("eventId");
            readGivenEvent(eventId);
        }
        //get the user, if it is saved in the db, the user name is read
        me = PersonController.getPersonWhoIam();
        if (me == null)
            me = new Person(true, "", "");
        edittext_userName.setText(me.getName());
    }

    //if the checkbox serial event is checked, repetition possibilities and the endOfrepetition is shown, else not
    private void checkSerialEvent() {
        if (checkBox_serialEvent.isChecked()) {
            textView_endofRepetiton.setVisibility(View.VISIBLE);
            editText_endOfRepetition.setVisibility(View.VISIBLE);
            spinner_repetition.setVisibility(View.VISIBLE);

        } else {
            textView_endofRepetiton.setVisibility(View.GONE);
            editText_endOfRepetition.setVisibility(View.GONE);
            spinner_repetition.setVisibility(View.GONE);

        }
    }

    public void getDate() {
        //close keyboard if it's open
        if (getActivity()!=null&&getActivity().getCurrentFocus() != null) {
            Context ctx = getContext();
            assert ctx!=null;
            InputMethodManager mngr =  (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
           assert mngr!=null;
            mngr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        // create a listener for the date picker dialog: update the date parts (year, month, date) of start and end time with the selected values
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                startTime.set(year, month, dayOfMonth);
                endTime.set(year, month, dayOfMonth);
                //format the choosen time as HH:mm and write it into the date text field
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                edittext_date.setText(format.format(startTime.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), listener, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void getStartTime() {
        //close keyboard if it's open
        if (getActivity()!=null&&getActivity().getCurrentFocus() != null) {
            Context ctx = getContext();
            assert ctx!=null;
            InputMethodManager mngr =  (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert mngr!=null;
            mngr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        // create a listener for the time picker dialog: update the start time with the selected values
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                //format the choosen time as HH:mm and write it into the start time text field
                DateFormat format = new SimpleDateFormat("HH:mm");
                edittext_startTime.setText(format.format(startTime.getTime()));
            }
        };
        //open a time picker to let the user choose a time, use the saved start time as initial value (initial value of startTime: now)
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndTime() {
        //close keyboard if it's open
        if (getActivity()!=null&&getActivity().getCurrentFocus() != null) {
            Context ctx = getContext();
            assert ctx!=null;
            InputMethodManager mngr =  (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert mngr!=null;
            mngr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        // create a listener for the time picker dialog: update the end time and the time for the end of repetition (for the comparing later) with the selected values
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTime.set(Calendar.MINUTE, minute);
                endOfRepetition.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endOfRepetition.set(Calendar.MINUTE, minute);
                //format the choosen time as HH:mm and write it into the end time text field
                DateFormat format = new SimpleDateFormat("HH:mm");
                editText_endTime.setText(format.format(endTime.getTime()));
            }
        };
        //open a time picker to let the user choose a time, use the saved end time as initial value (initial value of endTime: now)
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndOfRepetition() {
        //close keyboard if it's open
        if (getActivity().getCurrentFocus() != null)
            ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        // create a listener for the time picker dialog: update the date part (year, month, day) of the end of repetition with the selected values
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                endOfRepetition.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                editText_endOfRepetition.setText(format.format(endOfRepetition.getTime()));
            }
        };
        //open a date picker to let the user choose a date, use the saved end of repetition as initial value (initial value of endTime: now)
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), listener, endOfRepetition.get(Calendar.YEAR), endOfRepetition.get(Calendar.MONTH), endOfRepetition.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    //if the save button is clicked check the entrys and save the event if everything is ok
    public void onButtonClickSave() {
        if (checkValidity()) {
            if (me.getPhoneNumber() == null || !me.getPhoneNumber().matches("(\\+|0|00)[1-9][0-9]+"))
                checkPhonePermission();
            else
                saveEvent();

        }
    }

    //read the needed parameters / textfield and save the event
    public void saveEvent() {
        //save the new user name
        me.setName(edittext_userName.getText().toString());
        PersonController.savePerson(me);
        Event event = new Event(me);
        event.setAccepted(AcceptedState.ACCEPTED);
        event.setDescription(editText_description.getText().toString());
        event.setStartTime(startTime.getTime());
        event.setEndTime(endTime.getTime());
        event.setShortTitle(edittext_shortTitle.getText().toString());
        event.setRepetition(getRepetition());
        event.setPlace(edittext_room.getText().toString());
        // only save the end of repetition if the repetition is not none, if it's an serial event (repetition not none) save it as an serial event, else as an "normal" event
        if (event.getRepetition() != Repetition.NONE) {
            event.setEndDate(endOfRepetition.getTime());
            EventController.saveSerialevent(event);
        } else
            EventController.saveEvent(event);
        if (!EventController.createdEventsYet()) {
            Long date = System.currentTimeMillis();
            saveReadDate(String.valueOf(date));
        }
        openSavedSuccessfulDialog(event.getId());
        NotificationController.setAlarmForNotification(getContext(), event);
    }

    private void saveReadDate(String date) {
        FileOutputStream outputStream;
        try {
            Context ctx = getContext();
            assert ctx!=null;
            outputStream = getContext().openFileOutput("lastReadDate.txt", Context.MODE_PRIVATE);
            outputStream.write(date.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //clear all entrys and open a dialog where the user can choose what to do next
    private void openSavedSuccessfulDialog(final long eventId) {
        clearEntrys();
        Context ctx = getContext();
        assert ctx!=null;
        final Dialog dialogSavingSuccessful = new Dialog(ctx);
        dialogSavingSuccessful.setContentView(R.layout.dialog_savingsucessfull);
        dialogSavingSuccessful.setCancelable(true);
        dialogSavingSuccessful.show();
        //create a new event: only close the dialog
        dialogSavingSuccessful.findViewById(R.id.savingSuccessful_button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSavingSuccessful.dismiss();
            }
        });

        dialogSavingSuccessful.findViewById(R.id.savingSuccessful_button_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle whichFragment = getArguments();
                dialogSavingSuccessful.dismiss();
                QRGeneratorFragment qrFrag = new QRGeneratorFragment();
                assert whichFragment!=null;
                Bundle bundle = new Bundle();
                bundle.putLong("eventId", eventId);
                bundle.putString("fragment", whichFragment.getString("fragment"));
                qrFrag.setArguments(bundle);
assert whichFragment.getString("fragment")!=null;
assert getActivity()!=null;
                if (whichFragment.getString("fragment").equals("EventOverview")) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.eventOverview_frameLayout, qrFrag, "QrGeneratorEO")
                            .addToBackStack("QrGeneratorEO")
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.calendar_frameLayout, qrFrag, "QrGeneratorCA")
                            .addToBackStack("QrGeneratorCA")
                            .commit();
                }
            }
        });
    }

    //clear all entrys of the text edits and uncheck the serial event
    private void clearEntrys() {
        edittext_shortTitle.setText("");
        editText_description.setText("");
        edittext_room.setText("");
        edittext_date.setText("");
        edittext_startTime.setText("");
        editText_endTime.setText("");
        checkBox_serialEvent.setChecked(false);
        spinner_repetition.setSelected(false);
        editText_endOfRepetition.setText("");
        checkSerialEvent();
    }

    //checks if the entrys are valid and opens a toast if not return value: coolean if everything is ok
    private boolean checkValidity() {
        if (editText_description.getText().toString().equals("") || edittext_shortTitle.getText().toString().equals("") || edittext_date.getText().toString().equals("") || edittext_startTime.getText().toString().equals("") || editText_endTime.getText().toString().equals("") || edittext_userName.getText().toString().equals("") || edittext_room.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getRepetition() != Repetition.NONE && editText_endOfRepetition.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_shortTitle.getText().toString().matches(" +.*")) {
            Toast.makeText(getContext(), R.string.shortTitle_spaces, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_shortTitle.getText().toString().contains("|")) {
            Toast.makeText(getContext(), R.string.shortTitle_peek, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editText_description.getText().toString().matches(" +.*")) {
            Toast.makeText(getContext(), R.string.description_spaces, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editText_description.getText().toString().contains("|")) {
            Toast.makeText(getContext(), R.string.description_peek, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_room.getText().toString().matches(" +.*")) {
            Toast.makeText(getContext(), R.string.place_spaces, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_room.getText().toString().contains("|")) {
            Toast.makeText(getContext(), R.string.room_peek, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_userName.getText().toString().matches(" +.*")) {
            Toast.makeText(getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editText_description.getText().length() > 200) {
            Toast.makeText(getContext(), R.string.description_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_shortTitle.getText().length() > 50) {
            Toast.makeText(getContext(), R.string.shortTitle_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edittext_room.getText().length() > 50) {
            Toast.makeText(getContext(), R.string.room_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        //read the current date and time to compare if the start time is in the past, set seconds and milliseconds to 0 to ensure a ight compare (seonds and milliseconds doesn't matter)
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        if (startTime.before(now)) {
            Toast.makeText(getContext(), R.string.startTime_past, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTime.before(startTime)) {
            Toast.makeText(getContext(), R.string.endTime_before_startTime, Toast.LENGTH_SHORT).show();
            return false;
        }
        //if it is and repeating event and the end of the repetition is before the end time of the first event
        if (getRepetition() != Repetition.NONE && endOfRepetition.before(endTime)) {
            Toast.makeText(getContext(), R.string.endOfRepetition_before_endTime, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //get the right repetition
    private Repetition getRepetition() {
        //if the check box isnt checked return none
        if (!checkBox_serialEvent.isChecked()) {
            return Repetition.NONE;
        }
        switch (spinner_repetition.getSelectedItemPosition()) {
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

    //read the event of the given eventId and set the correct texts of the edit texts
    public void readGivenEvent(long eventId) {
        Event event = EventController.getEventById(eventId);
        if (event != null) {
            edittext_shortTitle.setText(event.getShortTitle());
            editText_description.setText(event.getDescription());
            startTime.setTime(event.getStartTime());
            DateFormat format = new SimpleDateFormat("dd.MM.YYYY");
            edittext_date.setText(format.format(event.getStartTime()));
            format = new SimpleDateFormat("HH:mm");
            edittext_startTime.setText(format.format(event.getStartTime()));
            endTime.setTime(event.getEndTime());
            editText_endTime.setText(format.format(event.getEndTime()));
            edittext_room.setText(event.getPlace());
            checkBox_serialEvent.setChecked(event.getRepetition() != Repetition.NONE);
            checkSerialEvent();
            switch (event.getRepetition()) {
                case YEARLY:
                    spinner_repetition.setSelection(0);
                    break;
                case MONTHLY:
                    spinner_repetition.setSelection(1);
                    break;
                case WEEKLY:
                    spinner_repetition.setSelection(2);
                    break;
                case DAILY:
                    spinner_repetition.setSelection(3);
                    break;
                default:
                    spinner_repetition.setSelected(false);
            }
            endOfRepetition.setTime(event.getEndDate());
            if (endOfRepetition != null) {
                format = new SimpleDateFormat("dd.MM.YYYY");
                editText_endOfRepetition.setText(format.format(endOfRepetition));
            }
        }
    }

    private void checkPhonePermission() {
        //Check if User has permission to start to scan, if not it's start a RequestLoop
        if (!isPhonePermissionGranted()) {
            requestPhonePermission();
        } else {
            readPhoneNumber();
        }
    }

    public boolean isPhonePermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPhonePermission() {
        //For Fragment: requestPermissions(permissionsList,REQUEST_CODE);
        //For Activity: ActivityCompat.requestPermissions(this,permissionsList,REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
            // for each permission check if the user granted/denied them you may want to group the
            // rationale in a single dialog,this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
                    if (!showRationale) {
                        // user also CHECKED "never ask again" you can either enable some fall back,
                        // disable features of your app or open another dialog explaining again the
                        // permission and directing to the app setting

                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle(R.string.accessWith_NeverAskAgain_deny)
                                .setMessage(R.string.sendSMS_accessDenied_withCheckbox)
                                .setPositiveButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openDialogAskForPhoneNumber();
                                    }
                                })
                                .create().show();
                    } else if (counter < 1) {
                        // user did NOT check "never ask again" this is a good place to explain the user
                        // why you need the permission and ask if he wants // to accept it (the rationale)
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle(R.string.requestPermission_firstTryRequest)
                                .setMessage(R.string.phoneNumber_explanation)
                                .setPositiveButton(R.string.oneMoreTime, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter++;
                                        checkPhonePermission();
                                    }
                                })
                                .setNegativeButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openDialogAskForPhoneNumber();
                                    }
                                })
                                .create().show();
                    } else if (counter == 1) {
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle(R.string.sendSMS_lastTry)
                                .setMessage(R.string.lastTry_phoneNumber)
                                .setPositiveButton(R.string.oneMoreTime, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter++;
                                        checkPhonePermission();
                                    }
                                })
                                .setNegativeButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openDialogAskForPhoneNumber();
                                    }
                                })
                                .create().show();
                    } else {
                        openDialogAskForPhoneNumber();
                    }
                } else {
                    readPhoneNumber();
                }
            }

        }


    }


    // method to read the phone number of the user
    public void readPhoneNumber() {
        //if permission is granted read the phone number
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String phoneNumber = telephonyManager.getLine1Number();
        //delete spaces and add a + if phoneNumber starts without a 0
        if (phoneNumber != null)
            phoneNumber = phoneNumber.replaceAll(" ", "");
        if (phoneNumber.matches("[1-9][0-9]+"))
            phoneNumber = "+" + phoneNumber;
        me.setPhoneNumber(phoneNumber);
        //if the number could not been read, open a dialog
        if (me.getPhoneNumber() == null || !me.getPhoneNumber().matches("(00|0|\\+)[1-9][0-9]+")) {
            openDialogAskForPhoneNumber();
        } else {
            Toast.makeText(getContext(), R.string.thanksphoneNumber, Toast.LENGTH_SHORT).show();
            saveEvent();

        }
    }


    public void openDialogAskForPhoneNumber() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        dialogBuilder.setView(R.layout.dialog_askingforphonenumber);
        dialogBuilder.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        EditText phoneNumber = alertDialog.findViewById(R.id.dialog_EditText_telephonNumber);
        phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = v.getText().toString().replaceAll(" ", "");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //regex: perhaps 0 + or 00 then 1-9 then numbers
                    if (input.matches("(0|\\+|00)[1-9][0-9]+")) {
                        alertDialog.dismiss();
                        me.setPhoneNumber(input);
                        Toast.makeText(v.getContext(), R.string.thanksphoneNumber, Toast.LENGTH_SHORT).show();
                        saveEvent();
                        return false;
                    } else {
                        Toast toast = Toast.makeText(v.getContext(), R.string.wrongNumberFormat, Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    }
                }
                return false;
            }
        });
        //if the dialog is canceled save nothing
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast toast = Toast.makeText(getContext(), R.string.event_save_notSuccessful, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}