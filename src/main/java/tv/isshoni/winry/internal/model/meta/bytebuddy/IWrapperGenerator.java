package tv.isshoni.winry.internal.model.meta.bytebuddy;

import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;

public interface IWrapperGenerator {

    void setClassTransformer(IClassTransformer classTransformer);

    Class<?> generate();

    IAnnotatedClass toWrap();

    IClassTransformer getClassTransformer();
}
