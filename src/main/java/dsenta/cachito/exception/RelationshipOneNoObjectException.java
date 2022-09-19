package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class RelationshipOneNoObjectException extends IllegalArgumentException {

    public RelationshipOneNoObjectException(Attribute attribute) {
        super(String.format(
                "Relationship attribute %s should have exactly one object (%s)",
                attribute.getName(),
                attribute.getDataType()
        ));
    }
}