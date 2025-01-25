package institute.isshoni.winry.api.config;

import java.io.IOException;

public interface IConfigSerializer<T> {

    T convert(Object value);

    void serialize(String path, T object) throws IOException;

    <R> R deserialize(String path, Class<R> target, boolean internal) throws IOException;

    default <R> R deserialize(String path, Class<R> target) throws IOException {
        return deserialize(path, target, false);
    }

    Class<? extends T> getType();
}
