package hft.wiinf.de.horario;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.EventOverviewActivity;
import hft.wiinf.de.horario.view.SettingsActivity;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.activeandroid.Cache.getContext;

public class TabActivity extends AppCompatActivity {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private static final int PERMISSION_REQUEST_TELEPHONE_STATE = 0;
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    Person person;
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
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_android_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_android_black2_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_android_black3_24dp);

        if (PersonController.getPersonWhoIam() == null) {
            openDialogAskForUsername();
        } else if (PersonController.getPersonWhoIam().getName().isEmpty()) {
            openDialogAskForUsername();
        }
    }

    //Method will be called after UI-Elements are created
    public void onStart() {
        super.onStart();
        //Select calendar by default
        tabLayout.getTabAt(1).select();
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
                    //Set Visibility of mainLayout to Visible and the rest to Gone, to see only the overview
                    try {
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_helper).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_button).setVisibility(View.VISIBLE);

                        //Leave edit mode from EditText in Settings (username)
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusableInTouchMode(false);
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusable(false);

                        //Close the keyboard on a tab change
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSectionsPageAdapter.getItem(2).getView().getApplicationWindowToken(), 0);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "TabActivity:" + e.getMessage());
                    }
                } else if (tab.getPosition() == 1) {
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_relativeLayout_helper).setVisibility(View.GONE);
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_constrainLayout_main).setVisibility(View.VISIBLE);
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonMenu).setVisibility(View.VISIBLE);

                    FloatingActionButton floatNewEvent = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonNewEvent);
                    FloatingActionButton floatQRScan = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonScan);
                    FloatingActionButton floatMenu = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonMenu);
                    TextView isFloatingMenuOpen = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_hiddenField);

                    floatNewEvent.hide();
                    floatQRScan.hide();
                    floatMenu.setImageResource(R.drawable.ic_android_black2_24dp);
                    isFloatingMenuOpen.setText("false");

                } else if (tab.getPosition() == 0) {
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_relativeLayout_helper).setVisibility(View.GONE);
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_Layout_main).setVisibility(View.VISIBLE);
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonMenu).setVisibility(View.VISIBLE);

                    FloatingActionButton floatNewEvent = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
                    FloatingActionButton floatQRScan = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonScan);
                    FloatingActionButton floatMenu = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonMenu);
                    TextView isFloatingMenuOpen = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverviewFabClosed);

                    floatNewEvent.hide();
                    floatQRScan.hide();
                    floatMenu.setImageResource(R.drawable.ic_android_black2_24dp);
                    isFloatingMenuOpen.setText("false");
                }
            }

            //Do something if Tab is reselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    //Set Visibility of mainLayout to Visible and the rest to Gone, to see only the overview
                    try {
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_helper).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_button).setVisibility(View.VISIBLE);

                        //Leave edit mode from EditText in Settings (username)
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusableInTouchMode(false);
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusable(false);

                        //Close the keyboard on a tab change
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSectionsPageAdapter.getItem(2).getView().getApplicationWindowToken(), 0);

                    } catch (NullPointerException e) {
                        Log.d(TAG, "TabActivity:" + e.getMessage());
                    }
                } else if (tab.getPosition() == 1) {
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_relativeLayout_helper).setVisibility(View.GONE);
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_constrainLayout_main).setVisibility(View.VISIBLE);
                    mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonMenu).setVisibility(View.VISIBLE);

                    FloatingActionButton floatNewEvent = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonNewEvent);
                    FloatingActionButton floatQRScan = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonScan);
                    FloatingActionButton floatMenu = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_floatingActionButtonMenu);
                    TextView isFloatingMenuOpen = mSectionsPageAdapter.getItem(1).getView().findViewById(R.id.calendar_hiddenField);


                    floatNewEvent.hide();
                    floatQRScan.hide();
                    floatMenu.setImageResource(R.drawable.ic_android_black2_24dp);
                    isFloatingMenuOpen.setText("false");

                } else if (tab.getPosition() == 0) {
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_relativeLayout_helper).setVisibility(View.GONE);
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_Layout_main).setVisibility(View.VISIBLE);
                    mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonMenu).setVisibility(View.VISIBLE);

                    FloatingActionButton floatNewEvent = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonNewEvent);
                    FloatingActionButton floatQRScan = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonScan);
                    FloatingActionButton floatMenu = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverview_floatingActionButtonMenu);
                    TextView isFloatingMenuOpen = mSectionsPageAdapter.getItem(0).getView().findViewById(R.id.eventOverviewFabClosed);

                    floatNewEvent.hide();
                    floatQRScan.hide();
                    floatMenu.setImageResource(R.drawable.ic_android_black2_24dp);
                    isFloatingMenuOpen.setText("false");
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapterActivity adapter = mSectionsPageAdapter;
        adapter.addFragment(new EventOverviewActivity(), "");
        adapter.addFragment(new CalendarActivity(), "");
        adapter.addFragment(new SettingsActivity(), "");
        viewPager.setAdapter(adapter);
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

        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String dialog_inputUsername;
                dialog_inputUsername = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(dialog_inputUsername);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    person = new Person(true, "", dialog_inputUsername);
                    if (person.getPhoneNumber() == null || person.getPhoneNumber().equalsIgnoreCase(""))
                        readOwnPhoneNumber();
                    alertDialogAskForUsername.dismiss();
                    return true;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
            }

        });
    }

    // method to read the phone number of the user
    public void readOwnPhoneNumber() {
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_TELEPHONE_STATE: {
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
                                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.oneMoreTime, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    counter++;
                                    readOwnPhoneNumber();
                                }
                            }).show();
                            break;

                        case 1:
                            Snackbar.make(this.findViewById(R.id.tabBarLayout),
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
                    if (input.matches("[+0].+")) {
                        alertDialog.dismiss();
                        person.setPhoneNumber(input);
                        PersonController.addPersonMe(person);
                        Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT).show();
                        // ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        return false;
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

