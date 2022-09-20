package dsenta.cachito.exception.attribute;

import dsenta.cachito.model.attribute.Attribute;

public class CannotAlterOverriddenAttributeException extends IllegalArgumentException {

    public CannotAlterOverriddenAttributeException(Attribute attribute) {
        super(String.format("Cannot alter overridden attribute %s", attribute.getName()));
    }
}