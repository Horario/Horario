package hft.wiinf.de.horario;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import hft.wiinf.de.horario.view.CalendarActivity;
import hft.wiinf.de.horario.view.NewEventActivity;
import hft.wiinf.de.horario.view.SettingActivity;

public class TabActivity extends AppCompatActivity {
//TODO Kommentieren und Java Doc Info Schreiben
    private static final String TAG = "TabActivity";
    private SectionsPageAdapterActivity mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Log.d(TAG, "onCrate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapterActivity(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabBarLayout);
        tabLayout.setupWithViewPager(mViewPager);

        //TODO Change Picture (DesignTeam)
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_android_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_android_black2_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_android_black3_24dp);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapterActivity adapter = new SectionsPageAdapterActivity((getSupportFragmentManager()));
        adapter.addFragment(new NewEventActivity(),"");
        adapter.addFragment(new CalendarActivity(), "");
        adapter.addFragment(new SettingActivity(), "");
        viewPager.setAdapter(adapter);
    }
}
