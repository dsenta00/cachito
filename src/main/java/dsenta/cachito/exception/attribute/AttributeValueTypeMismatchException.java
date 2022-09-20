package dsenta.cachito.exception.attribute;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;

public class AttributeValueTypeMismatchException extends IllegalArgumentException {
    public AttributeValueTypeMismatchException(Attribute attribute, Object object) {
        super(String.format(
                "The value of attribute %s should be of type %s, got %s",
                attribute.getName(),
                attribute.getDataType(),
                object
        ));
    }

    public AttributeValueTypeMismatchException(DataType type, Object object) {
        super(String.format(
                "The value of attribute should be of type %s, got %s",
                type,
                object
        ));
    }
}