package tv.isshoni.winry.internal.entity.bytebuddy;

import tv.isshoni.winry.internal.entity.bootstrap.element.BootstrappedField;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldTransformingPlan extends ITransformingPlan<Field, BootstrappedField> { }
