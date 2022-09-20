package dsenta.cachito.assertions.attribute;

import dsenta.cachito.exception.attribute.AttributeDoesNotExistException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static dsenta.cachito.assertions.attribute.AttributeAssert.NOW;
import static dsenta.cachito.model.attribute.DataType.STRING;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AttributeAssert_Simple {

    public static void canAddAttributes(Clazz clazz, List<Attribute> fieldsToAdd) {
        AttributeDuplicationAssert.assertDuplications(clazz, fieldsToAdd);

        for (Attribute fieldToAdd : fieldsToAdd) {
            canAddAttribute(fieldToAdd);
        }
    }

    public static void canAddAttribute(Attribute fieldToAdd) {
        switch (fieldToAdd.getDataType()) {
            case BOOLEAN:
                if (isNull(fieldToAdd.getDefaultValue())) {
                    fieldToAdd.setDefaultValue(false);
                }
                break;
            case INTEGER:
                if (isNull(fieldToAdd.getDefaultValue())) {
                    fieldToAdd.setDefaultValue(0L);
                }
                break;
            case FLOAT:
                if (isNull(fieldToAdd.getDefaultValue())) {
                    fieldToAdd.setDefaultValue(0.0);
                }
                break;
            case STRING:
                if (isNull(fieldToAdd.getDefaultValue())) {
                    fieldToAdd.setDefaultValue("");
                }
                break;
            case DATE:
                if (isNull(fieldToAdd.getDefaultValue())) {
                    fieldToAdd.setDefaultValue(NOW);
                }
                break;
        }
    }

    public static void canDeleteAttributes(Clazz clazz, List<String> fieldsToDelete) {
        Map<String, Attribute> clazzFieldMap = clazz.getAttributes();
        for (String fieldNameToDelete : fieldsToDelete) {
            if (!clazzFieldMap.containsKey(fieldNameToDelete)) {
                throw new AttributeDoesNotExistException(fieldNameToDelete);
            }
        }
    }

    public static void canUpdateAttributes(Clazz clazz, Map<String, Attribute> fieldsToUpdate) {
        Map<String, Attribute> clazzFieldMap = clazz.getAttributes();

        for (var entry : fieldsToUpdate.entrySet()) {
            String name = entry.getKey();
            Attribute targetUpdate = entry.getValue();

            if (!clazzFieldMap.containsKey(name)) {
                throw new AttributeDoesNotExistException(name);
            }

            Attribute attribute = clazzFieldMap.get(name);

            switch (attribute.getDataType()) {
                case BOOLEAN:
                    switch (targetUpdate.getDataType()) {
                        case BOOLEAN:
                        case INTEGER:
                        case FLOAT:
                        case STRING:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }
                    break;
                case INTEGER:
                    switch (targetUpdate.getDataType()) {
                        case INTEGER:
                        case FLOAT:
                        case STRING:
                        case DATE:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }
                    break;
                case FLOAT:
                    switch (targetUpdate.getDataType()) {
                        case FLOAT:
                        case STRING:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }
                    break;
                case STRING:
                    if (!targetUpdate.getDataType().equals(STRING)) {
                        throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }
                    break;
                case DATE:
                    switch (targetUpdate.getDataType()) {
                        case INTEGER:
                        case DATE:
                        case STRING:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }
                    break;
            }
        }
    }
}
