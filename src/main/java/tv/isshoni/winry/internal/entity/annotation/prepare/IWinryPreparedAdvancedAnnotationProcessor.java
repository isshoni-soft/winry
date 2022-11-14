package tv.isshoni.winry.internal.entity.annotation.prepare;

import tv.isshoni.araragi.annotation.model.IPreparedParameterSupplier;
import tv.isshoni.winry.internal.entity.annotation.IWinryAdvancedAnnotationProcessor;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAdvancedAnnotationProcessor extends
        IWinryPreparedAnnotationProcessor<IWinryAdvancedAnnotationProcessor<Annotation, Object>>,
        IPreparedParameterSupplier<IWinryAdvancedAnnotationProcessor<Annotation, Object>> { }
