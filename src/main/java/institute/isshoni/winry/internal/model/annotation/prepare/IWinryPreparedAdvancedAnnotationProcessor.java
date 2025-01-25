package institute.isshoni.winry.internal.model.annotation.prepare;

import institute.isshoni.araragi.annotation.processor.prepared.IPreparedParameterSupplier;
import institute.isshoni.winry.api.annotation.processor.IWinryAdvancedAnnotationProcessor;

import java.lang.annotation.Annotation;

public interface IWinryPreparedAdvancedAnnotationProcessor extends
        IWinryPreparedAnnotationProcessor<IWinryAdvancedAnnotationProcessor<Annotation, Object>>,
        IPreparedParameterSupplier<IWinryAdvancedAnnotationProcessor<Annotation, Object>> { }
