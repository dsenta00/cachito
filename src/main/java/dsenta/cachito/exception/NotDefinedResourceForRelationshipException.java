package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class NotDefinedResourceForRelationshipException extends IllegalArgumentException {

    public NotDefinedResourceForRelationshipException(Attribute attribute) {
        super(String.format("Not defined resource for relationship attribute %s", attribute.getName()));
    }
}