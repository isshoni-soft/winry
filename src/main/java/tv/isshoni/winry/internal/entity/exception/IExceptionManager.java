package tv.isshoni.winry.internal.entity.exception;

import tv.isshoni.winry.api.exception.IExceptionHandler;

import java.lang.reflect.Method;
import java.util.List;

public interface IExceptionManager {

    void toss(Throwable throwable);

    void toss(Throwable throwable, Method context);

    void register(Class<? extends IExceptionHandler<?>> handler);

    <E extends Throwable> List<IExceptionHandler<E>> getHandlersFor(Class<E> clazz);
}

// Exception occurs: -- (catch e -> toss(e))

// A: Method does not have special ExceptionHandler annotation
// A -> Does exception type handle global handler?
// A --> A: No global handler found for exception type (END)
// A --> B: Found a global handler for exception type
// A.B ---> Does global handler enforceSingleton?
// A.B ----> A: Yes
// A.B.A -----> Use singleton flow (END)
// A.B ----> B: No
// A.B.B -----> Create new instance & use that (END)

// B: Method has special ExceptionHandler annotation
// B -> does ExceptionHandler useSingleton?
// B --> A: Yes
// B.A ---> Use singleton flow (END)
// B --> B: No
// B.B ---> Does Exception Handler force singleton?
// B.B ----> A: Yes
// B.B.A ------> Use singleton flow (END)
// B.B ----> B: No
// B.B.B ------> Create new exception handler instance and use it (END)

// Singleton flow
// Does Singleton already exist?
// A: Yes
// A -> Use singleton instance on hand (END)
// B: No
// B -> Create singleton instance, store it, and use it (END)
