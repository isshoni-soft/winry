package tv.isshoni.winry.internal.util;

@Deprecated
public class StringUtil {

    public static String getCharsForNumber(int i) {
        if (i <= 0) {
            i = 1;
        }

        if (i >= 27) {
            i -= 26;
            return getCharsForNumber(26) + getCharsForNumber(i);
        }

        return String.valueOf((char)(i + 64));
    }
}
