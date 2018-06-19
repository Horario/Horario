package hft.wiinf.de.horario.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSettingsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "SettingFragmentActivity";
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    EditText editTextUsername, editText_PhoneNumber;
    Person person;
    Spinner spinner_notificationTime, spinner_startTab;
    Switch switch_enablePush;
    TextView textView_minutesBefore, textView_reminder;
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
        editText_PhoneNumber = view.findViewById(R.id.settings_settings_editText_phoneNumber);
        spinner_notificationTime = view.findViewById(R.id.settings_settings_spinner_minutes);
        spinner_startTab = view.findViewById(R.id.settings_settings_spinner_startTab);
        switch_enablePush.setChecked(person.isEnablePush());
        // on touch add a selection listener, remove the on touch listener after the first touch
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
                            NotificationController.deleteAllAlarms(getContext());
                        } else {
                            Toast.makeText(getContext(), getString(R.string.pushMinutesSet, person.getNotificationTime()), Toast.LENGTH_SHORT).show();
                            NotificationController.startAlarmForAllEvents(getContext());
                        }
                    }

                });
                switch_enablePush.setOnTouchListener(null);
                return false;
            }
        });


        spinner_notificationTime.setSelection(getItemPositionPushMinutes());
        spinner_startTab.setSelection(person.getStartTab());
        // if the start tab spinenr was touched, add a SelectionListener and remove the touch listener
        spinner_startTab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinner_startTab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        person.setStartTab(position);
                        PersonController.savePerson(person);
                        Toast.makeText(getContext(), getString(R.string.startTabChanged, parent.getSelectedItem()), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner_startTab.setOnTouchListener(null);
                return true;
            }
        });


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
        //write the phone number of the user into the text field (or empty string if number is currently not set)
        editText_PhoneNumber.setText(person.getPhoneNumber());
        //Everything that needs to happen after Username was written in the EditText-Field
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //on click: read out the textfield, ask for phone number and close the keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString();
                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(inputText);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches() && !inputText.contains("|") && !inputText.contains(",")) {
                    person.setName(inputText);
                    PersonController.savePerson(person);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
                    editTextUsername.setFocusableInTouchMode(false);
                    editTextUsername.setFocusable(false);
                    return false;
                } else if (inputText.contains("|")) {
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername_peek, Toast.LENGTH_SHORT);
                    toast.show();
                    //  editTextUsername.setText(person.getName());
                    return true;
                } else if (inputText.contains(",")) {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername_comma, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                } else {
                    //if the user name is not valid show a toast
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            }
        });
        editText_PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("(0|00|\\+)[1-9][0-9]+"))) {
                    if (getActivity().getCurrentFocus() != null) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    checkPhonePermission();
                }
            }
        });
        //Everything that needs to happen after phone number was written in the EditText-Field
        editText_PhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener()

        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString().replaceAll(" ", "");
                //on click: read out the textfield, save the personand close the keyboard
                //regex: perhaps + then numbers
                if (actionId == EditorInfo.IME_ACTION_DONE && inputText.matches("(\\+|00|0)[1-9][0-9]+")) {
                    person.setPhoneNumber(editText_PhoneNumber.getText().toString().replaceAll(" ", ""));
                    editText_PhoneNumber.setText(person.getPhoneNumber());
                    PersonController.savePerson(person);
                    editText_PhoneNumber.setFocusable(false);
                    editText_PhoneNumber.setFocusableInTouchMode(false);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Toast.makeText(getContext(), R.string.thanksphoneNumber, Toast.LENGTH_SHORT).show();

                } else {
                    //show a toast if the number does not fit the regex
                    Toast.makeText(view.getContext(), R.string.wrongNumberFormat, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });


        // set the choice possibilities of the push minutes dropdown
        ArrayAdapter minutesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.push_times, android.R.layout.simple_spinner_item);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_notificationTime.setAdapter(minutesAdapter);
        //set the choice selection - if there is something in db saved
        spinner_notificationTime.setSelection(getItemPositionPushMinutes());
        //if something is selected of the spinner, update the person
        spinner_notificationTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinner_notificationTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String s = (String) parent.getItemAtPosition(position);
                        int minutes = Integer.parseInt(s);
                        person.setNotificationTime(minutes);
                        PersonController.savePerson(person);
                        Toast.makeText(getContext(), getString(R.string.pushMinutesSet, minutes), Toast.LENGTH_SHORT).show();
                        NotificationController.startAlarmForAllEvents(getContext());
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
            spinner_notificationTime.setVisibility(View.VISIBLE);
        } else {
            textView_reminder.setVisibility(View.GONE);
            textView_minutesBefore.setVisibility(View.GONE);
            spinner_notificationTime.setVisibility(View.GONE);
        }
    }

    //return the correct item position based of the saved pushminutes
    private int getItemPositionPushMinutes() {
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


    private void checkPhonePermission() {
        //Check if User has permission to start to scan, if not it's start a RequestLoop
        if (!isPhonePermissionGranted()) {
            requestPhonePermission();
        } else {
            readPhoneNumber();
        }
    }

    private boolean isPhonePermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPhonePermission() {
        //For Fragment: requestPermissions(permissionsList,REQUEST_CODE);
        //For Activity: ActivityCompat.requestPermissions(this,permissionsList,REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
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
                                        //open keyboard
                                        ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    }
                                })
                                .create().show();
                    } else if (counter == 1) {
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle(R.string.sendSMS_lastTry)
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
                                        ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    }
                                })
                                .create().show();
                    } else {
                        ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                } else {
                    readPhoneNumber();
                }
            }

        }


    }


    // method to read the phone number of the user
    public void readPhoneNumber() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            //if permission is granted read the phone number
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String phoneNumber = telephonyManager.getLine1Number();
            //delete spaces and add a plus before the number if it begins without a 0
            if (phoneNumber != null)
                phoneNumber.replaceAll(" ", "");
            if (phoneNumber.matches("[1-9][0-9]+"))
                phoneNumber = "+" + phoneNumber;
            person.setPhoneNumber(phoneNumber);
            if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("(00|0|\\+)[1-9][0-9]+")) {
                Toast.makeText(getContext(), R.string.telephonenumerNotRead, Toast.LENGTH_SHORT).show();
                editText_PhoneNumber.requestFocusFromTouch();
                //open keyboard
                ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                PersonController.savePerson(person);
                Toast.makeText(getContext(), R.string.thanksphoneNumber, Toast.LENGTH_SHORT).show();
                editText_PhoneNumber.setText(phoneNumber);
                editText_PhoneNumber.setFocusable(false);
                editText_PhoneNumber.setFocusableInTouchMode(false);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        } else {
            if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("(00|0|\\+)[1-9][0-9]+")) {
                Toast.makeText(getContext(), R.string.notAbleToReadPhoneNumberCauseOfNoFunctionForThat, Toast.LENGTH_SHORT).show();
                editText_PhoneNumber.requestFocusFromTouch();
                //open keyboard
                ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }
}
