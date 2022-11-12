package unit;

import org.junit.Test;
import tv.isshoni.winry.internal.util.StringUtil;

import static org.junit.Assert.assertEquals;

public class TestStringUtil {

    @Test
    public void testStringUtil() {
        assertEquals("ZA", StringUtil.getCharsForNumber(27));
    }
}
