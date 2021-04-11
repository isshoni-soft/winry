package tv.isshoni.winry.annotation;

import tv.isshoni.winry.entity.inject.InjectedType;

public @interface Injected {

    InjectedType value() default InjectedType.DEFAULT;
}
