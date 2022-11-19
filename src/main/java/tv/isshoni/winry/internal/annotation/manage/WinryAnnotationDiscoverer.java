package tv.isshoni.winry.internal.annotation.manage;

import tv.isshoni.araragi.annotation.discovery.SimpleAnnotationDiscoverer;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.internal.entity.annotation.IWinryAnnotationManager;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class WinryAnnotationDiscoverer extends SimpleAnnotationDiscoverer {

    public WinryAnnotationDiscoverer(IWinryAnnotationManager annotationManager) {
        super(annotationManager);
    }

    @Override
    public IWinryAnnotationManager getAnnotationManager() {
        return (IWinryAnnotationManager) this.annotationManager;
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
}
