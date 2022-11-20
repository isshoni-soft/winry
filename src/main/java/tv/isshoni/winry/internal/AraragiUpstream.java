package tv.isshoni.winry.internal;

import java.util.function.Predicate;

// This class is basically a staging ground for methods that will be upstreamed into Araragi
@Deprecated
public class AraragiUpstream {

    public static <T> int simpleCompare(T f, T s, Predicate<T> comparator) {
        boolean fResult = comparator.test(f);
        boolean sResult = comparator.test(s);

        if (fResult && sResult) {
            return 0;
        } else if (sResult) {
            return 1;
        } else if (fResult) {
            return -1;
        }

        return 2;
    }
}
