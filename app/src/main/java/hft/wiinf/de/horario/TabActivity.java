package hft.wiinf.de.horario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.controller.NoScanResultExceptionController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.controller.ScanResultReceiverController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.CalendarFragment;
import hft.wiinf.de.horario.view.EventOverviewActivity;
import hft.wiinf.de.horario.view.EventOverviewFragment;
import hft.wiinf.de.horario.view.SettingsActivity;

import static android.Manifest.permission.READ_SMS;
import static com.activeandroid.Cache.getContext;

public class TabActivity extends AppCompatActivity implements ScanResultReceiverController {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    TabLayout tabLayout;
    Person person;
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    private int counter;

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Start DB
        ActiveAndroid.initialize(this);
        Stetho.initializeWithDefaults(this);

        mSectionsPageAdapter = new SectionsPageAdapterActivity(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabBarLayout);
        tabLayout.setupWithViewPager(mViewPager);

        //TODO Change Picture (DesignTeam)
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_dateview);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_calendarview);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings);
        person = PersonController.getPersonWhoIam();
        //create a new person if it is null
        if (person == null)
            person = new Person(true, "", "");
        if (person.getName().isEmpty()) {
            openDialogAskForUsername();
        } else if (person.getPhoneNumber() == null || person.getPhoneNumber().isEmpty()) {
            readOwnPhoneNumber();
        }
    }

    //After Scanning it was opened a Dialog where the user can choose what to do next
    @SuppressLint("ResourceType")
    private void openActionDialogAfterScanning(final String qrScannContentResult) {

        //Create the Dialog with the GUI Elements initial
        final Dialog afterScanningDialogAction = new Dialog(this);
        afterScanningDialogAction.setContentView(R.layout.dialog_afterscanning);
        afterScanningDialogAction.setCancelable(true);
        afterScanningDialogAction.show();

        TextView qrScanner_result_description = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_textView_description);
        TextView qrScanner_result_headline = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_textView_headline);
        Button qrScanner_reject = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventRecject);
        Button qrScanner_result_eventSave = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSave);
        Button qrScanner_result_abort = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_about);
        Button qrScanner_result_toCalender = afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_toCalender);
        Button qrScanner_result_eventSave_without_assign = afterScanningDialogAction.findViewById((R.id.dialog_qrScanner_button_eventSaveOnly));


        //Set the Cancel and BackToCalenderButtons to Invisible
        qrScanner_result_abort.setVisibility(View.GONE);
        qrScanner_result_toCalender.setVisibility(View.GONE);


        try {
            // Button to Save the Event and send for assent the Event a SMS  to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSave)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //ToDo Dennis hier kommt dein Code rein.


                            //Restart the TabActivity an Reload all Views
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

            //Button to Save the Event but don't send for assent the Event a SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSaveOnly).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToDo Dennis hier kommt dein Code rein.


                    //Restart the TabActivity an Reload all Views
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            //Button to Reject the Event und send a Reject SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventRecject).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToDo Dennis hier kommt dein Code rein.

                    //Restart the TabActivity an Reload all Views
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            //Put StringBuffer in an Array and split the Values to new String Variables
            //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
            //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Description;  9 = EventCreatorName; 10 = phoneNumber;
            String[] eventStringBufferArray = qrScannContentResult.split("\\|");
            String startDate = eventStringBufferArray[1].trim();
            String endDate = eventStringBufferArray[2].trim();
            String startTime = eventStringBufferArray[3].trim();
            String endTime = eventStringBufferArray[4].trim();
            String repetition = eventStringBufferArray[5].toUpperCase().trim();
            String shortTitle = eventStringBufferArray[6].trim();
            String place = eventStringBufferArray[7].trim();
            String description = eventStringBufferArray[8].trim();
            String eventCreatorName = eventStringBufferArray[9].trim();

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
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
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
                        + getString(R.string.instead_of) + "\n" + getString(R.string.eventDetails)
                        + description + "\n" + "\n" + getString(R.string.organizer) + eventCreatorName);
            } else {
                qrScanner_result_description.setText(getString(R.string.as_of) + startDate
                        + getString(R.string.until) + endDate + getString(R.string.find)
                        + repetition + getString(R.string.at) + startTime + getString(R.string.clock_to)
                        + endTime + getString(R.string.clock_at_room) + place + " " + shortTitle
                        + getString(R.string.instead_of) + "\n" + getString(R.string.eventDetails) + description +
                        "\n" + "\n" + getString(R.string.organizer) + eventCreatorName);

            }
            // In the CatchBlock the User see some Error Message and Restart after Clock on Button the TabActivity
        } catch (NullPointerException e) {
            com.activeandroid.util.Log.d(TAG, "TabActivity" + e.getMessage());

            //Hide the Buttons that's not possible to Save or Reject the Event.
            qrScanner_reject.setVisibility(View.GONE);
            qrScanner_result_eventSave_without_assign.setVisibility(View.GONE);
            qrScanner_result_eventSave.setVisibility(View.GONE);

            // It's show the Cancel Button to Restart the TabActivity
            qrScanner_result_toCalender.setVisibility(View.VISIBLE);
            qrScanner_result_description.setText(getString(R.string.ups_an_error));
            qrScanner_result_toCalender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            // Same like the NullPointerException
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
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }
    }


    // "Catch" the ScanningResult and throw the Content to the processing Method
    @Override
    public void scanResultData(String codeFormat, String codeContent) {
        openActionDialogAfterScanning(codeContent);
    }

    // Give some Errormessage if the Code have not Data inside
    @Override
    public void scanResultData(NoScanResultExceptionController noScanData) {
        Toast toast = Toast.makeText(this, noScanData.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    //Method will be called after UI-Elements are created
    public void onStart() {
        super.onStart();
        //Select calendar by default
        Objects.requireNonNull(tabLayout.getTabAt(1)).select();
        //Listener that will check when a Tab is selected, unselected and reselected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //Do something if Tab is selected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            //Do something if Tab is unselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //check if settings Tab is unselected
                if (tab.getPosition() == 2) {
                    getSupportFragmentManager().popBackStack();
                } else if (tab.getPosition() == 1) {
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.calendar_frameLayout, new CalendarFragment());
                    fr.commit();
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else if (tab.getPosition() == 0) {
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment());
                    fr.commit();
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }

            //Do something if Tab is reselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //check if settings Tab is unselected
                if (tab.getPosition() == 2) {
                    getSupportFragmentManager().popBackStack();
                } else if (tab.getPosition() == 1) {
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.calendar_frameLayout, new CalendarFragment());
                    fr.commit();
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else if (tab.getPosition() == 0) {
                    FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.eventOverview_frameLayout, new EventOverviewFragment());
                    fr.commit();
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });
    }

    // Add the Fragments to the VageViewer
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
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(dialog_inputUsername);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    person.setName(dialog_inputUsername);
                    if (person.getPhoneNumber() == null || person.getPhoneNumber().isEmpty())
                        readOwnPhoneNumber();
                    alertDialogAskForUsername.dismiss();
                    PersonController.savePerson(person);
                    Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    return false;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            }

        });
    }

    // method to read the phone number of the user
    public void readOwnPhoneNumber() {
        if (checkSelfPermission(READ_SMS) != PackageManager.PERMISSION_GRANTED)
            requestPermission();
        else {
            //if permission is granted read the phone number
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = (telephonyManager.getLine1Number());
            if (phoneNumber != null)
                phoneNumber.replaceAll(" ", "");
            person.setPhoneNumber(phoneNumber);
            //if the number could not been read, open a dialog
            if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("\\+?[0-9]+"))
                openDialogAskForPhoneNumber();
            else {
                PersonController.addPersonMe(person);
                Toast.makeText(getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_SEND_SMS: {
                // If Permission ist Granted User get a SnackbarMessage and the phone number is read
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(R.id.tabBarLayout),
                            R.string.thanksphoneNumber,
                            Snackbar.LENGTH_SHORT).show();
                    readOwnPhoneNumber();
                } else {
                    //If the User denies the access to the phone number he gets two Chance to accept the Request
                    //The Counter counts from 0 to 2. If the Counter is 2 user a dialog is shown where the user can input the phone number
                    switch (counter) {
                        case 0:
                            Snackbar.make(this.findViewById(R.id.tabBarLayout),
                                    R.string.phoneNumber_explanation,
                                    Snackbar.LENGTH_LONG).show();
                            counter++;
                            readOwnPhoneNumber();
                            break;

                        case 1:
                            Snackbar.make(this.findViewById(R.id.tabBarLayout),
                                    R.string.lastTry_phoneNumber,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            counter++;
                            readOwnPhoneNumber();
                            break;
                        default:
                            openDialogAskForPhoneNumber();
                    }
                }
            }
        }
    }

    public void openDialogAskForPhoneNumber() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.dialog_askingfortelephonenumber);
        dialogBuilder.setCancelable(true);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        EditText phoneNumber = alertDialog.findViewById(R.id.dialog_EditText_telephonNumber);
        if (person.getPhoneNumber() != null)
            phoneNumber.setText(person.getPhoneNumber());
        phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = v.getText().toString().replaceAll(" ", "");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //regex: perhaps + at beginning, then numbers
                    if (input.matches("(0|\\+|00)[1-9][0-9]*")) {
                        alertDialog.dismiss();
                        person.setPhoneNumber(input);
                        PersonController.addPersonMe(person);
                        Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
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
        //if the dialog is canceled save only the person (user name)
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast toast = Toast.makeText(getContext(), R.string.PhoneNumberNotSaved, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}


