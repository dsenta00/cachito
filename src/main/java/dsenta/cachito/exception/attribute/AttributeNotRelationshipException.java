package dsenta.cachito.exception.attribute;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;

public class AttributeNotRelationshipException extends IllegalArgumentException {

    public AttributeNotRelationshipException(Attribute attribute) {
        super(String.format("Attribute %s is not a relationship (%s)", attribute.getName(), attribute.getDataType()));
    }

    public AttributeNotRelationshipException(String attribute, DataType dataType) {
        super(String.format("Attribute %s is not a relationship (%s)", attribute, dataType));
    }
}