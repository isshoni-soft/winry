package institute.isshoni.winry.internal.model.meta.bytebuddy;

import institute.isshoni.winry.internal.model.meta.IAnnotatedMeta;

import java.lang.reflect.Field;

@FunctionalInterface
public interface IFieldTransformer extends ITransformer<Field, IAnnotatedMeta<Field>> { }
