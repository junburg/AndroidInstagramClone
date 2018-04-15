package moon.the.on.junburg.com.androidinstagramclone.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.time.chrono.MinguoChronology;

import moon.the.on.junburg.com.androidinstagramclone.Login.LoginActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.BottomNavigationViewHelper;
import moon.the.on.junburg.com.androidinstagramclone.Utils.MainfeedListAdapter;
import moon.the.on.junburg.com.androidinstagramclone.Utils.SectionsPagerAdapter;
import moon.the.on.junburg.com.androidinstagramclone.Utils.UniversalImageLoader;
import moon.the.on.junburg.com.androidinstagramclone.Utils.ViewCommentsFragment;
import moon.the.on.junburg.com.androidinstagramclone.models.Photo;
import moon.the.on.junburg.com.androidinstagramclone.models.UserAccountSettings;

public class HomeActivity extends AppCompatActivity implements
                            MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null) {
            fragment.displayMorePhotos();
        }
    }

    private static final int HOME_FRAGMENT = 1;
    private static final String TAG = "HomeActivity";

    // BottomNavigation에 붙는 액티비티 인덱스 0 ~ 4
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = HomeActivity.this;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: statrting.");
        mViewPager = (ViewPager)findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout)findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout)findViewById(R.id.relLayoutParent);

        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity) {
        Log.d(TAG, "onCommentThreadSelected: selected a comment the thread");

        // ViewCommentsFragment 생성
        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        // photo 모델 Bundle에 put
        args.putParcelable(getString(R.string.photo),  photo);
        // HomeActivity에서 호출
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        // 프래그먼트 매니저로 트랙잭션 시작
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // ViewCommentsFragment로 프래그먼트 변경
        transaction.replace(R.id.container, fragment);
        // ViewCommentsFragment 백스택에 추가
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        // transaction 마무리
        transaction.commit();
    }

    /**
     * ViewCommentsFragment VISIBLE
     */
    public void hideLayout() {
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * HomeFragment VISIBLE
     */
    public void showLayout() {
        Log.d(TAG, "showLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    /**
     * 백키를 눌렀을 때 ViewCommentsFragment가 띄어져있으면 showLayout() 실행
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
        if(mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    /**
     * 이미지 로더 초기화, 인스턴스 생성
     */
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     *  TabLayout과 ViewPager를 사용해서 HomeActivity에 사용되는 Fragment구현
     *  CameraFragment, HomeFragment, MessageFragment
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); // 인덱스 0
        adapter.addFragment(new HomeFragment()); // 인덱스 1
        adapter.addFragment(new MessageFragment()); // 인덱스 2
        mViewPager = (ViewPager)findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_instagram);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }

    /**
     *  BottomNavigationView setup
     *  BottomNavigationView 를 커스텀해서 사용할 수 있게 정의한 메서드
     *  BottomNavigationViewHelper 클래스의 static 메서드로 뷰를 커스텀하고 불러와서 사용
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this ,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /*
    ---------------------------------------firebase-------------------------------------------------
     */

    /**
     * 사용자가 로그인 되어있지 않은 상태라면 로그인 액티비티로 넘어감
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if(user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }

    }
    /**
     * 사용자 인증 관련 set
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // 사용자가 로그인되어 있는지 check
                checkCurrentUser(user);

                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    /**
     * 사용자 상태 리스너를 생성
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    /**
     * onStop 생명주기에서 사용자 상태 리스너가 존재하면 해제사용자 상태 리스너 해제
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
