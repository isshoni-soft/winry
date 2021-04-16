package tv.isshoni.winry.entity.bootstrap;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BootstrappedClass<A extends Annotation> {

    private final Class<?> clazz;

    private Object object;

    private final A annotation;

    private final Type type;

    private final List<BootstrappedField> fields;

    public BootstrappedClass(Class<?> clazz, A annotation, Type type) {
        this.clazz = clazz;
        this.annotation = annotation;
        this.type = type;
        this.fields = new LinkedList<>();
    }

    public void addField(BootstrappedField field) {
        this.fields.add(field);
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Object getObject() {
        return this.object;
    }

    public A getAnnotation() {
        return this.annotation;
    }

    public Type getType() {
        return this.type;
    }

    public ImmutableList<BootstrappedField> getFields() {
        return ImmutableList.copyOf(this.fields);
    }

    public boolean hasObject() {
        return Objects.nonNull(this.object);
    }

    public enum Type {
        BOOTSTRAP_CLASS,
        INJECTED_DEFAULT,
        INJECTED_DATABASE,
        INJECTED_SERVICE
    }
}
