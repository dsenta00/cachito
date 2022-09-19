package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class NoDefaultValueSetForResourceAttributeException extends RuntimeException {

    public NoDefaultValueSetForResourceAttributeException(Attribute attribute) {
        super(String.format("No default value set for resource attribute %s", attribute.getName()));
    }
}