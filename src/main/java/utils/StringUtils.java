package utils;

/**
 * @author liufei
 * @Description
 * @Date 2020/8/26 9:56
 */
public class StringUtils {
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
