package dsenta.cachito.exception.resource;

public class IdNotProvidedException extends IllegalArgumentException {
    public IdNotProvidedException() {
        super("ID not provided");
    }
}
