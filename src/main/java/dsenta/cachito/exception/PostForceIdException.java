package dsenta.cachito.exception;

public class PostForceIdException extends IllegalArgumentException {

    public PostForceIdException(Long id, String name) {
        super(String.format("Cannot force persisting with id %d for object type %s", id, name));
    }
}