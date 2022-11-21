package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.araragi.util.ComparatorUtil;
import tv.isshoni.winry.api.annotation.meta.Transformer;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;
import tv.isshoni.winry.internal.entity.logging.ILoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class WinryAnnotationDiscoverer extends SimpleAnnotationDiscoverer {

    private final AraragiLogger LOGGER;

    public WinryAnnotationDiscoverer(IWinryAnnotationManager annotationManager, ILoggerFactory loggerFactory) {
        super(annotationManager);

        this.LOGGER = loggerFactory.createLogger(this.getClass());
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

        Streams.to(getAnnotationManager().getAllManuallyLoaded())
                .filter(c -> c.isAnnotationPresent(clazz))
                .forEach(result::add);

        Class<?> bootstrapped = getAnnotationManager().getBootstrapper().getBootstrapped();

        if (bootstrapped.isAnnotationPresent(clazz)) {
            result.add(bootstrapped);
        }

        return result;
    }

    @Override
    public void safelyRecursiveDiscover(Class<? extends Annotation> clazz, Set<Class<? extends Annotation>> all, Stack<Class<? extends Annotation>> levels) {
        LOGGER.debug("Safely recursively discovering: " + clazz);
        super.safelyRecursiveDiscover(clazz, all, levels);
    }
}
