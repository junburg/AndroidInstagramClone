package moon.the.on.junburg.com.androidinstagramclone.Utils;

/**
 * Created by Junburg on 2018. 3. 27..
 */

public class StringManipulation {

    /**
     * .을 공백으로 문자열 조작
     */
    public static String expandUsername(String username) {
        return username.replace(".", " ");

    }

    /**
     * 공백을 .으로 문자열 조작
     */
    public static String condeseUsername(String username) {
        return username.replace(" ", ".");

    }

    /**
     * 해쉬태그 처리
     */
    public static String getTags(String string) {

        // indexOf(문자열의 인덱스 값 반환)
        // #의 인덱스가 0이 아니라면( 문자열 처음에 #이 존재하지 않는다면)
        if (string.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;

            for (char c : charArray) {

                // 문자 배열을 탐색하다 #을 만나면 foundWord 값을 true로 변경하고 StringBuilder에 해당 문자 append
                if (c == '#') {
                    foundWord = true;
                    sb.append(c);
                }

                // 탐색 값이 #이 아닐 때 foundWord의 값이 true라면 StringBuilder에 해당 문자 append
                else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }

                // 공백이라면 foundWord를 false로 변경( 해시태그 단어가 아닐 경우 )
                if (c == ' ') {
                    foundWord = false;
                }
            }
            // StringBuilder를 문자열로 변경. 띄어쓰기는 없애고, '#'은 ',#'으로 변경
            String s = sb.toString().replace(" ", "").replace("#", ", #");
            // 0번째 인덱스를 제외한 문자열 반환 ( s[0] = "," )
            return s.substring(1, s.length());
        }
        return string;
    }

    /*
    <Example>
    In -> 나는 오늘도 코딩을 했다 #java #android #client
    out -> #java, #android, #client
     */
}
