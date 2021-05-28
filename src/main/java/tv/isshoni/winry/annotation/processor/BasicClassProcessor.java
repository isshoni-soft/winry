package tv.isshoni.winry.annotation.processor;

import tv.isshoni.winry.entity.annotation.IAnnotationProcessor;
import tv.isshoni.winry.entity.element.BootstrappedClass;
import tv.isshoni.winry.logging.WinryLogger;
import tv.isshoni.winry.reflection.ReflectionManager;

import java.lang.annotation.Annotation;
import java.util.Map;

public class BasicClassProcessor implements IAnnotationProcessor<Annotation> {

    private final static WinryLogger LOGGER = WinryLogger.create("BasicClassProcessor");

    @Override
    public void executeClass(BootstrappedClass bootstrappedClass, Annotation annotation, Map<Class<?>, Object> provided) {
        if (bootstrappedClass.hasObject()) {
            LOGGER.warning("Two basic class processors present on type " + bootstrappedClass.getBootstrappedElement().getName());
            return;
        }

        if (bootstrappedClass.hasWrappedClass()) {
            LOGGER.info("Produced wrapped class: " + bootstrappedClass.getWrappedClass().getName());
        }

        Class<?> clazz = bootstrappedClass.getBootstrappedElement();
        Class<?> constructed = (bootstrappedClass.hasWrappedClass() ? bootstrappedClass.getWrappedClass() : clazz);

        if (provided.containsKey(clazz)) {
            LOGGER.info("Class: " + clazz.getName() + " is provided.");
            bootstrappedClass.setObject(provided.get(clazz));
        } else {
            LOGGER.info("Class: new " + constructed.getName() + "()");
            bootstrappedClass.setObject(ReflectionManager.construct(constructed));
        }

        LOGGER.info("Registered to class registry");
        ReflectionManager.registerClass(bootstrappedClass);
    }
}
