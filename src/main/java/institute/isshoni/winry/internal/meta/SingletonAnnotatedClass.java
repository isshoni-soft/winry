package institute.isshoni.winry.internal.meta;

import institute.isshoni.winry.api.context.IWinryContext;
import institute.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import institute.isshoni.winry.api.meta.ISingletonAnnotatedClass;

import java.util.Objects;

public class SingletonAnnotatedClass extends AnnotatedClass implements ISingletonAnnotatedClass {

    protected final Object instance;

    public SingletonAnnotatedClass(IWinryContext context, Class<?> element) throws Throwable {
        super(context, element);

        refreshAnnotations();
        transform(new WinryWrapperGenerator(context, this));

        this.instance = newInstance();
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
}
