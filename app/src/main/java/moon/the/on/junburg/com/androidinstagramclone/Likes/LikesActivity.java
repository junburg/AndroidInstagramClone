package moon.the.on.junburg.com.androidinstagramclone.Likes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.BottomNavigationViewHelper;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class LikesActivity extends AppCompatActivity {
    private static final String TAG = "LikesActivity";
    // BottomNavigation에 붙는 액티비티 인덱스 0 ~ 4
    private static final int ACTIVITY_NUM = 3;

    // Variables
    private Context mContext = LikesActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigationView();
    }

    /**
     * BottomNavigationView setup
     * BottomNavigationView 를 커스텀해서 사용할 수 있게 정의한 메서드
     * BottomNavigationViewHelper 클래스의 static 메서드로 뷰를 커스텀하고 불러와서 사용
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this ,bottomNavigationViewEx);        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
