package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class CannotAddUniqueConstraintToRelationshipException extends IllegalArgumentException {

    public CannotAddUniqueConstraintToRelationshipException(Attribute attribute) {
        super(String.format("Cannot add unique constraint to relationship attribute %s", attribute.getName()));
    }
}