package tv.isshoni.winry.internal.model.bytebuddy;

import tv.isshoni.winry.internal.model.bootstrap.element.BootstrappedField;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldTransformingPlan extends ITransformingPlan<Field, BootstrappedField> { }
