package dsenta.cachito.assertions.attribute;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;

public class CannotPromoteDataTypeException extends IllegalArgumentException {

    public CannotPromoteDataTypeException(Attribute attribute, DataType dataType) {
        super(String.format(
                "Cannot promote data type of attribute %s from %s to %s",
                attribute.getName(),
                attribute.getDataType(),
                dataType
        ));
    }
}