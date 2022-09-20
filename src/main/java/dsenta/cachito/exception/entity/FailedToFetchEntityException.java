package dsenta.cachito.exception.entity;

public class FailedToFetchEntityException extends RuntimeException  {
    public FailedToFetchEntityException() {
        super("Something went wrong -> Failed to fetch entity");
    }
}
