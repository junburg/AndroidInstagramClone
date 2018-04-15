package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import moon.the.on.junburg.com.androidinstagramclone.Home.HomeActivity;
import moon.the.on.junburg.com.androidinstagramclone.Likes.LikesActivity;
import moon.the.on.junburg.com.androidinstagramclone.Profile.ProfileActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Search.SearchActivity;
import moon.the.on.junburg.com.androidinstagramclone.Share.ShareActivity;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    /**
     * BottomNavigationViewEx의 동작에 관해 정의
     */
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        // 애니메이션 사용 X
        bottomNavigationViewEx.enableAnimation(false);
        // 아이콘 밀기 X
        bottomNavigationViewEx.enableItemShiftingMode(false);
        // 애니메이션 밀기 X
        bottomNavigationViewEx.enableShiftingMode(false);
        // 텍스트 숨김
        bottomNavigationViewEx.setTextVisibility(false);
    }

    /**
     * BottomNavigationEx에 정의된 menu를 선택할 때마다 어떤 액티비티를 띄울 것인지 정의
     */
    public static void enableNavigation(final Context context, final Activity callingActivity , BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    // HomeActivity 이동
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, HomeActivity.class); // ACTIVITIY_NUM = 0
                        context.startActivity(intent1);
                        // Activity 애니메이션 정의
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    // SearchActivity 이동
                    case R.id.ic_search:
                        Intent intent2 = new Intent(context, SearchActivity.class); // ACTIVITIY_NUM = 1
                        context.startActivity(intent2);
                        // Activity 애니메이션 정의
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    // ShareActivity 이동
                    case R.id.ic_circle:
                        Intent intent3 = new Intent(context, ShareActivity.class); // ACTIVITIY_NUM = 2
                        context.startActivity(intent3);
                        // Activity 애니메이션 정의
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    // LikesActivity 이동
                    case R.id.ic_alert:
                        Intent intent4 = new Intent(context, LikesActivity.class); // ACTIVITIY_NUM = 3
                        context.startActivity(intent4);
                        // Activity 애니메이션 정의
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    // ProfileActivity 이동
                    case R.id.ic_android:
                        Intent intent5 = new Intent(context, ProfileActivity.class); // ACTIVITIY_NUM = 4
                        context.startActivity(intent5);
                        // Activity 애니메이션 정의
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }
                return false;
            }
        });
    }
}
