package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import moon.the.on.junburg.com.androidinstagramclone.R;

/**
 * Created by Junburg on 2018. 3. 26..
 */

public class GridImageAdapter extends ArrayAdapter<String> {

    // Variable
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    // GridAdapter 생성자
    public GridImageAdapter(Context mContext, int layoutResource, String mAppend, ArrayList<String> imgURLs) {
        super(mContext, layoutResource, imgURLs);
        this.mContext = mContext;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imgURLs = imgURLs;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
    }

    /**
     * getView는 화면에 해당 포지션이 보이면 호출
     * convert view를 사용해, 스크롤시 inflate에 걸리는 시간 문제를 해결
     * View 설정시 많은 비용이 발생하기 때문에 스크롤의 자연스러움을 보장하기 위함
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;

        // converView가 존재하지 않으면 convertView 정의
        if (convertView == null) {

            // layoutResource View Inflate
            convertView = mInflater.inflate(layoutResource, parent, false);

            // ViewHolder의 위젯 설정
            viewHolder = new ViewHolder();
            viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            viewHolder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

            // viewHolder를 tag해서 재사용 (findViewById 할 필요없음)
            convertView.setTag(viewHolder);

        } else {
            // convert view가 존재하면 tag로 부터 재사용
            viewHolder = (ViewHolder) convertView.getTag();

        }

        // 해당 위치 이미지의 URL 반환
        String imgURL = getItem(position);

        // 이미지를 뷰에 뿌려주고, 이미지 로딩에 따라 프로그레스 바의 Visibility 수정
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, viewHolder.image, new ImageLoadingListener() {
            // 이미지 로딩이 시작될 때 프로그레스 바 VISIBLE
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.mProgressBar != null) {
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            // 이미지 로딩에 실패했을 경우 프로그레스 바 GONE
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.mProgressBar != null) {
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }

            // 이미지 로딩이 완료됐을 경우 프로그레스 바 GONE
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.mProgressBar != null) {
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }

            // 이미지 로딩이 취소됐을 경우 프로그레스 바 GONE
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.mProgressBar != null) {
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
}
