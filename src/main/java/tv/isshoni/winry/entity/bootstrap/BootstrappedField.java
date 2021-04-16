package tv.isshoni.winry.entity.bootstrap;

import tv.isshoni.winry.annotation.Injected;

import java.lang.reflect.Field;

public class BootstrappedField {

    private final Field field;

    private final Injected type;

    public BootstrappedField(Field field, Injected type) {
        this.field = field;
        this.type = type;
    }

    public Field getField() {
        return this.field;
    }

    public Injected getType() {
        return this.type;
    }
}
