package dsenta.cachito.exception.attribute;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;

public class CannotOverrideDataTypeException extends IllegalArgumentException {

    public CannotOverrideDataTypeException(Attribute attribute, DataType dataType) {
        super(String.format(
                "Cannot override data type for attribute %s (%s -> %s)",
                attribute.getName(),
                attribute.getDataType(),
                dataType
        ));
    }
}