package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.api.meta.IAnnotatedClass;
import tv.isshoni.winry.api.meta.IAnnotatedMethod;

public interface IWrapperGenerator {

    Class<?> generate();

    void setClassTransformer(IClassTransformer classTransformer);

    void setMethodTransformer(IAnnotatedMethod method, IMethodTransformer transformer);

    void setMethodTransformer(IAnnotatedMethod method, IMethodTransformer transformer, boolean force);

    void delegateMethod(IAnnotatedMethod method, int weight, IMethodDelegator delegator);

    boolean hasTransformer(IAnnotatedMethod method);

    IAnnotatedClass toWrap();

    IClassTransformer getClassTransformer();
}
