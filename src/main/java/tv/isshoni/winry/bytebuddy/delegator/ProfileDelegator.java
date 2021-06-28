package tv.isshoni.winry.bytebuddy.delegator;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.Callable;

public class ProfileDelegator {

    private static final WinryLogger LOGGER = WinryLogger.create("Profiling");

    @RuntimeType
    public static Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
        Instant prev = Instant.now();

        try {
            Object result = zuper.call();

            LOGGER.info("Method execution: " + method.getName() + " took " + (Instant.now().toEpochMilli() - prev.toEpochMilli()) + "ms!");
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
