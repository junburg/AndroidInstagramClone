package moon.the.on.junburg.com.androidinstagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.BottomNavigationViewHelper;
import moon.the.on.junburg.com.androidinstagramclone.Utils.Permissions;
import moon.the.on.junburg.com.androidinstagramclone.Utils.SectionsPagerAdapter;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";

    // BottomNavigation에 붙는 액티비티 인덱스 0 ~ 4
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    // Widgets
    private ViewPager mViewPager;

    // Variables
    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // 카메라, 메모리 사용 권한 체크
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
                // 권한 확인 여부를 확인하고
            setupViewPager();
        } else {
                // 실제적으로 권한 부여를 함
            verifyPermissions(Permissions.PERMISSIONS);
        }

        // setupBottomNavigationView();
    }

    // 넘어온 인텐트의 플래그를 반환
    public int getTask() {
        return getIntent().getFlags();
    }

    /**
     * 현재 탭의 숫자를 반환
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     */
    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }


    /**
     *  GalleryFragment, PhotoFragment 뷰 페이저 set
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }

    /**
     * 매개변수 배열로 넘겨받은 퍼미션들을 검증
     *
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * 권한이 담긴 배열 체크, 모든 권한 체크
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;

    }

    /**
     * 개별 권한을 체크
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    /**
     * BottomNavigationView setup
     * BottomNavigationView 를 커스텀해서 사용할 수 있게 정의한 메서드
     * BottomNavigationViewHelper 클래스의 static 메서드로 뷰를 커스텀하고 불러와서 사용
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this ,bottomNavigationViewEx);        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
