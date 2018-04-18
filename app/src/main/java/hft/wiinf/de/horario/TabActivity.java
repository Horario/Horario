package hft.wiinf.de.horario;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;
import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.NewEventActivity;
import hft.wiinf.de.horario.view.SettingsActivity;

public class TabActivity extends AppCompatActivity {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    Person personMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Start DB
        ActiveAndroid.initialize(this);

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
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_settings).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_support).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_feedback).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_copyright).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_main).setVisibility(View.VISIBLE);

                        //Leave edit mode from EditText in Settings (username)
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusableInTouchMode(false);
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusable(false);

                        //Close the keyboard on a tab change
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSectionsPageAdapter.getItem(2).getView().getApplicationWindowToken(), 0);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "TabActivity:" + e.getMessage());
                    }
                }
            }

            //Do something if Tab is reselected. Parameters: selected Tab.--- Info: tab.getPosition() == x for check which Tab
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    //Set Visibility of mainLayout to Visible and the rest to Gone, to see only the overview
                    try {
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_settings).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_support).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_feedback).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_copyright).setVisibility(View.GONE);
                        mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_main).setVisibility(View.VISIBLE);

                        //Leave edit mode from EditText in Settings (username)
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusableInTouchMode(false);
                        mSectionsPageAdapter.getItem(2).getActivity().findViewById(R.id.settings_settings_editText_username).setFocusable(false);

                        //Close the keyboard on a tab change
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSectionsPageAdapter.getItem(2).getView().getApplicationWindowToken(), 0);

                    } catch (NullPointerException e) {
                        Log.d(TAG, "TabActivity:" + e.getMessage());
                    }
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapterActivity adapter = mSectionsPageAdapter;
        adapter.addFragment(new NewEventActivity(), "");
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
                    return false;
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            }
        });

        alertDialogAskForUsername.findViewById(R.id.dialog_username_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personMe != null) {
                    PersonController.addPersonMe(personMe);
                    alertDialogAskForUsername.cancel();
                } else {
                    Toast toast = Toast.makeText(v.getContext(), R.string.UserIsNull, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        alertDialogAskForUsername.findViewById(R.id.dialog_username_button_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAskForUsername.cancel();
            }
        });
    }
}