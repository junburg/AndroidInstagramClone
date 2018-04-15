package moon.the.on.junburg.com.androidinstagramclone.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moon.the.on.junburg.com.androidinstagramclone.R;

/**
 * Created by Junburg on 2018. 3. 25..
 */


public class CameraFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment를 정의한 xml으로 부터 inflate하여 view 객체를 생성
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        return view;
    }
}
