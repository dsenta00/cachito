package dsenta.cachito.exception;

public class FailedToPersistEntityException extends RuntimeException {
    public FailedToPersistEntityException() {
        super("Something went wrong -> Failed to persist object");
    }
}
