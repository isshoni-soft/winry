package tv.isshoni.winry.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class AsyncManager {

    @RuntimeType
    public static Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
        System.out.println("Executing: " + method + " with args " + Arrays.toString(args) + " on " + object);

        try {
            return zuper.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
