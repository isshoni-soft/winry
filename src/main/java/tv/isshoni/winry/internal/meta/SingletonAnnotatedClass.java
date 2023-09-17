package tv.isshoni.winry.internal.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import tv.isshoni.winry.api.meta.ISingletonAnnotatedClass;

import java.util.Objects;

public class SingletonAnnotatedClass extends AnnotatedClass implements ISingletonAnnotatedClass {

    protected Object instance;

    public SingletonAnnotatedClass(IWinryContext context, Class<?> element) throws Throwable {
        super(context, element);

        refreshAnnotations();
        transform(new WinryWrapperGenerator(context, this));
    }

    public SingletonAnnotatedClass(IWinryContext context, Class<?> element, Object object) {
        super(context, element);
        this.instance = object;
    }

    @Override
    public Object getInstance() {
        return this.instance;
    }

    @Override
    public void regenerate() {
        regenerate(getInstance());
    }

    @Override
    public void execute() {
        try {
            this.instance = newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        getContext().getAnnotationManager().toExecutionList(this.getElement(), this.getAnnotations())
                .forEach(this::execute);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SingletonAnnotatedClass other)) {
            return false;
        }

        return Objects.equals(this.instance, other.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.instance);
    }
}
