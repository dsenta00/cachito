package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;

public class RelationshipOneManyNoObjectException extends IllegalArgumentException {

    public RelationshipOneManyNoObjectException(Attribute attribute) {
        super(String.format(
                "Relationship attribute %s should have at least one object (%s)",
                attribute.getName(),
                attribute.getDataType()
        ));
    }
}