package tv.isshoni.winry.internal.model.bytebuddy;

import java.lang.reflect.Method;
import java.util.function.Supplier;

@FunctionalInterface
public interface MethodDelegator {

    Object delegate(Object caller, Method original, Object[] args, Supplier<Object> nextCall);
}
