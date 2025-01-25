package institute.isshoni.winry.api.service;

import institute.isshoni.araragi.data.Constant;
import institute.isshoni.araragi.stream.Streams;
import institute.isshoni.winry.api.WReflect;
import institute.isshoni.winry.api.annotation.Config;
import institute.isshoni.winry.api.annotation.Injected;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.config.IConfigSerializer;
import institute.isshoni.winry.api.context.IWinryContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Injected
public class ConfigService {

    private final Constant<IWinryContext> context;

    private final Map<Class<? extends IConfigSerializer<?>>, IConfigSerializer<?>> serializers;

    public ConfigService(@Context IWinryContext context) {
        this.context = new Constant<>(context);
        this.serializers = new HashMap<>();
    }

    public void save(Object config) {
        if (!config.getClass().isAnnotationPresent(Config.class)) {
            return;
        }

        Config configAnnotation = config.getClass().getAnnotation(Config.class);
        String path = configAnnotation.value();
        Class<? extends IConfigSerializer<?>> serializerClass = configAnnotation.serializer();

        save(config, path, serializerClass);
    }

    public void save(Object config, String path, Class<? extends IConfigSerializer<?>> serializerClass) {
        IConfigSerializer<Object> serializer = (IConfigSerializer<Object>) findSerializer(serializerClass);

        // if input type does not match serializer type, utilize the object conversion method.
        if (!serializer.getType().isAssignableFrom(config.getClass())) {
            config = serializer.convert(config);
        }

        try {
            serializer.serialize(path, config);
        } catch (IOException e) {
            this.context.get().getExceptionManager().toss(e);
        }
    }

    public <C> C load(Class<C> configClazz) {
        if (!configClazz.isAnnotationPresent(Config.class)) {
            return null;
        }

        Config configAnnotation = configClazz.getAnnotation(Config.class);
        String path = configAnnotation.value();
        Class<? extends IConfigSerializer<?>> serializerClass = configAnnotation.serializer();
        boolean internal = configAnnotation.internal();

        return load(configClazz, path, serializerClass, internal);
    }

    public void loadSingletonConfig(Class<?> configClazz, Object target) {
        if (!configClazz.isAnnotationPresent(Config.class)) {
            return;
        }

        Config configAnnotation = configClazz.getAnnotation(Config.class);
        String path = configAnnotation.value();
        Class<? extends IConfigSerializer<?>> serializerClass = configAnnotation.serializer();
        boolean internal = configAnnotation.internal();

        loadSingletonConfig(path, target, serializerClass, internal);
    }

    public <C> C load(Class<C> configClazz, String path, Class<? extends IConfigSerializer<?>> serializerClass, boolean internal) {
        IConfigSerializer<?> serializer = findSerializer(serializerClass);

        try {
            return serializer.deserialize(path, configClazz, internal);
        } catch (IOException e) {
            this.context.get().getExceptionManager().toss(e);
            return null;
        }
    }

    public void loadSingletonConfig(String path, Object target, Class<? extends IConfigSerializer<?>> serializerClass, boolean internal) {
        IConfigSerializer<?> serializer = findSerializer(serializerClass);

        Object serialized;
        try {
            serialized = serializer.deserialize(path, target.getClass(), internal);
        } catch (IOException e) {
            this.context.get().getExceptionManager().toss(e);
            return;
        }

        copyFieldsInto(serialized, target);
    }

    private IConfigSerializer<?> findSerializer(Class<? extends IConfigSerializer<?>> serializerClass) {
        if (this.serializers.containsKey(serializerClass)) {
            return this.serializers.get(serializerClass);
        }

        IConfigSerializer<?> serializer = this.context.get().getAnnotationManager()
                .winryConstruct(this.context.get(), serializerClass);
        this.serializers.put(serializerClass, serializer);

        return serializer;
    }

    private void copyFieldsInto(Object from, Object to) {
        if (from.getClass().equals(to.getClass()) || from.getClass().isAssignableFrom(to.getClass())) {
            Streams.to(WReflect.getFields(from.getClass())).forEach(field -> {
                boolean accessible = field.canAccess(from);

                try {
                    Field targetField = WReflect.getField(to.getClass(), field.getName());
                    if (!accessible) {
                        field.setAccessible(true);
                        targetField.setAccessible(true);
                    }

                    targetField.set(to, field.get(from));

                    if (!accessible) {
                        field.setAccessible(false);
                        targetField.setAccessible(false);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    this.context.get().getExceptionManager().toss(e);
                }
            });
        }
    }
}
