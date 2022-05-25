package tv.isshoni.winry.entity.bytebuddy;

import tv.isshoni.winry.entity.bootstrap.element.BootstrappedField;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldTransformingPlan extends ITransformingPlan<Field, BootstrappedField> { }
