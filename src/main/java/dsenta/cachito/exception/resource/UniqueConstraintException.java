package dsenta.cachito.exception.resource;

import java.util.Set;

public class UniqueConstraintException extends IllegalArgumentException {

    public UniqueConstraintException(String resource, Set<String> fields) {
        super(String.format("Unique constraint on resource %s for fields %s", resource, fields));
    }
}
