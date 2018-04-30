package hft.wiinf.de.horario.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.FailedSMSController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.FailedSMS;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.service.FailedSMSService;
import hft.wiinf.de.horario.utility.BundleUtlity;

public class SettingsActivity extends Fragment {

    private static final String TAG = "SettingFragmentActivity";
    Button button_settings, button_support, button_copyright, button_feedback;
    RelativeLayout rLayout_main, rLayout_settings, rLayout_support, rLayout_copyright, rLayout_feedback;
    EditText editTextUsername;
    Person person;

    String SENT = "SMS_SENT";
    PendingIntent sentPI;
    BroadcastReceiver smsSentReceiver;
    Bundle sms;
    String phoneNo = "00000";
    String message = "Hellodedededede";

    private static final int SEND_SMS_PERMISSION_CODE = 1;

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

        sentPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);

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
                sendSMS();
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

    @Override
    public void onResume() {
        super.onResume();
        //Receiver will get message about what happened to the SMS and than do sth depending on the result
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), R.string.sms_sent, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
                        startJobSendSMS(phoneNo, message);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
                        startJobSendSMS(phoneNo, message);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
                        startJobSendSMS(phoneNo, message);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
                        startJobSendSMS(phoneNo, message);
                        break;
                }
            }
        };
        getActivity().registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(smsSentReceiver);
    }


    public void sendSMS() {
        //Check if User has permission to send sms
        if (!isSendSmsPermissionGranted()) {
            requestSendSmsPermission();
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, "Heyho", sentPI, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSendSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSendSmsPermission() {
        //For Fragment: requestPermissions(permissionsList,REQUEST_CODE);
        //For Activity: ActivityCompat.requestPermissions(this,permissionsList,REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_CODE);
    }


    //getActivity().registerReceiver(smsSentReceiver, new IntentFilter(SENT));

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager manager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    if (manager.getPhoneType() == TelephonyManager.DATA_CONNECTED) {
                        sendSMS();
                    } else {
                        Toast.makeText(getContext(), R.string.sms_fail, Toast.LENGTH_SHORT).show();
                        startJobSendSMS(phoneNo, message);
                    }
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

    public void startJobSendSMS(String phoneNo, String message) {
        FailedSMS failedSMS = new FailedSMS(message, phoneNo, 1, false);
        saveFailedSMS(failedSMS);

        sms = new Bundle();
        sms.putString("phoneNo", phoneNo);
        sms.putString("message", message);
        sms.putInt("creatorID", 1);
        sms.putBoolean("accepted", false);
        sms.putInt("id", failedSMS.getId().intValue());

        PersistableBundle persBund = BundleUtlity.toPersistableBundle(sms);
        JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(failedSMS.getId().intValue(), new ComponentName(getActivity(), FailedSMSService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(persBund)
                .setPersisted(true)
                .build());
    }

    public void saveFailedSMS(FailedSMS failedSMS) {
        FailedSMSController.addFailedSMS(failedSMS);
    }
}


