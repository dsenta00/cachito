package dsenta.cachito.exception;

public class CannotDropParentResourceException extends IllegalArgumentException {
    public CannotDropParentResourceException(String resource) {
        super(String.format("Cannot drop because resource %s is parent", resource));
    }
}
