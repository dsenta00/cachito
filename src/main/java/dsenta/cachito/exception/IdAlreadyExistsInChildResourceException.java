package dsenta.cachito.exception;

public class IdAlreadyExistsInChildResourceException extends IllegalArgumentException {
    public IdAlreadyExistsInChildResourceException(Long id, String childResource) {
        super(String.format("ID %d already exists in child resource %s", id, childResource));
    }
}