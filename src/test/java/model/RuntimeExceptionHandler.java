package model;

import tv.isshoni.winry.api.annotation.exception.Handler;
import tv.isshoni.winry.api.exception.IExceptionHandler;

@Handler(value = RuntimeException.class, global = true)
public class RuntimeExceptionHandler implements IExceptionHandler<RuntimeException> {

    @Override
    public void handle(RuntimeException exception) { }
}
