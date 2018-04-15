package moon.the.on.junburg.com.androidinstagramclone.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Junburg on 2018. 3. 29..
 */

public class FileSearch {

    /**
     * 디렉토리를 찾고 모든 디렉토리를 리스트 형태로 반환
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                // 디렉토리 절대 경로 add
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }

        return pathArray;
    }


    /**
     * 매개변수로 받은 디렉토리의 파일을 리스트 형태로 반환
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isFile()) {
                // 파일 절대 경로 add
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }

        return pathArray;
    }


}
