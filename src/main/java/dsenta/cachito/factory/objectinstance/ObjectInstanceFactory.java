package dsenta.cachito.factory.objectinstance;

import dsenta.cachito.exception.NoDefaultValueSetForResourceAttributeException;
import dsenta.cachito.mapper.objectinstance.BooleanMapper;
import dsenta.cachito.mapper.objectinstance.DateMapper;
import dsenta.cachito.mapper.objectinstance.FloatMapper;
import dsenta.cachito.mapper.objectinstance.IntMapper;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.objectinstance.ObjectInstance;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

import static dsenta.cachito.model.attribute.DataType.isRelationship;
import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectInstanceFactory {

    public static ObjectInstance create(Clazz clazz, Map<String, Object> object) {
        Collection<Attribute> attributes = clazz.getAttributeCollection();
        ObjectInstance objectInstance = new ObjectInstance(new Object[attributes.size()]);

        for (Attribute attribute : attributes) {
            setAttributeValue(objectInstance, attribute, object);
        }

        return objectInstance;
    }

    public static ObjectInstance patch(ObjectInstance objectInstance, Clazz clazz, Map<String, Object> object) {
        ObjectInstance patchedObjectInstance = objectInstance.deepClone();

        for (Attribute attribute : clazz.getAttributeCollection()) {
            if (object.containsKey(attribute.getName())) {
                setAttributeValue(patchedObjectInstance, attribute, object);
            } else {
                if (!isRelationship(attribute.getDataType())) {
                    patchedObjectInstance.set(attribute, objectInstance.get(attribute));
                }
            }
        }

        return patchedObjectInstance;
    }

    private static void setAttributeValue(ObjectInstance objectInstance, Attribute attribute, Map<String, Object> object) {
        Object value = object.getOrDefault(attribute.getName(), null);

        if (isNull(value)) {
            if (isNull(attribute.getDefaultValue()) && isNull(attribute.getClazz())) {
                throw new NoDefaultValueSetForResourceAttributeException(attribute);
            }

            switch (attribute.getDataType()) {
                case DATE:
                    value = DateMapper.toDate(attribute.getDefaultValue());
                    break;
                case INTEGER:
                    value = IntMapper.toLongInt(attribute.getDefaultValue());
                    break;
                case FLOAT:
                    value = FloatMapper.toDouble(attribute.getDefaultValue());
                    break;
                case BOOLEAN:
                    value = BooleanMapper.toBoolean(attribute.getDefaultValue());
                    break;
                default:
                    value = attribute.getDefaultValue();
                    break;
            }
        } else {
            switch (attribute.getDataType()) {
                case DATE:
                    value = DateMapper.toDate(value);
                    break;
                case INTEGER:
                    value = IntMapper.toLongInt(value);
                    break;
                case FLOAT:
                    value = FloatMapper.toDouble(value);
                    break;
                case BOOLEAN:
                    value = BooleanMapper.toBoolean(value);
                    break;
            }
        }

        object.put(attribute.getName(), value);
        objectInstance.setUnsafe(attribute, value);
    }
}