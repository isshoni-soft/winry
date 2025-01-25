package institute.isshoni.winry.internal.model.meta;

public interface ITransformedClass extends ITransformable<Class<?>> {

    Class<?> getTransform();

    boolean isTransformed();
}
