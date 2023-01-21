package tv.isshoni.winry.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

// This class is basically a staging ground for methods that will be upstreamed into Araragi
@Deprecated
public class AraragiUpstream {

    public static String toString(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        throwable.printStackTrace(print);

        String result = writer.toString();

        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        print.close();

        return result.trim();
    }
}
