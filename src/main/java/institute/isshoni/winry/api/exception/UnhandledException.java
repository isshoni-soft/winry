package institute.isshoni.winry.api.exception;

public class UnhandledException extends RuntimeException {

    public UnhandledException(Throwable throwable) {
        super("Unhandled Exception: ", throwable);
    }
}
