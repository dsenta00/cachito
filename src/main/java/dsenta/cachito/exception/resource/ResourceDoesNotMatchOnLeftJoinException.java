package dsenta.cachito.exception.resource;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;

public class ResourceDoesNotMatchOnLeftJoinException  extends IllegalArgumentException {

    public ResourceDoesNotMatchOnLeftJoinException(Clazz clazz, Attribute leftJoinedAttribute) {
        super(String.format(
                "Resource %s does not match on left joined resource. Attribute %s is of the type %s.",
                clazz.getName(),
                leftJoinedAttribute.getName(),
                leftJoinedAttribute.getClazz().getName()
        ));
    }
}