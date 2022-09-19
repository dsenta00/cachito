package dsenta.cachito.exception;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;

public class CannotChangeRelationshipResourceException extends IllegalArgumentException {

    public CannotChangeRelationshipResourceException(Attribute attribute, Clazz clazz) {
        super(String.format(
                "Cannot change relationship from resource %s to %s for attribute %s ",
                attribute.getClazz().getName(),
                clazz.getName(),
                attribute
        ));
    }
}