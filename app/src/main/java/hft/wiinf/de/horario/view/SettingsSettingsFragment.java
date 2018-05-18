package hft.wiinf.de.horario.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

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
    private static final int PERMISSION_REQUEST_TELEPHONE_STATE = 0;
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
                        if (!isChecked)
                            Toast.makeText(getContext(), R.string.pushDisabled, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), getString(R.string.pushMinutesSet, person.getNotificationTime()), Toast.LENGTH_SHORT).show();
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
                if (actionId == EditorInfo.IME_ACTION_DONE && !inputText.matches(" .*")) {
                    person.setName(inputText);
                    readOwnPhoneNumber();
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
    public void readOwnPhoneNumber() {
        if (checkSelfPermission(getActivity(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            requestPermission();
        else {
            //if permission is granted read the phone number
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            person.setPhoneNumber(telephonyManager.getLine1Number());
            //if the number could not been read, open a dialog
            if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("[0+].*"))
                openDialogAskForPhoneNumber();
            else {
                PersonController.addPersonMe(person);
                Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_TELEPHONE_STATE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_TELEPHONE_STATE: {
                // If Permission ist Granted User get a SnackbarMessage and the phone number is read
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(getView().findViewById(R.id.setting_settings_relativeLayout_buttonFrame),
                            R.string.thanksphoneNumber,
                            Snackbar.LENGTH_SHORT).show();
                    readOwnPhoneNumber();
                } else {
                    //If the User denies the access to the phone number he gets two Chance to accept the Request
                    //The Counter counts from 0 to 2. If the Counter is 2 user a dialog is shown where the user can input the phone number
                    switch (counter) {
                        case 0:
                            Snackbar.make(getActivity().findViewById(getView().getId()),
                                    R.string.phoneNumber_explanation,
                                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.oneMoreTime, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    readOwnPhoneNumber();
                                }
                            }).show();
                            break;

                        case 1:
                            Snackbar.make(getActivity().findViewById(getView().getId()),
                                    R.string.lastTry_phoneNumber,
                                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.oneMoreTime, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    readOwnPhoneNumber();
                                }
                            }).show();
                            break;
                        default:
                            openDialogAskForPhoneNumber();
                    }
                }
            }
        }
    }

    public void openDialogAskForPhoneNumber() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(R.layout.dialog_askingfortelephonenumber);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        EditText phoneNumber = alertDialog.findViewById(R.id.dialog_EditText_telephonNumber);
        if (person.getPhoneNumber() != null)
            phoneNumber.setText(person.getPhoneNumber());
        phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = v.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (input.matches("[\\+0].+")) {
                        alertDialog.dismiss();
                        person.setPhoneNumber(input);
                        PersonController.addPersonMe(person);
                        Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast toast = Toast.makeText(v.getContext(), R.string.wrongNumberFormat, Toast.LENGTH_SHORT);
                        toast.show();
                        return false;
                    }
                }
                return false;
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast toast = Toast.makeText(getContext(), R.string.UsernameNotSaved, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}



