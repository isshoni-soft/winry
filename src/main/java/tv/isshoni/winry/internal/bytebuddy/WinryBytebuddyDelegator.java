package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.internal.entity.bytebuddy.MethodDelegator;
import tv.isshoni.winry.internal.entity.exception.IExceptionManager;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WinryBytebuddyDelegator {

    private final IExceptionManager exceptionManager;

    private final Queue<MethodDelegator> delegators;

    public WinryBytebuddyDelegator(List<Pair<MethodDelegator, Integer>> delegators, IExceptionManager exceptionManager) {
        this.exceptionManager = exceptionManager;
        this.delegators = Streams.to(delegators, Streams.collectionToPairStream())
                .sorted(Pair.compareSecond())
                .mapFirst()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @RuntimeType
    public Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) throws Exception {
        try {
            if (this.delegators.isEmpty()) {
                return zuper.call();
            }

            MethodDelegator delegator = this.delegators.poll();

            return delegator.delegate(object, method, args, getNext(object, method, args, () -> {
                try {
                    return zuper.call();
                } catch (Exception e) {
                    this.exceptionManager.toss(e, method);
                    throw new RuntimeException(e);
                }
            }));
        } catch (Throwable throwable) {
            this.exceptionManager.toss(throwable, method);
            throw throwable;
        }
    }

    private Supplier<Object> getNext(Object object, Method method, Object[] args, Supplier<Object> zuper) {
        if (this.delegators.isEmpty()) {
            return zuper;
        } else {
            return () -> this.delegators.poll().delegate(object, method, args, getNext(object, method, args, zuper));
        }
    }
}
