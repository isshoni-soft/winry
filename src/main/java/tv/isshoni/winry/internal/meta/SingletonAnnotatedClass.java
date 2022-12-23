package tv.isshoni.winry.internal.meta;

import tv.isshoni.winry.api.context.IWinryContext;

import java.util.Objects;

public class SingletonAnnotatedClass extends AnnotatedClass {

    protected Object instance;

    public SingletonAnnotatedClass(IWinryContext context, Class<?> element) {
        super(context, element);
    }

    @Override
    public Object newInstance() {
        if (!isInitialized()) {
            this.instance = getContext().getMetaManager().construct(this, false);
        }

        return this.instance;
    }

    public Object getInstance() {
        return this.instance;
    }

    public boolean isInitialized() {
        return Objects.nonNull(this.instance);
    }

    @Override
    public String getDisplay() {
        return null;
    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
