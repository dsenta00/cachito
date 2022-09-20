package dsenta.cachito.exception.resource;

import dsenta.cachito.model.schema.Schema;

public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException(String resource) {
        super(String.format("Resource %s does not exist", resource));
    }

    public ResourceNotFoundException(String resource, Schema schema) {
        super(String.format(
                "Resource %s does not exist in schema %s",
                resource,
                schema.getName()
        ));
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("Resource %s does not contain entity with id %d", resource, id));
    }

    public ResourceNotFoundException(String resource, String value) {
        super(String.format("Resource %s does not contain entity with value %s", resource, value));
    }
}
