package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.util.ComparatorUtil;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.api.context.IContextual;
import tv.isshoni.winry.api.context.ILoggerFactory;
import tv.isshoni.winry.api.context.IWinryContext;
import tv.isshoni.winry.internal.model.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class WinryAnnotationDiscoverer extends SimpleAnnotationDiscoverer implements IContextual {

    private final AraragiLogger LOGGER;

    private final Constant<IWinryContext> context;

    public WinryAnnotationDiscoverer(IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        super(annotationManager);

        this.context = new Constant<>();
        this.LOGGER = loggerFactory.createLogger(this.getClass());
    }

    public void setContext(IWinryContext context) {
        this.context.set(context);
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return (IWinryAnnotationManager) this.annotationManager;
    }

    @Override
    public IAnnotationDiscoverer discoverAnnotations() {
        Set<Class<? extends Annotation>> all = findProcessorAnnotations();
        List<Class<? extends Annotation>> ordered = new LinkedList<>(all);
        ordered.sort((first, second) -> {
            int simple = ComparatorUtil.simpleCompare(first, second, e -> e.isAnnotationPresent(Transformer.class));

            if (simple != 2) {
                return simple;
            }

            return -1;
        });

        ordered.forEach(ac -> safelyRecursiveDiscover(ac, all, new Stack<>()));

        return this;
    }

    @Override
    public <A extends Annotation> Set<Class<?>> findWithAnnotations(Class<A> clazz) {
        Set<Class<?>> result = new HashSet<>(super.findWithAnnotations(clazz));

        for (Class<?> manLoad : getAnnotationManager().getAllManuallyLoaded()) {
            if (!manLoad.isAnnotationPresent(clazz)) {
                continue;
            }

            result.add(manLoad);
        }

        Class<?> bootstrapped = getAnnotationManager().getBootstrapper().getBootstrapped();

        if (bootstrapped.isAnnotationPresent(clazz)) {
            result.add(bootstrapped);
        }

        return result;
    }

    @Override
    public Constant<IWinryContext> getContext() {
        return this.context;
    }
}
