package model;

import institute.isshoni.winry.api.annotation.exception.Handler;
import institute.isshoni.winry.api.exception.IExceptionHandler;

@Handler(value = RuntimeException.class, global = true)
public class RuntimeExceptionHandler implements IExceptionHandler<RuntimeException> {

    @Override
    public void handle(RuntimeException exception) { }
}
