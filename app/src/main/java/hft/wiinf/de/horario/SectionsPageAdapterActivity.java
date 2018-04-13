package hft.wiinf.de.horario;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;
//TODO Kommentieren und Java Doc Info Schreiben //wird gemacht
class SectionsPageAdapterActivity extends FragmentPagerAdapter{

    private final List<Fragment>mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public SectionsPageAdapterActivity(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public CharSequence getPageTitle(int position){
        return mFragmentTitleList.get(position);
    }



    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
