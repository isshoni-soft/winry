package tv.isshoni.winry.internal.bytebuddy.delegator;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class WinryBytebuddyDelegator {

    private final List<WinryDelegator> delegators;

    public WinryBytebuddyDelegator() {
        this.delegators = new LinkedList<>();
    }

    @RuntimeType
    public Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
        try {
            return zuper.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @IgnoreForBinding
    public void register(Object delegator) {
        this.delegators.add(new WinryDelegator(delegator));
    }
}
