package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.reflect.Method;

public interface IWrapperGenerator {

    Class<?> generate();

    void setClassTransformer(IClassTransformer classTransformer);

    void setMethodTransformer(IAnnotatedMeta<Method> method, IMethodTransformer transformer);

    void setMethodTransformer(IAnnotatedMeta<Method> method, IMethodTransformer transformer, boolean force);

    void delegateMethod(IAnnotatedMeta<Method> method, int weight, IMethodDelegator delegator);

    IAnnotatedClass toWrap();

    IClassTransformer getClassTransformer();
}
