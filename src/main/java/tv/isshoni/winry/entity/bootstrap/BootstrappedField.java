package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.annotation.Bootstrap;
import tv.isshoni.winry.annotation.Injected;
import tv.isshoni.winry.annotation.Logger;
import tv.isshoni.winry.logging.WinryLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

public class BootstrappedField<A extends Annotation> implements IBootstrappedElement<A, Field> {

    private static final WinryLogger LOGGER = WinryLogger.create("BootstrapField", 8);

    private final Field field;

    private final A annotation;

    private final BootstrappedClass<?> target;

    public BootstrappedField(Field field, A annotation, BootstrappedClass<?> target) {
        this.field = field;
        this.annotation = annotation;
        this.target = target;
    }

    @Override
    public A getAnnotation() {
        return this.annotation;
    }

    @Override
    public Field getBootstrappedElement() {
        return this.field;
    }

    @Override
    public int getWeight() {
        LOGGER.setIndent(0);
        LOGGER.info(this.field.getName() + " " + this.target);

        if (this.target.getAnnotation() instanceof Bootstrap) {
            return 5;
        }

        if (this.annotation instanceof Logger) {
            return 5;
        }

        if (this.target.getAnnotation() instanceof Injected) {
            Injected injected = (Injected) this.target.getAnnotation();

            if (injected.weight() == Injected.DEFAULT_WEIGHT) {
                return injected.value().getWeight();
            }

            return injected.weight();
        }

        return 3;
    }

    @Override
    public void execute(Map<Class<?>, Object> provided) {
        LOGGER.info(this.target.toString());
    }

    @Override
    public String toString() {
        return "BootstrappedField[field=" + this.field.getName() + ",target=" + this.target.getBootstrappedElement().getName() + ",weight=" + this.getWeight() + "]";
    }
}
