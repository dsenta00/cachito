package dsenta.cachito.exception.attribute;

import dsenta.cachito.model.attribute.Attribute;

public class AttributeNotProvidedException extends IllegalArgumentException {

    public AttributeNotProvidedException(Attribute attribute) {
        super(String.format("Attribute %s not provided", attribute.getName()));
    }
}