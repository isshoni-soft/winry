package institute.isshoni.winry.internal.model.meta.bytebuddy;

import java.lang.reflect.Method;
import java.util.function.Supplier;

@FunctionalInterface
public interface IMethodDelegator {

    Object delegate(Object caller, Method original, Object[] args, Supplier<Object> nextCall);
}
