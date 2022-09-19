package dsenta.cachito.mapper.attribute;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Operator;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import dsenta.queryablemap.exception.ShouldNeverHappenException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AndWhereMapper {

    public static AndWhere toAndWhereEqualsForConstraint(Attribute attribute, ObjectInstance objectInstance) {
        switch (attribute.getDataType()) {
            case BOOLEAN:
            case INTEGER:
            case FLOAT:
            case STRING:
            case DATE:
                return new AndWhere(attribute.getName(), Operator.EQUALS, objectInstance.get(attribute));
        }

        throw new ShouldNeverHappenException();
    }
}