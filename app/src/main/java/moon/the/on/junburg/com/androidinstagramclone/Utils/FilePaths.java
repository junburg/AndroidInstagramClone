package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.os.Environment;

/**
 * Created by Junburg on 2018. 3. 29..
 */

public class FilePaths {

    // "storage/emulated/0" 루트 디렉토리를 get
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    // pictures, camera, screenshots 디렉토리 get
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    public String SCREENSHOTS = ROOT_DIR + "/DCIM/Screenshots";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
