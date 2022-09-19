package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class CannotAddRequiredRelationshipToTheSameResourceException extends IllegalArgumentException {

    public CannotAddRequiredRelationshipToTheSameResourceException(Attribute attribute) {
        super(String.format(
                "Cannot add required relationship (%s) to the resource of the same type (%s)",
                attribute.getDataType(),
                attribute.getClazz().getName()
        ));
    }
}