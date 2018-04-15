package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Junburg on 2018. 3. 26..
 */

public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * ImageView를 상속받아 세로의 길이가 가로의 길이와 같은 정사각형의 ImageView로 커스텀
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // (widthMesureSpec, widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
