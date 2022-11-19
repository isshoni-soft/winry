package tv.isshoni.winry.internal.entity.annotation.prepare;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedParameterSupplier;
import tv.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAdvancedAnnotationProcessor extends
        IWinryPreparedAnnotationProcessor<IWinryAdvancedAnnotationProcessor<Annotation, Object>>,
        IPreparedParameterSupplier<IWinryAdvancedAnnotationProcessor<Annotation, Object>> { }
