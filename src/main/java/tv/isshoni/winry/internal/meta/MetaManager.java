package tv.isshoni.winry.internal.meta;

import tv.isshoni.araragi.data.collection.map.TypeMap;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.api.meta.IMetaManager;
import tv.isshoni.winry.internal.model.logging.ILoggerFactory;
import tv.isshoni.winry.internal.model.meta.IAnnotatedClass;

public class MetaManager implements IMetaManager {

    private final AraragiLogger logger;

    private final TypeMap<Class<?>, IAnnotatedClass> storedClassMetas;

    private IWinryContext context;

    public MetaManager(ILoggerFactory loggerFactory) {
        this.logger = loggerFactory.createLogger("MetaManager");
        this.storedClassMetas = new TypeMap<>();
    }

    @Override
    public void setContext(IWinryContext context) {
        this.context = context;
    }

    @Override
    public IWinryContext getContext() {
        return this.context;
    }

    @Override
    public IAnnotatedClass generateMeta(Class<?> element) {
        if (this.storedClassMetas.containsKey(element)) {
            logger.warn("Returning already generated meta for class: " + element.getName());
            return this.storedClassMetas.get(element);
        }

        AnnotatedClass annotatedClass = new AnnotatedClass(this.context, element);

        this.storedClassMetas.put(element, annotatedClass);

        return annotatedClass;
    }

    @Override
    public IAnnotatedClass getMeta(Object element) {
        Class<?> clazz = element.getClass();

        if (Class.class.isAssignableFrom(element.getClass())) {
            clazz = (Class<?>) element;
        }

        return this.storedClassMetas.get(clazz);
    }

    @Override
    public <R> R construct(IAnnotatedClass meta) throws Throwable {
        return meta.newInstance();
    }
}
