package moon.the.on.junburg.com.androidinstagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import moon.the.on.junburg.com.androidinstagramclone.Profile.AccountSettingsActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.Permissions;

/**
 * Created by Junburg on 2018. 3. 25..
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";

    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment를 정의한 xml으로 부터 inflate하여 view 객체를 생성
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Button btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: launching camera");

                if (((ShareActivity) getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM) {
                    // 카메라 사용 퍼미션이 allow(true)라면
                    if (((ShareActivity) getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])) {
                        Log.d(TAG, "onClick: starting camera");
                        // 카메라 촬영
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    }
                } else {

                    Intent intent = new Intent(getActivity(), ShareActivity.class);
                    // FLAG_ACTIVITY_CLEAR_TASK -> EX) 액티비티 스택(태스크) : ABCDE, E가 C를 호출 -> 액티비티 스택: ABC
                    // ShareActivity로 이동하고 그 위의 액티비티들을 삭제
                    // FLAG_ACTIVITY_NEW_TASK -> 새로운 태스크를 생성해서 액티비티 실행
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라가 촬영을 끝낸 결과
        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult:  done taking photo");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen");
            // navigate to the final share screen to publish photo

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            if (isRootTask()) {
                try {
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    startActivity(intent);
                } catch (NullPointerException e) {
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }

            }
            // 프로필 이미지 변경을 카메라를 사용해서 하는 경우
            else {
                try {
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                } catch (NullPointerException e) {
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }
        }
    }
}
