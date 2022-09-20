package dsenta.cachito.exception.entity;

public class FailedToPersistEntityException extends RuntimeException {
    public FailedToPersistEntityException() {
        super("Something went wrong -> Failed to persist object");
    }
}
