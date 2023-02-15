package tv.isshoni.winry.api.meta;

import tv.isshoni.winry.internal.model.meta.IDeclared;
import tv.isshoni.winry.internal.model.meta.ITransformable;

import java.lang.reflect.Field;

public interface IAnnotatedField extends IDeclared<Field>, ITransformable<Field> { }
