package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.annotation.processor.prepared.IPreparedAnnotationProcessor;
import tv.isshoni.araragi.reflect.ReflectedModifier;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.prepare.IWinryPreparedAnnotationProcessor;
import tv.isshoni.winry.internal.model.meta.IAnnotatedMeta;
import tv.isshoni.winry.internal.model.meta.ITransformedClass;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AnnotatedClass extends AbstractAnnotatedMeta<Class<?>> implements ITransformedClass {

    protected final Set<ReflectedModifier> modifiers;

    protected Class<?> transformed;

    protected final List<AnnotatedMethod> methods;

    protected final List<IAnnotatedMeta<Field>> fields;

    public AnnotatedClass(IWinryContext context, Class<?> element) {
        super(context, element);
        this.modifiers = ReflectedModifier.getModifiers(element);
        this.methods = new LinkedList<>();
        this.fields = new LinkedList<>();
    }

    public List<AnnotatedMethod> getMethods() {
        return this.methods;
    }

    public Object newInstance() {
        return getContext().getMetaManager().construct(this, true);
    }

    @Override
    public void execute(IPreparedAnnotationProcessor preparedAnnotationProcessor) {
        preparedAnnotationProcessor.executeClass(this.getElement());
    }

    @Override
    public void transform(IWinryPreparedAnnotationProcessor preparedAnnotationProcessor) {
//        preparedAnnotationProcessor.transformClass();

        getMethods().forEach(AnnotatedMethod::transform);
    }

    @Override
    public Class<?> getTransform() {
        return this.transformed;
    }

    @Override
    public Set<ReflectedModifier> getModifiers() {
        return Collections.unmodifiableSet(this.modifiers);
    }

    @Override
    public boolean isTransformed() {
        return Objects.nonNull(this.transformed);
    }

    public boolean isDirty() {
        return true;
    }

    @Override
    public String getDisplay() {
        return this.element.getSimpleName();
    }
}
