package tv.isshoni.winry.exception;

import java.lang.annotation.Annotation;

public class NoWeightException extends CannotCalculateWeightException {

    public NoWeightException(Annotation annotation) {
        super(annotation.annotationType().getName() + " does not have the @Weight annotation present, it is required for processed annotations.");
    }
}
