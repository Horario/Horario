package hft.wiinf.de.horario;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.activeandroid.ActiveAndroid;

import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.NewEventActivity;
import hft.wiinf.de.horario.view.SettingsActivity;

public class TabActivity extends AppCompatActivity {

    //TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;

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
}
