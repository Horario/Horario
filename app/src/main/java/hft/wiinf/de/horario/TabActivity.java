package hft.wiinf.de.horario;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import hft.wiinf.de.horario.View.CalendarActivity;
import hft.wiinf.de.horario.View.NewEventActivity;
import hft.wiinf.de.horario.View.SettingActivity;

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
        Log.d(TAG, "onCrate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapterActivity(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.container);
        setupViewPager(mViewPager);

       tabLayout = (TabLayout) findViewById(R.id.tabBarLayout);
        tabLayout.setupWithViewPager(mViewPager);

        //TODO Change Picture (DesignTeam)
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_android_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_android_black2_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_android_black3_24dp);
    }

    public void onStart() {
        super.onStart();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 2){
                    mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_settings).setVisibility(View.GONE);
                    mSectionsPageAdapter.getItem(2).getView().findViewById(R.id.settings_relativeLayout_main).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapterActivity adapter = mSectionsPageAdapter;
        adapter.addFragment(new NewEventActivity(),"");
        adapter.addFragment(new CalendarActivity(), "");
        adapter.addFragment(new SettingActivity(), "");
        viewPager.setAdapter(adapter);
    }
}
