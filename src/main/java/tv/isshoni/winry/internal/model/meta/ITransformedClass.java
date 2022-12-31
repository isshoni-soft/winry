package tv.isshoni.winry.internal.model.meta;

import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;

public interface ITransformedClass extends ITransformable<Class<?>> {

    default void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor) {
        transform(preparedAnnotationProcessor, );
    }

    Class<?> getTransform();
}
