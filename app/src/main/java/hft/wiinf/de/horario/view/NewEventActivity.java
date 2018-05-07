package hft.wiinf.de.horario.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;

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
        edittext_shortTitle = view.findViewById(R.id.newEvent_textEdit_shortTitle);
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
        checkBox_serialEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSerialEvent();
            }
        });
        ArrayAdapter repetitionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.event_repetitions, android.R.layout.simple_spinner_item);
        repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_repetition.setAdapter(repetitionAdapter);
        editText_endOfRepetition.setShowSoftInputOnFocus(false);
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
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickSave();
            }
        });
        if (getArguments()!=null){
            Long eventId = getArguments().getLong("eventId");
            if (eventId != null)
                readGivenEvent(eventId);
        }
    }

    private void checkSerialEvent() {
        if (checkBox_serialEvent.isChecked()) {
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
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);

                DateFormat format = new SimpleDateFormat("HH:mm");
                edittext_startTime.setText(format.format(startTime.getTime()));
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndTime() {
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
        TimePickerDialog dialog = new TimePickerDialog(this.getContext(), listener, endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void getEndOfRepetition() {
        ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                endOfRepetition.set(year, month, dayOfMonth);
                DateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                editText_endOfRepetition.setText(format.format(endOfRepetition.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this.getContext(), listener, endOfRepetition.get(Calendar.YEAR), endOfRepetition.get(Calendar.MONTH), endOfRepetition.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    public void onButtonClickSave() {
        if (checkValidity()) {
            Person me = PersonController.getPersonWhoIam();
            if (me == null || me.getName().equals(""))
                askforUserName();
            else
                saveEvent();
        }
    }

    public void saveEvent() {
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
        } else
            EventController.saveEvent(event);
        openSavedSuccessfulDialog(event.getId());

    }

    private void openSavedSuccessfulDialog(final long eventId) {
        clearEntrys();
        final Dialog dialogSavingSuccessful = new Dialog(getContext());
        dialogSavingSuccessful.setContentView(R.layout.dialog_savingsucessfull);
        dialogSavingSuccessful.setCancelable(true);
        dialogSavingSuccessful.show();
        dialogSavingSuccessful.findViewById(R.id.savingSuccessful_button_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSavingSuccessful.dismiss();
            }
        });
        dialogSavingSuccessful.findViewById(R.id.savingSuccessful_button_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSavingSuccessful.dismiss();
                QRGeneratorActivity qrFrag= new QRGeneratorActivity();
                Bundle bundle = new Bundle();
                bundle.putLong("eventId",eventId);
                qrFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.newEvent_newFragment, qrFrag)
                        .addToBackStack(null)
                        .commit();
                getView().findViewById(R.id.newEvent_oldFragment).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.newEvent_newFragment).setVisibility(View.VISIBLE);
            }
        });
    }

    private void clearEntrys() {
        edittext_shortTitle.setText("");
        editText_description.setText("");
        edittext_room.setText("");
        edittext_date.setText("");
        edittext_startTime.setText("");
        editText_endTime.setText("");
        checkBox_serialEvent.setChecked(false);
        spinner_repetition.setSelection(0);
        editText_endOfRepetition.setText("");
        checkSerialEvent();
    }


    private void askforUserName() {
        final AlertDialog.Builder dialogAskForUsername = new AlertDialog.Builder(getContext());
        dialogAskForUsername.setView(R.layout.dialog_askforusername);
        dialogAskForUsername.setTitle(R.string.titleDialogUsername);
        dialogAskForUsername.setCancelable(true);

        final AlertDialog alertDialogAskForUsername = dialogAskForUsername.create();
        alertDialogAskForUsername.show();

        EditText username =alertDialogAskForUsername.findViewById(R.id.dialog_EditText_Username);
        alertDialogAskForUsername.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                PersonController.addPersonMe(new Person(true,"007",""));
                saveEvent();
            }
        });
        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String dialog_inputUsername;
                dialog_inputUsername = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(dialog_inputUsername);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    //ToDo: Flo - PhoneNumber
                    Person me = new Person("007", dialog_inputUsername);
                    PersonController.addPersonMe(me);

                    Toast toast = Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();

                    alertDialogAskForUsername.cancel();
                    PersonController.addPersonMe(new Person("007",""));
                    return false;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }

            }
        });
    }


    private boolean checkValidity() {
        if (editText_description.getText().toString().equals("") || edittext_shortTitle.getText().toString().equals("") || edittext_date.getText().toString().equals("") || edittext_startTime.getText().toString().equals("") || editText_endTime.getText().toString().equals("") || edittext_room.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editText_description.getText().length()>500){
            Toast.makeText(getContext(), R.string.description_too_long, Toast.LENGTH_LONG).show();
            return false;
        }
        if (editText_description.getText().length()>100){
            Toast.makeText(getContext(), R.string.shortTitle_too_long, Toast.LENGTH_LONG).show();
            return false;
        }
        if (endTime.before(startTime)) {
            Toast.makeText(getContext(), R.string.endTime_before_startTime, Toast.LENGTH_LONG).show();
            return false;
        }
        if (getRepetition() != Repetition.NONE && endOfRepetition.before(endTime)) {
            Toast.makeText(getContext(), R.string.endOfRepetition_before_endTime, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private Repetition getRepetition() {
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
}