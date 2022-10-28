package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IParameterSupplier;

import java.lang.annotation.Annotation;

public interface IWinryAdvancedAnnotationProcessor<A extends Annotation, O> extends IWinryAnnotationProcessor<A>, IParameterSupplier<A, O> { }
