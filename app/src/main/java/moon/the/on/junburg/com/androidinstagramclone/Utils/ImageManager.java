package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Junburg on 2018. 3. 29..
 */

public class ImageManager {

    private static final String TAG = "ImageManager";


    /**
     * 매개변수로 받은 이미지 Url을 Bitmap으로 변환해서 반환
     */
    public static Bitmap getBitmap(String imgUrl) {
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            // 로컬에 존재하는 파일(이미지)로부터 바이트 단위로 입력을 받음
            fis = new FileInputStream(imageFile);
            // InputStream으로 부터 bitmap 파일 생성
            bitmap = BitmapFactory.decodeStream(fis);

            // 파일이 존재하지 않을 가능성이 있기 때문에 Exception 처리
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getBitmap: FileNotFoundException" + e.getMessage());
        } finally {
            try {
                // 스트림 Close
                fis.close();
            } catch (IOException e) {
                Log.e(TAG, "instance initializer: FileNotFoundException: " + e.getMessage());
            }
        }
        return bitmap;
    }

    /**
     * 비트맵을 바이트 배열로 변환해서 반환
     * 퀄리티 0 ~ 100
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality) {
        // ByteArrayOutputStream: 내부 저장 공간에 바이트 배열을 저장
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // 비트맵 파일의 형식(JPEG)과 퀄리티를 지정
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        // 스트림에 있는 비트맵 파일을 바이트 배열로 변환하고 반환
        return stream.toByteArray();
    }










}
