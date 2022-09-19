package dsenta.cachito.exception;

public class FailedToFetchEntityException extends RuntimeException  {
    public FailedToFetchEntityException() {
        super("Something went wrong -> Failed to fetch entity");
    }
}
