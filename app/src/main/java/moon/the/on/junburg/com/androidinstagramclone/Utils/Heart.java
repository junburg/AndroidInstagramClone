package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Junburg on 2018. 4. 5..
 */

public class Heart {

    private static final String TAG = "Heart";

    // 점점 느리게
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    // 점점 빠르게
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    // Widgets
    public ImageView heartWhite, heartRed;

    // Heart 생성자
    public Heart(ImageView heartWhite, ImageView hearRed) {
        this.heartWhite = heartWhite;
        this.heartRed = hearRed;
    }

    //
    public void toggleLike() {
        Log.d(TAG, "toggleLike: toggling heart.");

        // ObjectAnimator를 사용하기 위한 AnimatorSet 인스턴스 생성
        AnimatorSet animatorSet = new AnimatorSet();

        // 좋아요를 누른 상태라면 (이미지 뷰에 빨간하트 VISIBLE)
        if(heartRed.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "toggleLike: toggling red heart off.");

            // 빨간 하트의 크기를 1/10로 줄인다
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            // ofFloat(대상이 되는 뷰, 적용할 애니메이션, 시작 크기, 종료 크기)
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f, 0f);
            // 0.3 초간 동작
            scaleDownY.setDuration(4000);
            // 점점 빠르게 동작
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            // ofFloat(대상이 되는 뷰, 적용할 애니메이션, 시작 크기, 종료 크기)
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f, 0f );
            // 0.3 초간 동작
            scaleDownX.setDuration(4000);
            // 점점 빠르게 동작
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);


            // 빨간 하트는 사라지고
            heartRed.setVisibility(View.GONE);
            // 빈 하트가 VISIBLE
            heartWhite.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownY, scaleDownX);



        }
        // 좋아요를 누르지 않은 상태라면 (이미지 뷰에 빈 하트 VISIBLE)
        else if(heartRed.getVisibility() == View.GONE) {
            Log.d(TAG, "toggleLike: toggling red heart on.");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);

            // 빨간 하트는 VISIBLE
            heartRed.setVisibility(View.VISIBLE);
            // 빈 하트는 GONE
            heartWhite.setVisibility(View.GONE);

            animatorSet.playTogether(scaleDownY, scaleDownX);

        }

        animatorSet.start();
    }
}
