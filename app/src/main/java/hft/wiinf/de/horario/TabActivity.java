package hft.wiinf.de.horario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.NoScanResultExceptionController;
import hft.wiinf.de.horario.controller.NotificationController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.controller.ScanResultReceiverController;
import hft.wiinf.de.horario.controller.SendSmsController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.CalendarFragment;
import hft.wiinf.de.horario.view.EventOverviewActivity;
import hft.wiinf.de.horario.view.EventOverviewFragment;
import hft.wiinf.de.horario.view.EventRejectEventFragment;
import hft.wiinf.de.horario.view.SettingsActivity;

import static com.activeandroid.Cache.getContext;

public class TabActivity extends AppCompatActivity implements ScanResultReceiverController {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 0;
    private int PERMISSION_REQUEST_RECEIVE_SMS = 1;
    private int PERMISSION_REQUEST_READ_CONTACTS = 2;
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    private static int startTab;
    private Person personMe;
    Person personEventCreator;

    Event singleEvent;
    //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
    //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName
    private String creatorID, startDate, endDate, startTime, endTime, repetition, shortTitle, place,
            description, eventCreatorName, creatorPhoneNumber;
    private String hourOfDay, minutesOfDay, year, month, day;

    Calendar myStartTime = Calendar.getInstance();
    Calendar myEndTime = Calendar.getInstance();
    Calendar myEndDate = Calendar.getInstance();

    int buttonId = 0;
    private int counter = 0;
    private int counterSMS = 0;
    private int counterCONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Start DB
        ActiveAndroid.initialize(this);
        Stetho.initializeWithDefaults(this);
        //read startTab out of db, default=1(calendar tab)
        personMe = PersonController.getPersonWhoIam();
        if (personMe == null)
            personMe = new Person(true, "", "");
        startTab = personMe.getStartTab();

        mSectionsPageAdapter = new SectionsPageAdapterActivity(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabBarLayout);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_dateview);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_calendarview);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings);


        askForSMSPermissions();


        if (personMe == null || personMe.getName().isEmpty()) {
            openDialogAskForUsername();
        }


        myStartTime.set(Calendar.SECOND, 0);
        myStartTime.set(Calendar.MILLISECOND, 0);
        myEndTime.set(Calendar.SECOND, 0);
        myEndTime.set(Calendar.MILLISECOND, 0);
        myEndDate.set(Calendar.SECOND, 0);
        myEndDate.set(Calendar.MILLISECOND, 0);
    }

    private void askForSMSPermissions() {
        checkSMSPermissions();
    }

    private void checkSMSPermissions() {
        if (!areSMSPermissionsGranted()) {
            requestSMSPermissions();
        } else {
            counterSMS = 5;
            checkContactsPermission();
        }
    }

    public void checkContactsPermission() {
        if (!areContactPermissionsGranted()) {
            requestContactPermissions();
        } else {
            counterCONTACTS = 5;
        }
    }


    private boolean areSMSPermissionsGranted() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean areContactPermissionsGranted() {
        int contacts = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }

    private void requestSMSPermissions() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {

            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_RECEIVE_SMS);
        }
    }

    private void requestContactPermissions() {
        int contacts = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    private void restartApp(String fragmentResource) {
        //check from which Fragment (EventOverview or Calendar) are the Scanner was called
        switch (fragmentResource) {
            case "EventOverview":
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment());
                fr.commit();
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                break;
            case "Calendar":
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction frCA = getSupportFragmentManager().beginTransaction();
                frCA.replace(R.id.calendar_frameLayout, new CalendarFragment());
                frCA.commit();
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                break;
            default:
                Toast.makeText(this, R.string.ups_an_error, Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
        }

    }


    //After Scanning it was opened a Dialog where the user can choose what to do next
    @SuppressLint({"ResourceType", "SetTextI18n"})
    private void openActionDialogAfterScanning(final String qrScannContentResult, final String whichFragmentTag) {
        //Create the Dialog with the GUI Elements initial
        final Dialog afterScanningDialogAction = new Dialog(this);
        afterScanningDialogAction.setContentView(R.layout.dialog_afterscanning);
        afterScanningDialogAction.setCancelable(true);
        afterScanningDialogAction.show();

        TextView qrScanner_result_description = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_textView_description);
        TextView qrScanner_result_headline = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_textView_headline);
        Button qrScanner_reject = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventRecject);
        Button qrScanner_result_eventSave = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSave);
        final Button qrScanner_result_abort = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_about);
        Button qrScanner_result_toCalender = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_toCalender);
        Button qrScanner_result_eventSave_without_assign = afterScanningDialogAction.findViewById(
                (R.id.dialog_qrScanner_button_eventSaveOnly));

        //Set the Cancel and BackToCalenderButtons to Invisible
        qrScanner_result_abort.setVisibility(View.GONE);
        qrScanner_result_toCalender.setVisibility(View.GONE);

        afterScanningDialogAction.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    restartApp(whichFragmentTag);
                    dialog.cancel();
                    return true;
                }
                return false;
            }
        });

        try {
            // Button to Save the Event and send for assent the Event a SMS  to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSave)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonId = 1;
                            if (!checkIfEventIsInPast()) {
                                decideWhatToDo(afterScanningDialogAction);
                            } else {
                                //Restart the TabActivity an Reload all Views
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    });

            //Button to Save the Event but don't send for assent the Event a SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSaveOnly)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonId = 2;
                            if (!checkIfEventIsInPast()) {
                                decideWhatToDo(afterScanningDialogAction);
                            } else {
                                //Restart the TabActivity an Reload all Views
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    });

            //Button to Reject the Event und send a Reject SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventRecject)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonId = 3;
                            if (!checkIfEventIsInPast()) {
                                decideWhatToDo(afterScanningDialogAction);
                            } else {
                                //Restart the TabActivity an Reload all Views
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }
                    });

            //Put StringBuffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName; 10 = phoneNumber;
            String[] eventStringBufferArray = qrScannContentResult.split("\\|");
            creatorID = eventStringBufferArray[0].trim();
            startDate = eventStringBufferArray[1].trim();
            endDate = eventStringBufferArray[2].trim();
            startTime = eventStringBufferArray[3].trim();
            endTime = eventStringBufferArray[4].trim();
            repetition = eventStringBufferArray[5].toUpperCase().trim();
            shortTitle = eventStringBufferArray[6].trim();
            place = eventStringBufferArray[7].trim();
            description = eventStringBufferArray[8].trim();
            eventCreatorName = eventStringBufferArray[9].trim();
            creatorPhoneNumber = eventStringBufferArray[10].trim();

            // There are two SecurityQuery
            // - First this two (unused) Variables are checked to Create an Exception if the Array isn't in the correct Form
            // - Second is the Switch-Case Method. If der no correct repetition String inside
            // it will show an Error an the Cancel Button.
            String creatorID = eventStringBufferArray[0].trim();
            String phoneNummber = eventStringBufferArray[10].trim();


            // Change the DataBase Repetition Information in a German String for the Repetition Element
            // like "Daily" into "täglich" and so on
            switch (repetition) {
                case "YEARLY":
                    repetition = getString(R.string.yearly);
                    break;
                case "MONTHLY":
                    repetition = getString(R.string.monthly);
                    break;
                case "WEEKLY":
                    repetition = getString(R.string.weekly);
                    break;
                case "DAILY":
                    repetition = getString(R.string.daily);
                    break;
                case "NONE":
                    repetition = "";
                    break;
                default:
                    qrScanner_reject.setVisibility(View.GONE);
                    qrScanner_result_eventSave_without_assign.setVisibility(View.GONE);
                    qrScanner_result_eventSave.setVisibility(View.GONE);
                    qrScanner_result_abort.setVisibility(View.VISIBLE);
                    qrScanner_result_description.setText(getString(R.string.wrongQRCodeResult) + "\n" + "\n"
                            + qrScannContentResult + "\n" + "\n" + getString(R.string.notAsEventSaveable));

                    qrScanner_result_toCalender.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            restartApp(whichFragmentTag);
                            afterScanningDialogAction.dismiss();
                        }
                    });

            }

            // Event shortTitle in Headline with eventCreatorName
            qrScanner_result_headline.setText(shortTitle + " " + getString(R.string.from) + eventCreatorName);
            // Check for a Repetition Event and Change the Description Output with and without
            // Repetition Element inside.
            if (repetition.equals("")) {
                qrScanner_result_description.setText(getString(R.string.on) + startDate
                        + getString(R.string.find) + getString(R.string.from) + startTime + getString(R.string.until)
                        + endTime + getString(R.string.clock_at_room) + place + " " + shortTitle
                        + getString(R.string.instead_of) + "\n" + "\n" + getString(R.string.eventDetails)
                        + description);
            } else {
                qrScanner_result_description.setText(getString(R.string.as_of) + startDate
                        + getString(R.string.until) + endDate + getString(R.string.find)
                        + repetition + getString(R.string.at) + startTime + getString(R.string.clock_to)
                        + endTime + getString(R.string.clock_at_room) + place + " " + shortTitle
                        + getString(R.string.instead_of) + "\n" + "\n" + getString(R.string.eventDetails) + description);

            }
            // In the CatchBlock the User see some Error Message and Restart after Clock on Button the TabActivity
        } catch (NullPointerException e) {
            restartApp(whichFragmentTag);
            afterScanningDialogAction.dismiss();
        } catch (ArrayIndexOutOfBoundsException z) {
            com.activeandroid.util.Log.d(TAG, "TabActivity" + z.getMessage());
            qrScanner_reject.setVisibility(View.GONE);
            qrScanner_result_eventSave_without_assign.setVisibility(View.GONE);
            qrScanner_result_eventSave.setVisibility(View.GONE);
            qrScanner_result_abort.setVisibility(View.VISIBLE);
            qrScanner_result_description.setText(getString(R.string.wrongQRCodeResult) + "\n" + "\n"
                    + qrScannContentResult + "\n" + "\n" + getString(R.string.notAsEventSaveable));

            qrScanner_result_abort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartApp(whichFragmentTag);
                    afterScanningDialogAction.dismiss();
                }
            });
        }
    }

    // "Catch" the ScanningResult and throw the Content to the processing Method
    @Override
    public void scanResultData(String whichFragment, String codeContent) {
        openActionDialogAfterScanning(codeContent, whichFragment);
    }

    // Give some error Message if the Code have not Data inside
    @Override
    public void scanResultData(NoScanResultExceptionController noScanData) {
        Toast toast = Toast.makeText(this, noScanData.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    //Method will be called after UI-Elements are created
    public void onStart() {
        super.onStart();
        //Select calendar by default
        Objects.requireNonNull(tabLayout.getTabAt(startTab)).select();
        //Listener that will check when a Tab is selected, unselected and reselected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //Do something if Tab is selected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    CalendarFragment.update(CalendarFragment.selectedMonth);
                    EventOverviewFragment.update();
                }
            }

            //Do something if Tab is unselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//Close the keyboard on a tab change
                //close keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(mSectionsPageAdapter.getItem(tab.getPosition())
                        .getView()).getApplicationWindowToken(), 0);
                //check if settings Tab is unselected
                if (tab.getPosition() == 2) {
                    getSupportFragmentManager().popBackStack();
                } else if (tab.getPosition() == 1) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.calendar_frameLayout, new CalendarFragment());
                    fr.commit();
                } else if (tab.getPosition() == 0) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment());
                    fr.commit();
                }
            }

            //Do something if Tab is reselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //check if settings Tab is unselected
                //close keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(mSectionsPageAdapter.getItem(tab.getPosition())
                        .getView()).getApplicationWindowToken(), 0);
                if (tab.getPosition() == 2) {
                    getSupportFragmentManager().popBackStack();
                } else if (tab.getPosition() == 1) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.calendar_frameLayout, new CalendarFragment());
                    fr.commit();
                } else if (tab.getPosition() == 0) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment());
                    fr.commit();
                }
            }
        });

        //TODO
    }

    // Add the Fragments to the PageViewer
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapterActivity adapter = mSectionsPageAdapter;
        adapter.addFragment(new EventOverviewActivity(), "");
        adapter.addFragment(new CalendarActivity(), "");
        adapter.addFragment(new SettingsActivity(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method calls a Dialog when the User has not added a username
    public void openDialogAskForUsername() {
        final AlertDialog.Builder dialogAskForUsername = new AlertDialog.Builder(this);
        dialogAskForUsername.setView(R.layout.dialog_askforusername);
        dialogAskForUsername.setTitle(R.string.titleDialogUsername);
        dialogAskForUsername.setCancelable(true);

        final AlertDialog alertDialogAskForUsername = dialogAskForUsername.create();
        alertDialogAskForUsername.show();

        final EditText username = alertDialogAskForUsername.findViewById(R.id.dialog_EditText_Username);

        Objects.requireNonNull(username).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String dialog_inputUsername;
                dialog_inputUsername = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("(\\w|\\.)(\\w|\\s|\\.)*");
                Matcher matcher_username = pattern_username.matcher(dialog_inputUsername);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches() && dialog_inputUsername.length()<=50) {
                    personMe.setName(dialog_inputUsername);
                    PersonController.savePerson(personMe);
                    Toast toast = Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    alertDialogAskForUsername.dismiss();
                    return false;
                }else if(dialog_inputUsername.isEmpty()) {
                    Toast.makeText(getContext(), R.string.username_empty, Toast.LENGTH_SHORT).show();
                } else if(dialog_inputUsername.length()>50){
                    Toast.makeText(getContext(), R.string.username_too_long, Toast.LENGTH_SHORT).show();
                }else if(dialog_inputUsername.startsWith(" ")){
                    Toast.makeText(getContext(), R.string.username_spaces, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    //convert startDate-String from QR-Scanner into Calendar format
    private Calendar getStartTimeEvent() {
        //startDate from qr scanner
        String[] startDateStringBufferArray = startDate.split("\\.");
        day = startDateStringBufferArray[0].trim();
        month = startDateStringBufferArray[1].trim();
        year = startDateStringBufferArray[2].trim();

        //startTime from qr scanner
        String[] startTimeStringBufferArray = startTime.split(":");
        hourOfDay = startTimeStringBufferArray[0].trim();
        minutesOfDay = startTimeStringBufferArray[1].trim();

        //set startDate and startTime in one variable
        myStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myStartTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myStartTime.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        return myStartTime;
    }

    //convert endDate-String from QR-Scanner into Calendar format
    private Calendar getEndTimeEvent() {
        String[] startDateStringBufferArray = startDate.split("\\.");
        day = startDateStringBufferArray[0].trim();
        month = startDateStringBufferArray[1].trim();
        year = startDateStringBufferArray[2].trim();

        String[] endTimeStringBufferArray = endTime.split(":");
        hourOfDay = endTimeStringBufferArray[0].trim();
        minutesOfDay = endTimeStringBufferArray[1].trim();

        myEndTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myEndTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myEndTime.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        return myEndTime;
    }

    //if serialevent
    //get end of Repetition Date
    private Calendar getEndDateEvent() {
        String[] endDateStringBufferArray = endDate.split("\\.");
        day = endDateStringBufferArray[0].trim();
        month = endDateStringBufferArray[1].trim();
        year = endDateStringBufferArray[2].trim();

        String[] endTimeStringBufferArray = endTime.split(":");
        hourOfDay = endTimeStringBufferArray[0].trim();
        minutesOfDay = endTimeStringBufferArray[1].trim();

        myEndDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myEndDate.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myEndDate.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        return myEndDate;
    }

    //convert Repetition string into Repetition format
    private Repetition getRepetition() {
        switch (repetition) {
            case "jährlich":
                return Repetition.YEARLY;
            case "monatlich":
                return Repetition.MONTHLY;
            case "wöchentlich":
                return Repetition.WEEKLY;
            case "täglich":
                return Repetition.DAILY;
            default:
                return Repetition.NONE;
        }
    }

    //save Event and Person
    private void dialogListener() {
        final AlertDialog.Builder dialogAskForFinalDecission = new AlertDialog.Builder(this);
        dialogAskForFinalDecission.setView(R.layout.dialog_afterscanningbuttonclick);
        dialogAskForFinalDecission.setTitle(R.string.titleDialogFinalDecission);
        dialogAskForFinalDecission.setCancelable(true);

        final AlertDialog alertDialogAskForFinalDecission = dialogAskForFinalDecission.create();
        //open Dialog with yes or no after button click (accept, save, reject)
        alertDialogAskForFinalDecission.show();
        Objects.requireNonNull(alertDialogAskForFinalDecission.findViewById(R.id.dialog_event_final_decission_accept))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Calendar variables for checking startTime and endTime
                        Calendar checkStartTime = getStartTimeEvent();
                        Calendar checkEndTime = getEndTimeEvent();

                        //check if Event is n Database or not
                        singleEvent = EventController.checkIfEventIsInDatabase(description,
                                shortTitle, place, checkStartTime, checkEndTime);

                        //if event is in database
                        if (singleEvent != null && singleEvent.getAccepted().equals(AcceptedState.WAITING) ||
                                singleEvent != null && singleEvent.getAccepted().equals(AcceptedState.ACCEPTED)) {
                            //finish and restart the activity
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            //write Toast, event is in database
                            Toast toast = Toast.makeText(v.getContext(), R.string.eventIsInDatabase, Toast.LENGTH_LONG);
                            toast.show();

                        } else if (singleEvent != null && singleEvent.getAccepted().equals(AcceptedState.REJECTED)) {
                            //finish and restart the activity
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            //write Toast, event is in database
                            Toast toast = Toast.makeText(v.getContext(), R.string.eventIsInDatabaseRejected, Toast.LENGTH_LONG);
                            toast.show();
                            //if event is not in database
                        } else {
                            savePersonAndEvent();

                        }
                    }
                });
        //if Button "nein": cancel dialog
        Objects.requireNonNull(alertDialogAskForFinalDecission.findViewById(R.id.dialog_event_final_decission_reject))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogAskForFinalDecission.cancel();
                    }
                });
    }

    private void savePersonAndEvent() {
        Person person = new Person();
        Event event = new Event(person);
        //check if user who published the event is in database
        personEventCreator = PersonController.checkforPhoneNumber(creatorPhoneNumber);

        checkIfPersonIsInDatabase(event, person);

        //set all things for event
        event.setCreatorEventId(Long.parseLong(creatorID));
        event.setStartTime(getStartTimeEvent().getTime());
        event.setEndTime(getEndTimeEvent().getTime());
        event.setRepetition(getRepetition());
        event.setShortTitle(shortTitle);
        event.setPlace(place);
        event.setDescription(description);

        //check which button got pressed and set acceptedState
        if (buttonId == 1) {
            event.setAccepted(AcceptedState.ACCEPTED);
            sendSMS(event);
        } else if (buttonId == 2) {
            event.setAccepted(AcceptedState.WAITING);
        }

        //check if event is serialevent
        if (event.getRepetition() != Repetition.NONE) {
            event.setEndDate(getEndDateEvent().getTime());
            //save serialevent
            EventController.saveSerialevent(event);
        } else {
            //save the one event
            EventController.saveEvent(event);
        }

        if (event.getAccepted().equals(AcceptedState.ACCEPTED)) {
            NotificationController.setAlarmForNotification(getApplicationContext(), event);
        }

        //TODO: geht nicht weil die Dialoge noch im Vordergrund sind
        if (event.getAccepted().equals(AcceptedState.REJECTED)) {

        } else {
            Toast.makeText(getContext(), R.string.save_event, Toast.LENGTH_SHORT).show();

            //Restart the TabActivity an Reload all Views
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void saveEventAndPersonForRejection(Dialog afterScanningDialogAction) {
        Person person = new Person();
        Event event = new Event(person);
        //check if user who published the event is in database
        personEventCreator = PersonController.checkforPhoneNumber(creatorPhoneNumber);

        checkIfPersonIsInDatabase(event, person);

        //Calendar variables for checking startTime and endTime
        Calendar checkStartTime = getStartTimeEvent();
        Calendar checkEndTime = getEndTimeEvent();
        //check if Event is n Database or not
        singleEvent = EventController.checkIfEventIsInDatabase(description,
                shortTitle, place, checkStartTime, checkEndTime);

        afterScanningDialogAction.cancel();
        EventRejectEventFragment eventRejectEventFragment = new EventRejectEventFragment();
        Bundle bundleAcceptedEventId = new Bundle();

        //if event is in not database
        if (singleEvent == null) {
            //set all things for event
            event.setCreatorEventId(Long.parseLong(creatorID));
            event.setStartTime(getStartTimeEvent().getTime());
            event.setEndTime(getEndTimeEvent().getTime());
            event.setRepetition(getRepetition());
            event.setShortTitle(shortTitle);
            event.setPlace(place);
            event.setDescription(description);
            event.setAccepted(AcceptedState.REJECTED);

            //check if event is serialevent
            if (event.getRepetition() != Repetition.NONE) {
                event.setEndDate(getEndDateEvent().getTime());
                //save serialevent
                EventController.saveSerialevent(event);
            } else {
                //save the one event
                EventController.saveEvent(event);
            }

            bundleAcceptedEventId.putLong("EventId", event.getId());

        } else {
            //finish and restart the activity
            bundleAcceptedEventId.putLong("EventId", singleEvent.getId());
        }
        bundleAcceptedEventId.putString("fragment", "AcceptedEventDetails");
        eventRejectEventFragment.setArguments(bundleAcceptedEventId);
        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.calendar_frameLayout, eventRejectEventFragment, "RejectEvent");
        fr.addToBackStack("RejectEvent");
        fr.commit();
    }

    private void sendSMS(Event event) {
        //SMS
        String reject_message = "";
        SendSmsController.sendSMS(getApplicationContext(), event.getCreator().getPhoneNumber(), reject_message, true, event.getCreatorEventId(), event.getShortTitle());
    }

    private void checkIfPersonIsInDatabase(Event event, Person person) {
        //if publisher is in database
        if (personEventCreator != null) {
            event.setCreator(personEventCreator);
        } else {
            //if publisher is not in database: save a new person
            person.setName(eventCreatorName);
            person.setPhoneNumber(creatorPhoneNumber);
            person.save();
        }

    }

    private boolean checkIfEventIsInPast() {
        //read the current date and time to compare if the End of the Event is in the past (Date & Time),
        // set seconds and milliseconds to 0 to ensure a ight compare (seonds and milliseconds doesn't matter)
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        boolean test = getStartTimeEvent().before(now);
        Log.i("STARTZEIT", getStartTimeEvent().getTime().toString());
        Log.i("EVENTZEIT", now.getTime().toString());
        if (getRepetition() == Repetition.NONE) {
            if (getStartTimeEvent().getTime().before(now.getTime())) {
                Toast.makeText(this, R.string.startTime_afterScanning_past, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }

        } else {
            if (getEndDateEvent().getTime().before(now.getTime())) {
                Toast.makeText(this, R.string.startTime_afterScanning_past, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
    }

    private void decideWhatToDo(Dialog afterScanningDialogActionn) {
        if (!checkIfEventIsInPast()) {
            Person person = PersonController.getPersonWhoIam();
            if (person == null) {
                openDialogAskForUsername();
            } else if (buttonId == 1 || buttonId == 2) {
                dialogListener();
            } else if (buttonId == 3) {
                saveEventAndPersonForRejection(afterScanningDialogActionn);
            }
        } else {
            //Restart the TabActivity an Reload all Views
            Intent intent = getIntent();
            finish();
            startActivity(intent);
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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPhonePermission() {
        //For Fragment: requestPermissions(permissionsList,REQUEST_CODE);
        //For Activity: ActivityCompat.requestPermissions(this,permissionsList,REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
            // for each permission check if the user granted/denied them you may want to group the
            // rationale in a single dialog,this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {

                if (grantResults.length > 0
                        && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
                    if (!showRationale) {
                        // user also CHECKED "never ask again" you can either enable some fall back,
                        // disable features of your app or open another dialog explaining again the
                        // permission and directing to the app setting

                        new android.support.v7.app.AlertDialog.Builder(this)
                                .setTitle(R.string.accessWith_NeverAskAgain_deny)
                                .setMessage(R.string.sendSMS_accessDenied_withCheckbox)
                                .setPositiveButton(R.string.sendSMS_manual, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        openDialogAskForUsername();
                                    }
                                })
                                .create().show();
                    } else if (counter < 1) {
                        // user did NOT check "never ask again" this is a good place to explain the user
                        // why you need the permission and ask if he wants // to accept it (the rationale)
                        new android.support.v7.app.AlertDialog.Builder(this)
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
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                        openDialogAskForUsername();
                                    }
                                })
                                .create().show();
                    } else if (counter == 1) {
                        new android.support.v7.app.AlertDialog.Builder(this)
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
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                        openDialogAskForUsername();
                                    }
                                })
                                .create().show();
                    } else {
                        openDialogAskForUsername();
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                } else {
                    readPhoneNumber();
                }
            }

        } else if (requestCode == PERMISSION_REQUEST_RECEIVE_SMS) {
            if (counterSMS != 5) {
                // for each permission check if the user granted/denied them you may want to group the
                // rationale in a single dialog,this is just an example
                for (int i = 0; i < 1; i++) {

                    if (grantResults.length > 0
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale;
                        if (counterSMS == 5) {
                            showRationale = true;
                        } else {
                            showRationale = shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS);
                        }
                        if (!showRationale) {
                            // user also CHECKED "never ask again" you can either enable some fall back,
                            // disable features of your app or open another dialog explaining again the
                            // permission and directing to the app setting
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.accessWith_NeverAskAgain_deny)
                                    .setMessage(R.string.requestSMSPermission_accessDenied_withCheckbox)
                                    .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create().show();
                        } else if (counterSMS < 1) {
                            // user did NOT check "never ask again" this is a good place to explain the user
                            // why you need the permission and ask if he wants // to accept it (the rationale)
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                dialog.cancel();
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.requestPermission_firstTryRequest)
                                    .setMessage(R.string.requestPermission_askForSMSPermission)
                                    .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            counterSMS++;
                                            checkSMSPermissions();
                                        }

                                    })
                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkContactsPermission();
                                            counterSMS = 0;
                                        }
                                    })
                                    .create().show();
                        } else if (counterSMS == 1) {
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.requestPermission_lastTryRequest)
                                    .setMessage(R.string.requestPermission_askForSMSPermission)
                                    .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            counterSMS++;
                                            checkSMSPermissions();
                                        }
                                    })
                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkContactsPermission();
                                            counterSMS = 0;
                                        }
                                    })
                                    .create().show();
                        } else {
                        }
                    } else {
                        counterSMS = 5;
                        checkSMSPermissions();
                    }
                }

            }
        } else if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (counterCONTACTS != 5) {
                // for each permission check if the user granted/denied them you may want to group the
                // rationale in a single dialog,this is just an example
                for (int i = 0; i < 1; i++) {

                    if (grantResults.length > 0
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale;
                        if (counterCONTACTS == 5) {
                            showRationale = true;
                        } else {
                            showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                        }
                        if (!showRationale) {
                            // user also CHECKED "never ask again" you can either enable some fall back,
                            // disable features of your app or open another dialog explaining again the
                            // permission and directing to the app setting
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {

                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.accessWith_NeverAskAgain_deny)
                                    .setMessage(R.string.requestContactPermission_accessDenied_withCheckbox)
                                    .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create().show();
                        } else if (counterCONTACTS < 1) {
                            // user did NOT check "never ask again" this is a good place to explain the user
                            // why you need the permission and ask if he wants // to accept it (the rationale)
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {

                                                dialog.cancel();
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.requestPermission_firstTryRequest)
                                    .setMessage(R.string.requestPermission_askForContactsPermission)
                                    .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            counterCONTACTS++;
                                            checkContactsPermission();
                                        }
                                    })
                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            counterCONTACTS = 0;
                                        }
                                    })
                                    .create().show();
                        } else if (counterCONTACTS == 1) {
                            new AlertDialog.Builder(this)
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .setTitle(R.string.requestPermission_lastTryRequest)
                                    .setMessage(R.string.requestPermission_askForContactsPermission)
                                    .setPositiveButton(R.string.requestPermission_againButton, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            counterCONTACTS++;
                                            checkContactsPermission();
                                        }
                                    })
                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            counterCONTACTS = 0;
                                        }
                                    })
                                    .create().show();
                        } else {
                        }
                    } else {
                        counterCONTACTS = 5;
                    }
                }

            }
        }

    }

    // }


    // method to read the phone number of the user
    public void readPhoneNumber() {
        //if permission is granted read the phone number
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String phoneNumber = telephonyManager.getLine1Number();
        //delete spaces and add a plus before the number if it begins without a 0
        if (phoneNumber != null)
            phoneNumber.replaceAll(" ", "");
        if (phoneNumber.matches("[1-9][0-9]+"))
            phoneNumber = "+" + phoneNumber;
        personMe.setPhoneNumber(phoneNumber);
        if (personMe.getPhoneNumber() == null || !personMe.getPhoneNumber().matches("(00|0|\\+)[1-9][0-9]+")) {
            Toast.makeText(this, R.string.telephonenumerNotRead, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.thanksphoneNumber, Toast.LENGTH_SHORT).show();
            if (this.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            }
            if (personMe.getName().isEmpty())
                openDialogAskForUsername();
            else {
                PersonController.savePerson(personMe);
                dialogListener();
            }
        }
    }

}


