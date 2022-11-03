package tv.isshoni.winry.entity.bytebuddy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface MethodDelegator {

    Object delegate(Object caller, Method original, Object[] args, Callable<Object> nextCall);
}
