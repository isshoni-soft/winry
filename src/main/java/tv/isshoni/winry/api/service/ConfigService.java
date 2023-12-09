package tv.isshoni.winry.api.service;

import tv.isshoni.araragi.data.Constant;
import tv.isshoni.araragi.stream.Streams;
import tv.isshoni.winry.api.WReflect;
import tv.isshoni.winry.api.annotation.Config;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.config.IConfigSerializer;
import tv.isshoni.winry.api.context.IWinryContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

@Injected
public class ConfigService {

    private final Constant<IWinryContext> context;

    public ConfigService(@Context IWinryContext context) {
        this.context = new Constant<>(context);
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

    public <C> C load(Class<C> configClazz, String path, Class<? extends IConfigSerializer<?>> serializerClass, boolean internal) {
        IConfigSerializer<?> serializer = this.context.get().getAnnotationManager()
                .winryConstruct(this.context.get(), serializerClass);

        try {
            return serializer.deserialize(path, configClazz, internal);
        } catch (IOException e) {
            this.context.get().getExceptionManager().toss(e);
            return null;
        }
    }

    public void loadSingletonConfig(String path, Object target, Class<? extends IConfigSerializer<?>> serializerClass, boolean internal) {
        IConfigSerializer<?> serializer = this.context.get().getAnnotationManager()
                .winryConstruct(this.context.get(), serializerClass);

        Object serialized;
        try {
            serialized = serializer.deserialize(path, target.getClass(), internal);
        } catch (IOException e) {
            this.context.get().getExceptionManager().toss(e);
            return;
        }

        copyFieldsInto(serialized, target);
    }

    private void copyFieldsInto(Object from, Object to) {
        if (from.getClass().equals(to.getClass()) || from.getClass().isAssignableFrom(to.getClass())) {
            System.out.println("--> " + Arrays.toString(WReflect.getFields(from.getClass())));
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
