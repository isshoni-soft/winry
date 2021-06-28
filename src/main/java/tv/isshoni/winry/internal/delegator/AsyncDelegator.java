package tv.isshoni.winry.internal.delegator;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import tv.isshoni.winry.annotation.api.Delegator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class AsyncDelegator {

    @RuntimeType
    @Delegator
    public static Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
        System.out.println("Executing: " + method + " with args " + Arrays.toString(args) + " on " + object);

        try {
            return zuper.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
