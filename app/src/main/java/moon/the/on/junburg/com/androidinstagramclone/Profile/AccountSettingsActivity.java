package moon.the.on.junburg.com.androidinstagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.BottomNavigationViewHelper;
import moon.the.on.junburg.com.androidinstagramclone.Utils.FirebaseMethods;
import moon.the.on.junburg.com.androidinstagramclone.Utils.SectionStatePagerAdapter;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;

    // Variables
    private Context mContext;
    public SectionStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        setupSettingList();
        setupBottomNavigationView();
        setupFragments();
        getIncomingIntent();

        //  뒤로가기 버튼 설정 to ProfileActivity
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigation back to 'ProfileActivity");
                finish();
            }
        });
    }

    /**
     * AccountSettingActivity로 전달된 Intent를 이용해서 현재 나타낼 프래그먼트를 View Pager에 set
     */
    private void getIncomingIntent() {
        Intent intent = getIntent();

        // 인텐트가 selected_image나 selected_image Extra를 가지고 있으면
        if (intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {
            // Extra가 selected_image일 경우 (갤러리를 사용해서 프로필 사진을 변경할 경우)
            if (intent.hasExtra(getString(R.string.selected_image))) {
                // 프로필 사진 변경
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                        intent.getStringExtra(getString(R.string.selected_image)), null);
            }
            // Extra가 selected_bitmap일 경우 (카메라를 사용해서 프로필 사진을 변경할 경우)
            else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                // 프로필 사진 변경
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                        null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
            }
        }

        // AccountSettingsActivity가 ProfileActivity로 부터 호출되었으면 EditProfileFragment를 ViewPager에 set
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    /**
     * FragmentStatePagerAdapter를 사용해 Fragment 추가
     */
    private void setupFragments() {
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); // fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment)); // fragment 1
    }

    /**
     * ViewPager에 Adapter set
     */
    public void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigation to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    /**
     * 프로파일 수정과 탈퇴를 할 수 있는 리스트 뷰 set
     */
    private void setupSettingList() {
        Log.d(TAG, "setupSettingList: initializing 'Account Settings' list.");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));
        options.add(getString(R.string.sign_out_fragment));

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: navigation to fragment#: " + position);
                setViewPager(position);

            }
        });
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
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}
