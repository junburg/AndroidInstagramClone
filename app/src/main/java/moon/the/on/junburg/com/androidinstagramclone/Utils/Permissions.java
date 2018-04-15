package moon.the.on.junburg.com.androidinstagramclone.Utils;


import android.Manifest;

/**
 * Created by Junburg on 2018. 3. 28..
 */

public class Permissions {

    /**
     * 필요한 모든 퍼미션
     */
    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * 카메라 사용 퍼미션
     */
    public static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA
    };

    /**
     * 외부 저장소 쓰기 퍼미션
     */
    public static final String[] STORAGE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 외부 저장소 읽기 퍼미션
     */
    public static final String[] READ_STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}
