package hft.wiinf.de.horario;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.controller.EventController;
import hft.wiinf.de.horario.controller.NoScanResultExceptionController;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.controller.ScanResultReceiverController;
import hft.wiinf.de.horario.model.AcceptedState;
import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.model.Repetition;
import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.CalendarFragment;
import hft.wiinf.de.horario.view.EventOverviewActivity;
import hft.wiinf.de.horario.view.EventOverviewFragment;
import hft.wiinf.de.horario.view.SettingsActivity;

public class TabActivity extends AppCompatActivity implements ScanResultReceiverController {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    Person personMe;

    Person person;
    Person personEventCreator;
    //Index: 0 = CreatorID; 1 = StartDate; 2 = EndDate; 3 = StartTime; 4 = EndTime;
    //       5 = Repetition; 6 = ShortTitle; 7 = Place; 8 = Descriptoin;  9 = EventCreatorName
    private String creatorID, startDate, endDate, startTime, endTime, repetition, shortTitle, place, description, eventCreatorName, creatorPhoneNumber;
    private String hourOfDay, minutesOfDay, year, month, day;

    Calendar myStartTime = Calendar.getInstance();
    Calendar myEndTime = Calendar.getInstance();
    Calendar myEndDate = Calendar.getInstance();

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

        if (PersonController.getPersonWhoIam() == null) {
            openDialogAskForUsername();
        } else if (PersonController.getPersonWhoIam().getName().isEmpty()) {
            openDialogAskForUsername();
        }

        myStartTime.set(Calendar.SECOND, 0);
        myStartTime.set(Calendar.MILLISECOND, 0);
        myEndTime.set(Calendar.SECOND, 0);
        myEndTime.set(Calendar.MILLISECOND, 0);
        myEndDate.set(Calendar.SECOND, 0);
        myEndDate.set(Calendar.MILLISECOND, 0);
    }

    //After Scanning it was opened a Dialog where the user can choose what to do next
    @SuppressLint("ResourceType")
    private void openActionDialogAfterScanning(final String qrScannContentResult) {

        //Create the Dialog with the GUI Elements initial
        final Dialog afterScanningDialogDecission = new Dialog(this);
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


        final AlertDialog.Builder dialogAskForFinalDecission = new AlertDialog.Builder(this);
        dialogAskForFinalDecission.setView(R.layout.dialog_afterscanningbuttonclick);
        dialogAskForFinalDecission.setTitle(R.string.titleDialogFinalDecission);
        dialogAskForFinalDecission.setCancelable(true);

        final AlertDialog alertDialogAskForFinalDecission = dialogAskForFinalDecission.create();

        TextView qrScanner_result_final_question = alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_textView_question);
        Button qrScanner_final_question_save = alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_button_accept);
        Button qrScanner_final_question_reject = alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_button_eventSave);

        //Set the Cancel and BackToCalenderButtons to Invisible
        qrScanner_result_abort.setVisibility(View.GONE);
        qrScanner_result_toCalender.setVisibility(View.GONE);


        try {
            // Button to Save the Event and send for assent the Event a SMS  to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSave)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogAskForFinalDecission.show();
                            //ToDo Dennis hier kommt dein Code rein.
                            alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_button_accept)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            person = PersonController.getPersonWhoIam();

                                            Person person = new Person();
                                            personEventCreator = PersonController.checkforPhoneNumber(creatorPhoneNumber);
                                            Event event = new Event(person);
                                            if (personEventCreator != null) {
                                                event.setCreator(personEventCreator);
                                            } else {
                                                person.setName(eventCreatorName);
                                                person.setPhoneNumber(creatorPhoneNumber);
                                                person.save();
                                            }
                                            event.setAccepted(AcceptedState.ACCEPTED);
                                            event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                                            event.setStartTime(getStartTime().getTime());
                                            event.setEndTime(getEndTime().getTime());
                                            event.setRepetition(getRepetition());
                                            event.setShortTitle(shortTitle);
                                            event.setPlace(place);
                                            event.setDescription(description);

                                            if (event.getRepetition() != Repetition.NONE) {
                                                event.setEndDate(getEndDate().getTime());
                                                EventController.saveSerialevent(event);
                                            } else {
                                                EventController.saveEvent(event);
                                            }

                                            //Restart the TabActivity an Reload all Views
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                        }
                    });

            //Button to Save the Event but don't send for assent the Event a SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventSaveOnly)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogAskForFinalDecission.show();
                            alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_button_accept)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            person = PersonController.getPersonWhoIam();

                                            Person person = new Person();
                                            personEventCreator = PersonController.checkforPhoneNumber(creatorPhoneNumber);
                                            Event event = new Event(person);
                                            if (personEventCreator != null) {
                                                event.setCreator(personEventCreator);
                                            } else {
                                                person.setName(eventCreatorName);
                                                person.setPhoneNumber(creatorPhoneNumber);
                                                person.save();
                                            }
                                            event.setAccepted(AcceptedState.WAITING);
                                            event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                                            event.setStartTime(getStartTime().getTime());
                                            event.setEndTime(getEndTime().getTime());
                                            event.setRepetition(getRepetition());
                                            event.setShortTitle(shortTitle);
                                            event.setPlace(place);
                                            event.setDescription(description);

                                            if (event.getRepetition() != Repetition.NONE) {
                                                event.setEndDate(getEndDate().getTime());
                                                EventController.saveSerialevent(event);
                                            } else {
                                                EventController.saveEvent(event);
                                            }

                                            //Restart the TabActivity an Reload all Views
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                        }
                    });

            //Button to Reject the Event und send a Reject SMS to the EventCreator
            afterScanningDialogAction.findViewById(R.id.dialog_qrScanner_button_eventRecject)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogAskForFinalDecission.show();
                            alertDialogAskForFinalDecission.findViewById(R.id.dialog_qrScanner_button_accept)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            person = PersonController.getPersonWhoIam();

                                            Person person = new Person();
                                            personEventCreator = PersonController.checkforPhoneNumber(creatorPhoneNumber);
                                            Event event = new Event(person);
                                            if (personEventCreator != null) {
                                                event.setCreator(personEventCreator);
                                            } else {
                                                person.setName(eventCreatorName);
                                                person.setPhoneNumber(creatorPhoneNumber);
                                                person.save();
                                            }
                                            event.setAccepted(AcceptedState.REJECTED);
                                            event.setCreatorEventId(Long.parseLong(creatorID.toString()));
                                            event.setStartTime(getStartTime().getTime());
                                            event.setEndTime(getEndTime().getTime());
                                            event.setRepetition(getRepetition());
                                            event.setShortTitle(shortTitle);
                                            event.setPlace(place);
                                            event.setDescription(description);

                                            if (event.getRepetition() != Repetition.NONE) {
                                                event.setEndDate(getEndDate().getTime());
                                                EventController.saveSerialevent(event);
                                            } else {
                                                EventController.saveEvent(event);
                                            }

                                            //Restart the TabActivity an Reload all Views
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
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

        EditText username = alertDialogAskForUsername.findViewById(R.id.dialog_EditText_Username);

        Objects.requireNonNull(username).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String dialog_inputUsername;
                dialog_inputUsername = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(dialog_inputUsername);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    if (PersonController.getPersonWhoIam() == null) {
                        //ToDo: Flo - PhoneNumber
                        personMe = new Person(true, "007", dialog_inputUsername);
                        PersonController.addPersonMe(personMe);

                        Toast toast = Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                        toast.show();

                        alertDialogAskForUsername.cancel();
                    } else {
                        personMe = PersonController.getPersonWhoIam();
                        personMe.setName(dialog_inputUsername);
                        PersonController.savePerson(personMe);

                        Toast toast = Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                        toast.show();

                        alertDialogAskForUsername.cancel();
                    }
                    return false;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
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

    private Calendar getStartTime() {
        String[] startDateStringBufferArray = startDate.split("\\.");
        day = startDateStringBufferArray[0].trim();
        month = startDateStringBufferArray[1].trim();
        year = startDateStringBufferArray[2].trim();

        String[] startTimeStringBufferArray = startTime.split(":");
        hourOfDay = startTimeStringBufferArray[0].trim();
        minutesOfDay = startTimeStringBufferArray[1].trim();

        myStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myStartTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myStartTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        return myStartTime;
    }

    private Calendar getEndTime() {
        String[] startDateStringBufferArray = startDate.split("\\.");
        day = startDateStringBufferArray[0].trim();
        month = startDateStringBufferArray[1].trim();
        year = startDateStringBufferArray[2].trim();

        String[] endTimeStringBufferArray = endTime.split(":");
        hourOfDay = endTimeStringBufferArray[0].trim();
        minutesOfDay = endTimeStringBufferArray[1].trim();

        myEndTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myEndTime.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myEndTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        return myEndTime;
    }

    private Calendar getEndDate() {
        String[] endDateStringBufferArray = endDate.split("\\.");
        day = endDateStringBufferArray[0].trim();
        month = endDateStringBufferArray[1].trim();
        year = endDateStringBufferArray[2].trim();

        String[] endTimeStringBufferArray = endTime.split(":");
        hourOfDay = endTimeStringBufferArray[0].trim();
        minutesOfDay = endTimeStringBufferArray[1].trim();

        myEndDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOfDay));
        myEndDate.set(Calendar.MINUTE, Integer.parseInt(minutesOfDay));
        myEndDate.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return myEndDate;
    }

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
}