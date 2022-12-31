package tv.isshoni.winry.internal.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.internal.model.meta.bytebuddy.IMethodDelegator;
import tv.isshoni.winry.internal.model.exception.IExceptionManager;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class WinryBytebuddyDelegator {

    private final IExceptionManager exceptionManager;

    private final Queue<IMethodDelegator> delegators;

    public WinryBytebuddyDelegator(List<Pair<IMethodDelegator, Integer>> delegators, IExceptionManager exceptionManager) {
        this.exceptionManager = exceptionManager;
        this.delegators = Streams.to(delegators, Streams.collectionToPairStream())
                .sorted(Pair.compareSecond())
                .mapFirst()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @RuntimeType
    public Object executeMethod(@This Object object, @Origin Method method, @SuperCall Callable<Object> zuper, @AllArguments Object[] args) {
            if (this.delegators.isEmpty()) {
                return this.exceptionManager.unboxCallable(zuper, method).get();
            }

            return this.delegators.poll().delegate(object, method, args,
                    this.exceptionManager.unboxCallable(getNext(object, method, args, zuper), method));
    }

    private Callable<Object> getNext(Object object, Method method, Object[] args, Callable<Object> zuper) {
        if (this.delegators.isEmpty()) {
            return zuper;
        } else {
            return () -> this.delegators.poll().delegate(object, method, args,
                    this.exceptionManager.unboxCallable(getNext(object, method, args, zuper), method));
        }
    }
}
