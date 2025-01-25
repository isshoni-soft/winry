package tv.isshoni.winry.api.annotation.processor;

import institute.isshoni.araragi.annotation.processor.IParameterSupplier;

import java.lang.annotation.Annotation;

public interface IWinryAdvancedAnnotationProcessor<A extends Annotation, O> extends IWinryAnnotationProcessor<A>, IParameterSupplier<A, O> { }
