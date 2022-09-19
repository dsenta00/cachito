package dsenta.cachito.exception;

public class EntityNotFoundException extends IllegalArgumentException {

    public EntityNotFoundException(Long id) {
        super(String.format("Entity with id %d doesn't exist!", id));
    }
}