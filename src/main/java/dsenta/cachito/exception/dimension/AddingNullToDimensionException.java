package dsenta.cachito.exception.dimension;

import dsenta.cachito.model.attribute.Attribute;

public class AddingNullToDimensionException extends RuntimeException {

    public AddingNullToDimensionException(Attribute attribute) {
        super(String.format("Cannot add null to dimension for attribute %s", attribute.getName()));
    }
}