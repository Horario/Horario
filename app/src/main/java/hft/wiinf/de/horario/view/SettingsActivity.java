package hft.wiinf.de.horario.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

public class SettingsActivity extends Fragment {

    private static final String TAG = "SettingFragmentActivity";
    Button button_settings, button_support, button_copyright, button_feedback;
    RelativeLayout rLayout_main, rLayout_settings, rLayout_support, rLayout_copyright, rLayout_feedback;
    EditText editTextUsername;
    Person person;
    private static final int SMS_PERMISSION_CODE = 0;
    String phoneNo = "01729101821";
    String sms = "Hello how are you=";

    public SettingsActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container, false);
        return view;
    }

    //Method will be called directly after View is created
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        try {
            person = PersonController.getPersonWhoIam();
        } catch (NullPointerException e) {
            Log.d(TAG, "SettingsActivity:" + e.getMessage());
        }

        //Initialize all Gui-Elements
        button_settings = (Button) view.findViewById(R.id.settings_button_settings);
        button_support = (Button) view.findViewById(R.id.settings_button_support);
        button_copyright = (Button) view.findViewById(R.id.settings_button_copyright);
        button_feedback = (Button) view.findViewById(R.id.settings_button_feedback);

        rLayout_main = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_main);
        rLayout_copyright = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_copyright);
        rLayout_feedback = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_feedback);
        rLayout_settings = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_settings);
        rLayout_support = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_support);

        editTextUsername = (EditText) view.findViewById(R.id.settings_settings_editText_username);

        //If username is already saved -> pull it from db an set Text equal to it
        if (person != null) {
            editTextUsername.setText(person.getName());
        }

        //Everything that needs to happen after click on "Settings" button
        //set Visibility of mainLayout to Gone and settingsLayout to Visible
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_settings.setVisibility(View.VISIBLE);

                if (person == null) {
                    try {
                        person = PersonController.getPersonWhoIam();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "SettingsActivity:" + e.getMessage());
                    }
                }
                if (person != null) {
                    editTextUsername.setText(person.getName());
                }
            }
        });

        //Everything that needs to happen after click on "Feedback" button
        //set Visibility of mainLayout to Gone and FeedbackLayout to Visible
        button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_feedback.setVisibility(View.VISIBLE);
            }
        });

        //Everything that needs to happen after click on "Copyright" button
        //set Visibility of mainLayout to Gone and copyrightLayout to Visible
        button_copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_copyright.setVisibility(View.VISIBLE);

                //ToDo: Flo: SMS in richtige Datei
                sendSMS(v);

                //End of SMS
            }
        });

        //Everything that needs to happen after click on "Support" button
        //set Visibility of mainLayout to Gone and supportLayout to Visible
        button_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_support.setVisibility(View.VISIBLE);
            }
        });

        //Make EditText-Field editable
        editTextUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextUsername.setFocusable(true);
                editTextUsername.setFocusableInTouchMode(true);
                return false;
            }
        });

        //Everything that needs to happen after Username was written in the EditText-Field
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(inputText);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    if (person != null) {
                        person.setName(inputText);
                        PersonController.addPersonMe(person);
                    } else {
                        //ToDo: get correct phoneNumber
                        person = new Person(true, "007", inputText);
                        PersonController.addPersonMe(person);
                    }
                    Toast toast = Toast.makeText(view.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    editTextUsername.setFocusable(false);
                    editTextUsername.setFocusableInTouchMode(false);
                } else {
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    if (person != null) {
                        editTextUsername.setText(person.getName());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void sendSMS(View v) {
        if (!isSmsPermissionGranted()) {
            requestSendSmsPermission();
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                Toast.makeText(v.getContext().getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(v.getContext().getApplicationContext(),
                        "SMS faild, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}


