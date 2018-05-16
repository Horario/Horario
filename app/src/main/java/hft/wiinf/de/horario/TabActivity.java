package hft.wiinf.de.horario;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

public class TabActivity extends AppCompatActivity {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    Person personMe;

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

        EditText username = (EditText) alertDialogAskForUsername.findViewById(R.id.dialog_EditText_Username);

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
                    personMe = new Person(true, "007", dialog_inputUsername);
                    PersonController.addPersonMe(personMe);

                    Toast toast = Toast.makeText(v.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();

                    alertDialogAskForUsername.cancel();
                    return false;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            }
        });
    }
}
