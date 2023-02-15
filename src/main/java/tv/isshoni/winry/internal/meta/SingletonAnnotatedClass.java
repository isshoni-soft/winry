package tv.isshoni.winry.internal.meta;

import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.meta.bytebuddy.WinryWrapperGenerator;
import tv.isshoni.winry.api.meta.ISingletonAnnotatedClass;

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
}
