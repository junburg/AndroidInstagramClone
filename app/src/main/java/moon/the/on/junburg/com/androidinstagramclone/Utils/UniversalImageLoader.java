package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import moon.the.on.junburg.com.androidinstagramclone.R;

/**
 * Created by Junburg on 2018. 3. 26..
 */

public class UniversalImageLoader {

    // 기본 이미지 리소스
    private static final int defaultImage = R.drawable.ic_android;
    private Context mContext;

    public UniversalImageLoader(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 이미지 로더의 기본 세팅
     * 이미지가 로딩 될 때, 이미지가 비었을 때, 이미지 보여주기가 실패했을 때 기본 이미지 show
     * 디스크와 메모리에 캐쉬를 남김
     * 이미지 로딩 전에 뷰를 리셋한다
     * 타겟 사이즈에 정확히 들어맞도록 이미지 스케일링
     * Fade In 애니메이션으로 300Millis 동안 디스플레이
     */
    public ImageLoaderConfiguration getConfig() {
        // 기본 옵션 정의 및 설정
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .considerExifParams(true)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        return configuration;
    }

    /**
     * 이미지 로더의 설정 값을 바탕으로 이미지를 디스플레이한다
     * 오버라이딩된 각각의 메서드에서 프로그레스 바의 Visibility를 처리한다
     * 로딩 시작시에만 프로그레스 바를 보이게한다
     */
    public static void setImage(String imgURL, ImageView image, final ProgressBar mProgressBar, String append) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
