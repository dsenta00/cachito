package dsenta.cachito.assertions.attribute;

import dsenta.cachito.cache.clazz.ClazzCache;
import dsenta.cachito.exception.*;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.Persistence;
import dsenta.cachito.repository.resource.PersistableResource;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static dsenta.cachito.model.attribute.DataType.RELATIONSHIP_ZERO_MANY;
import static dsenta.cachito.model.attribute.DataType.STRING;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AttributeAssert {

    public static final String NOW = "now";

    public static void canAddAttributes(Clazz clazz, List<Attribute> fieldsToAdd, Persistence persistence) {
        AttributeDuplicationAssert.assertDuplications(clazz, fieldsToAdd);

        for (Attribute fieldToAdd : fieldsToAdd) {
            canAddAttribute(clazz, fieldToAdd, persistence);
        }
    }

    public static void canAddAttribute(Clazz clazz, Attribute fieldToAdd, Persistence persistence) {
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
            case RELATIONSHIP_ZERO_ONE:
            case RELATIONSHIP_ONE:
            case RELATIONSHIP_ZERO_MANY:
            case RELATIONSHIP_ONE_MANY:
                canCreateRelationshipAttribute(clazz, fieldToAdd, persistence);
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

    public static void canUpdateAttributes(Clazz clazz,
                                           Map<String, Attribute> fieldsToUpdate,
                                           Persistence persistence) {
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
                case RELATIONSHIP_ZERO_ONE:
                    switch (targetUpdate.getDataType()) {
                        case RELATIONSHIP_ZERO_ONE:
                        case RELATIONSHIP_ZERO_MANY:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }

                    canUpdateRelationshipAttribute(clazz, targetUpdate, attribute, persistence);
                    break;
                case RELATIONSHIP_ONE:
                    switch (targetUpdate.getDataType()) {
                        case RELATIONSHIP_ZERO_ONE:
                        case RELATIONSHIP_ZERO_MANY:
                        case RELATIONSHIP_ONE:
                        case RELATIONSHIP_ONE_MANY:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }

                    canUpdateRelationshipAttribute(clazz, targetUpdate, attribute, persistence);
                    break;
                case RELATIONSHIP_ZERO_MANY:
                    if (!targetUpdate.getDataType().equals(RELATIONSHIP_ZERO_MANY)) {
                        throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }

                    canUpdateRelationshipAttribute(clazz, targetUpdate, attribute, persistence);
                    break;
                case RELATIONSHIP_ONE_MANY:
                    switch (targetUpdate.getDataType()) {
                        case RELATIONSHIP_ZERO_MANY:
                        case RELATIONSHIP_ONE_MANY:
                            break;
                        default:
                            throw new CannotPromoteDataTypeException(attribute, targetUpdate.getDataType());
                    }

                    canUpdateRelationshipAttribute(clazz, targetUpdate, attribute, persistence);
                    break;
            }
        }
    }

    public static void canCreateRelationshipAttribute(Clazz clazz,
                                                      Attribute targetCreate,
                                                      Persistence persistence) {
        if (isNull(targetCreate.getClazz())) {
            throw new NotDefinedResourceForRelationshipException(targetCreate);
        }

        if (targetCreate.getClazz().compareTo(clazz) == 0) {
            switch (targetCreate.getDataType()) {
                case RELATIONSHIP_ZERO_ONE:
                case RELATIONSHIP_ZERO_MANY:
                    break;
                default:
                    throw new CannotAddRequiredRelationshipToTheSameResourceException(targetCreate);
            }
        } else {
            if (targetCreate.isUnique()) {
                throw new CannotAddUniqueConstraintToRelationshipException(targetCreate);
            }

            PersistableResource.get(targetCreate.getClazz(), persistence);
        }
    }

    public static void canUpdateRelationshipAttribute(Clazz clazz,
                                                      Attribute targetUpdate,
                                                      Attribute attribute,
                                                      Persistence persistence) {
        if (isNull(targetUpdate.getClazz())) {
            throw new NotDefinedResourceForRelationshipException(targetUpdate);
        }

        if (targetUpdate.getClazz().compareTo(attribute.getClazz()) != 0) {
            throw new CannotChangeRelationshipResourceException(attribute, targetUpdate.getClazz());
        }

        if (targetUpdate.getClazz().compareTo(clazz) == 0) {
            switch (targetUpdate.getDataType()) {
                case RELATIONSHIP_ZERO_ONE:
                case RELATIONSHIP_ZERO_MANY:
                    break;
                default:
                    throw new CannotAddRequiredRelationshipToTheSameResourceException(targetUpdate);
            }
        } else {
            PersistableResource.get(targetUpdate.getClazz(), persistence);
        }
    }

    public static void canAlterParentClazz(Clazz parentClazz,
                                           List<Attribute> fieldsToAdd,
                                           Map<String, Attribute> fieldsToUpdate) {
        if (isNull(parentClazz)) {
            return;
        }

        canAlterParentClazz(parentClazz.getParentClazz(), fieldsToAdd, fieldsToUpdate);

        for (Attribute attribute : parentClazz.getAttributeCollection()) {
            if (fieldsToUpdate.containsKey(attribute.getName())) {
                var fieldUpdateTarget = fieldsToUpdate.get(attribute.getName());

                if (!fieldUpdateTarget.getDataType().equals(attribute.getDataType())) {
                    throw new CannotPromoteDataTypeException(attribute, fieldUpdateTarget.getDataType());
                }
            }

            canPromoteDataType(fieldsToAdd, attribute);
        }
    }

    public static void canAlterChildClazzes(Clazz clazz,
                                            Map<String, Attribute> fieldsToUpdate,
                                            List<String> fieldsToDelete,
                                            List<Attribute> fieldsToAdd) {
        var childClazzes = ClazzCache.stream().getChildClazzes(clazz);
        for (Clazz childClazz : childClazzes) {
            for (Attribute attribute : childClazz.getAttributeCollection()) {
                if (fieldsToDelete.contains(attribute.getName())) {
                    throw new CannotAlterOverriddenAttributeException(attribute);
                }

                if (fieldsToUpdate.containsKey(attribute.getName())) {
                    Attribute fieldUpdateTarget = fieldsToUpdate.get(attribute.getName());

                    if (!fieldUpdateTarget.getName().equals(attribute.getName())) {
                        throw new CannotAlterOverriddenAttributeException(attribute);
                    }

                    if (!fieldUpdateTarget.getDataType().equals(attribute.getDataType())) {
                        throw new CannotPromoteDataTypeException(attribute, fieldUpdateTarget.getDataType());
                    }
                }

                canPromoteDataType(fieldsToAdd, attribute);
            }
        }
    }

    private static void canPromoteDataType(List<Attribute> fieldsToAdd, Attribute attribute) {
        fieldsToAdd.stream()
                .filter(targetUpdate -> attribute.getName().equals(targetUpdate.getName()))
                .findFirst()
                .map(Attribute::getDataType)
                .ifPresent(type -> {
                    if (!type.equals(attribute.getDataType())) {
                        throw new CannotPromoteDataTypeException(attribute, type);
                    }
                });
    }
}
