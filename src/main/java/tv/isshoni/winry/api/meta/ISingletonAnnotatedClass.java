package tv.isshoni.winry.api.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;

public interface ISingletonAnnotatedClass extends IAnnotatedClass {

    Object getInstance();

    void regenerate();

    default void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        execute(preparedAnnotationProcessor, getInstance());
    }
}
