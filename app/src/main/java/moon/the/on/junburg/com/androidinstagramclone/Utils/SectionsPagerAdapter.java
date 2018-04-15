package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junburg on 2018. 3. 25..
 */

/**
 * Class that stores fragments for tabs
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "SectionsPagerAdapter";

    // FragmentPagerAdapter로 관리할 Fragment들이 들어갈 리스트
    private final List<Fragment> mFragmnetList = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmnetList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmnetList.size();
    }

    // 리스트에 관리할 Fragment 추가 메서드
    public void addFragment(Fragment fragment) {
        mFragmnetList.add(fragment);
    }
}
