package moon.the.on.junburg.com.androidinstagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import moon.the.on.junburg.com.androidinstagramclone.Profile.AccountSettingsActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.FilePaths;
import moon.the.on.junburg.com.androidinstagramclone.Utils.FileSearch;
import moon.the.on.junburg.com.androidinstagramclone.Utils.GridImageAdapter;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;

    // Widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    // Variables
    private ArrayList<String> directories;
    private String mAppend = "file://";
    private String mSelectedImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment를 정의한 xml으로 부터 inflate하여 view 객체를 생성
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        gridView = (GridView) view.findViewById(R.id.gridView);
        directorySpinner = (Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started");

        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the gallery fragment");
                getActivity().finish();
            }
        });

        TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: navigation to the final share screen");

                if (isRootTask()) {
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    // NextActivity로 이동하면서 이미지 뷰에 set된 이미지를 함께 put
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }
                // 프로필 이미지를 갤러리를 사용해서 변경하는 경우, AccountSettingActivity로 이동
                else {
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    // NextActivity로 이동하면서 이미지 뷰에 set된 이미지를 함께 put
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }


            }
        });

        init();
        return view;
    }

    /**
     * GalleryFragment가 존재하는 액티비티가 루트 태스크인지 확인
     */
    private boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * GalleryFragment Initialize
     */
    private void init() {
        FilePaths filePaths = new FilePaths();

        // "/storage/emulated/0/pictures" 안에 있는 폴더들이 있다면 directories 리스트에 add

        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        // "/storage/emulated/0/DCIM/Camera", "/storage/emulated/0/Screenshots" 디렉토리 리스트에 add
        directories.add(filePaths.CAMERA);
        directories.add(filePaths.SCREENSHOTS);

        // 스피너 아이템 텍스트가 디렉토리 경로 전체로 set되므로 디렉토리 경로를 마지막 폴더이름으로 change
        // Ex) Directory name = "/storage/emulated/0/DCIM/Camera" -> "Camera"
        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index + 1);
            directoryNames.add(string);
        }

        // 스피너 어댑터 정의
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        // 스피너의 디렉토리 폴더 select -> 그리드 뷰에 폴더에 있는 사진 파일들 set
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemSelected: selected: " + directories.get(position));
                // setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 그리드 뷰에 해당 디렉토리 사진 파일들을 set
     */
    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        // 매개변수로 받은 디렉토리 경로에 있는 파일들의 경로를 리스트에 반환
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        Log.d(TAG, "setupGridView: imgSize " + imgURLs.size());
        // 전체 화면의 3분의 1을 그리드 뷰 한 칼럼의 가로 길이로 지정
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        // 그리드 뷰 어댑터로 파일 경로를 보내고 이미지 set
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        try {
            // 프래그먼트가 생성된 첫 화면에서 0번째 인덱스의 사진 파일을 이미지 뷰에 set
            setImage(imgURLs.get(0), galleryImage, mAppend);
            // 첫 화면에서 Next버튼 클릭시에 0번째 인덱스의 사진을 Intent로 넘겨줌
            mSelectedImage = imgURLs.get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: " + e.getMessage());
        }

        // 그리드 뷰에서 클릭한 이미지를 이미지 뷰에 set
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }

    /**
     * 매개변수로 받은 이미지 뷰에 ImageLoader를 사용해 이미지 set
     * 이미지 로딩 과정에 따라 프로그레스 바의 visibility set
     */
    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage:  setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}