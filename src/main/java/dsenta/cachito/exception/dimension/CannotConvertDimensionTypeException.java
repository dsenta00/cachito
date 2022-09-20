package dsenta.cachito.exception.dimension;

import dsenta.cachito.model.attribute.DataType;

public class CannotConvertDimensionTypeException extends RuntimeException {

    public CannotConvertDimensionTypeException(DataType from, DataType to) {
        super(String.format("Cannot convert dimension type from %s to %s", from, to));
    }
}