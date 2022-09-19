package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class DimensionDoesNotExistException extends IllegalArgumentException {
    private static final long serialVersionUID = -2127465998937300629L;

    public DimensionDoesNotExistException(Attribute attribute) {
        this(attribute.getName());
    }

    public DimensionDoesNotExistException(String attribute) {
        super(String.format("Attribute %s is neither filterable nor relationship", attribute));
    }
}