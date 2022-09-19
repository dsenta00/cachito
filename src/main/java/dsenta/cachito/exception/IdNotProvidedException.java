package dsenta.cachito.exception;

public class IdNotProvidedException extends IllegalArgumentException {
    public IdNotProvidedException() {
        super("ID not provided");
    }
}
